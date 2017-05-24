package graph;

import org.postgis.PGgeometry;
import org.postgis.Point;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Intersection {
    int id;
    Point point;

    public Intersection(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("id");
        this.point = (Point) ((PGgeometry) resultSet.getObject("point")).getGeometry();
    }

    public int getId() {
        return id;
    }

    public Point getPoint() {
        return point;
    }
}
