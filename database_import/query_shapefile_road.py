import shapefile
from geoalchemy2.shape import from_shape, to_shape

from shapely.geometry import Point, LineString
from sqlalchemy import create_engine, exc, func, or_
from setup_database import Intersection, Road
from sqlalchemy.orm import sessionmaker

engine = create_engine('postgresql://robin@localhost/geo-ny', echo=True)
engine.connect()

Session = sessionmaker(bind=engine)

session = Session()

sf = shapefile.Reader('./data/ny/roads/NYC_road_proj')

shapes = sf.shapes()
print(sf.fields)
print(sf.fields[12])

successfully_added = 0
not_added = 0
self_loop = 0

for shapeRecord in sf.iterShapeRecords():
    length = shapeRecord.record[12 - 1]
    # print(length)

    first_point = shapeRecord.shape.points[0]
    last_point = shapeRecord.shape.points[-1]

    first_point = Point(first_point[0], first_point[1])
    last_point = Point(last_point[0], last_point[1])

    if (first_point != last_point):
        query = session.query(Intersection).filter(
            or_(func.ST_contains(Intersection.point, from_shape(first_point, srid=2263)),
                func.ST_contains(Intersection.point, from_shape(last_point, srid=2263)))
        )

        try:

            incident_intersections = list(query)
            if len(incident_intersections) != 2 or incident_intersections[0] == incident_intersections[1]:
                if len(incident_intersections) == 0:
                    raise AssertionError('no results')
                else:
                    raise AssertionError('More or less than two results')

            if incident_intersections[0].id < incident_intersections[1].id:
                from_id = incident_intersections[0].id
                to_id = incident_intersections[1].id
                first_point = to_shape(incident_intersections[0].point)
                last_point = to_shape(incident_intersections[1].point)
            else:
                from_id = incident_intersections[1].id
                to_id = incident_intersections[0].id
                first_point = to_shape(incident_intersections[1].point)
                last_point = to_shape(incident_intersections[0].point)

            line = LineString([first_point, last_point])


            road = Road(line=from_shape(line, srid=2263), road_length=length, from_id=from_id, to_id=to_id)
            session.add(road)
            session.commit()
            successfully_added = successfully_added + 1
        except exc.IntegrityError:
            session.rollback()
            not_added = not_added + 1
            pass
        except AssertionError:
            not_added = not_added + 1
            self_loop = self_loop + 1
            pass

session.commit()
print("Successfully added: {}".format(successfully_added))
print("Not added: {}".format(not_added))
print("Self loop: {}".format(self_loop))
# Successfully added: 116585
# Not added: 680
# Self loop: 120

