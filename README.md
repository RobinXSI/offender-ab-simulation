# Offender Agent based Simulation
## Project setup

The project is split in two parts.
One part consists out of python scripts which are used to get the data out of the shapefiles and into the database.
These scripts run very slow, but they are easy to implement and easy to work with.
The database import needs only to be done one.

TODO: Write Part 2

# Instructions
## Database
The required database is Postgres SQL with Post GIS installed.

### Data
The database can be filled by the python scripts with the corresponding table definitions.
However, the data is to big to store on the github.com servers.

Thats's why you need to copy the files yourself in the corresponding folders.
Follwing files are required:

* ./data/ny/crimes/NYC_crime2015_proj.dbf
* ./data/ny/crimes/NYC_crime2015_proj.prj
* ./data/ny/crimes/NYC_crime2015_proj.qpj
* ./data/ny/crimes/NYC_crime2015_proj.shp
* ./data/ny/crimes/NYC_crime2015_proj.shx
* ./data/ny/roads/NYC_road_proj.dbf
* ./data/ny/roads/NYC_road_proj.prj
* ./data/ny/roads/NYC_road_proj.qpj
* ./data/ny/roads/NYC_road_proj.shp
* ./data/ny/roads/NYC_road_proj.shx
* ./data/ny/venues/nyc_fs_venues_join.dbf
* ./data/ny/venues/nyc_fs_venues_join.prj
* ./data/ny/venues/nyc_fs_venues_join.qpj
* ./data/ny/venues/nyc_fs_venues_join.shp
* ./data/ny/venues/nyc_fs_venues_join.shx

Alternatively there are Exports of the data of the Databases as CSV files. However, the table definitions are recommended to be created by python.

## Python database import
To run the python scripts I used Anaconda with Python 3.6.

Creating a Conda Environment is recommend

Following Packages need to be additionally installed:

* pip install geoalchemy2
* pip install pyshp
* pip install shapely

There are 4 scripts that setup the database. They can be run independently but should be run in the predefined order (see install_db.sh)

* python setup_database.py
* python query_shapefile_intersection.py
* python query_shapefile_road.py
* python query_shapefile_crime.py

To verify the import check in QGIS by connecting to the database.

## Agent Simulation
The agent simulation is implemented with Java.
As prerequisites is the build tool Grade needed.


## TODO
* Better Logging with a Logging Framework
* Parameterization
* Different Radius Types
* Improve random generators
* Improve tests
* Parallelize
* Refactoring

