import shapefile
from geoalchemy2.shape import from_shape, to_shape

from shapely.geometry import Point, LineString
from sqlalchemy import create_engine, exc, func, or_
from setup_database import Intersection, Road, Crime
from sqlalchemy.orm import sessionmaker

engine = create_engine('postgresql://robin@localhost/geo-ny', echo=True)
engine.connect()

Session = sessionmaker(bind=engine)

session = Session()

sf = shapefile.Reader('./data/ny/crimes/NYC_crime2015_proj')

shapes = sf.shapes()
print(sf.fields)
print(sf.fields[12])  # offense

successfully_added = 0
not_added = 0

for shapeRecord in sf.iterShapeRecords():
    offense = shapeRecord.record[12]
    # print(offense)

    point = shapeRecord.shape.points[0]
    point = Point(point[0], point[1])

    query = session.query(Road).order_by(Road.line.distance_box(from_shape(point, srid=2263))).limit(1)

    closest_road = query[0]
    road_id = closest_road.id
    # print(point.distance(to_shape(closest_road.line)))

    try:
        crime = Crime(crime_type=offense, road_id=road_id, point=from_shape(point, srid=2263))
        session.add(crime)
        session.commit()
        successfully_added = successfully_added + 1
    except exc.IntegrityError:
        session.rollback()
        not_added = not_added + 1
        pass

print("Successfully added: {}".format(successfully_added))
print("Not added: {}".format(not_added))
# Successfully added: 54009
# Not added: 0
