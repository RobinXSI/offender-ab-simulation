import shapefile
from geoalchemy2.shape import from_shape

from shapely.geometry import Point
from sqlalchemy import create_engine, exc
from setup_database import Intersection
from sqlalchemy.orm import sessionmaker


engine = create_engine('postgresql://robin@localhost/geo-ny', echo=False)
engine.connect()


Session = sessionmaker(bind=engine)

session = Session()

sf = shapefile.Reader('./data/ny/roads/NYC_road_proj')

shapes = sf.shapes()

successfully_added = 0
not_added = 0

for shapeRecord in sf.iterShapeRecords():
    first_point = shapeRecord.shape.points[0]
    last_point = shapeRecord.shape.points[-1]

    first_point = Point(first_point[0], first_point[1])
    last_point = Point(last_point[0], last_point[1])


    if (first_point != last_point):
        try:
            intersection_from = Intersection(point=from_shape(first_point, srid=2263))
            session.add(intersection_from)
            session.commit()
            successfully_added = successfully_added + 1
        except exc.IntegrityError:
            session.rollback()
            not_added = not_added + 1
            pass

        try:
            intersection_to = Intersection(point=from_shape(last_point, srid=2263))
            session.add(intersection_to)
            session.commit()
            successfully_added = successfully_added + 1
        except exc.IntegrityError:
            session.rollback()
            not_added = not_added + 1
            pass

print("Successfully added: {}".format(successfully_added))
print("Double entires: {}".format(not_added))
# Successfully added: 75876
# Double entires: 158654
