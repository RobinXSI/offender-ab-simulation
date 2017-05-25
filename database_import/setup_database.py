from sqlalchemy import create_engine, ForeignKey, UniqueConstraint
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, DECIMAL
from geoalchemy2 import Geometry
from sqlalchemy.orm import relationship

engine = create_engine('postgresql://robin@localhost/geo-ny', echo=True)
engine.connect()

Base = declarative_base()


class Intersection(Base):
    __tablename__ = 'intersection'
    id = Column(Integer, primary_key=True)
    point = Column(Geometry('POINT'), nullable=False, unique=True)


class Road(Base):
    __tablename__ = 'road'
    id = Column(Integer, primary_key=True)
    road_length = Column(DECIMAL, nullable=False)
    from_id = Column(Integer, ForeignKey('intersection.id'), nullable=False)
    to_id = Column(Integer, ForeignKey('intersection.id'), nullable=False)
    line = Column(Geometry('LINESTRING'), nullable=False, unique=True)

    from_intersection = relationship('Intersection', foreign_keys=[from_id])
    to_intersection = relationship('Intersection', foreign_keys=[to_id])

    __table_args__ = (UniqueConstraint('from_id', 'to_id', name='_to_from_unique'),
                      )

class Crime(Base):
    __tablename__ = 'crime'
    id = Column(Integer, primary_key=True)
    road_id = Column(Integer, ForeignKey('road.id'), nullable=False)
    crime_type = Column(String)
    point = Column(Geometry('POINT'), nullable=False)

    road = relationship('Road', back_populates='crimes')


Road.crimes = relationship('Crime', back_populates='road')

class Venue(Base):
    __tablename__ = 'venue'
    id = Column(Integer, primary_key=True)
    road_id = Column(Integer, ForeignKey('road.id'), nullable=False)
    venue_type = Column(String)
    point = Column(Geometry('POINT'), nullable=False)
    checkins_counter = Column(Integer, nullable=False)

    road = relationship('Road', back_populates='venues')

Road.venues = relationship('Venue', back_populates='road')

if __name__ == "__main__":
    if engine.dialect.has_table(engine, 'venue'):
        Venue.__table__.drop(engine)
    if engine.dialect.has_table(engine, 'crime'):
        Crime.__table__.drop(engine)

    if engine.dialect.has_table(engine, 'road'):
        Road.__table__.drop(engine)

    if engine.dialect.has_table(engine, 'intersection'):
        Intersection.__table__.drop(engine)

    Intersection.__table__.create(engine)
    Road.__table__.create(engine)
    Crime.__table__.create(engine)
    Venue.__table__.create(engine)

