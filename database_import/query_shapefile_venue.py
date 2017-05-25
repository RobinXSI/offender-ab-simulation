import shapefile
from geoalchemy2.shape import from_shape, to_shape

from shapely.geometry import Point, LineString
from sqlalchemy import create_engine, exc, func, or_
from setup_database import Intersection, Road, Crime, Venue
from sqlalchemy.orm import sessionmaker

engine = create_engine('postgresql://robin@localhost/geo-ny', echo=True)
engine.connect()

Session = sessionmaker(bind=engine)

session = Session()

sf = shapefile.Reader('./data/ny/venues/nyc_fs_venue_join.dbf')

shapes = sf.shapes()
print(sf.fields)
print(sf.fields[8])  # checkinscouter
print(sf.fields[7])  # parentname

successfully_added = 0
not_added = 0

for shapeRecord in sf.iterShapeRecords():
    checkins_counter = shapeRecord.record[8 - 1]
    venue_type = shapeRecord.record[7 - 1]
    print(checkins_counter)
    print(venue_type)

    point = shapeRecord.shape.points[0]
    point = Point(point[0], point[1])

    query = session.query(Road).order_by(Road.line.distance_box(from_shape(point, srid=2263))).limit(1)

    closest_road = query[0]
    road_id = closest_road.id
    print(point.distance(to_shape(closest_road.line)))

    try:
        venue = Venue(venue_type=venue_type, checkins_counter=checkins_counter, road_id=road_id,
                      point=from_shape(point, srid=2263))
        session.add(venue)
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
