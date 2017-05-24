package graph;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Road {
    int id;
    double road_length;
    int fromId;
    int toId;

    public Road(ResultSet resultSet, HashMap<Integer, Intersection> intersections) throws SQLException {
        this.id = resultSet.getInt("id");
        this.road_length = resultSet.getDouble("road_length");
        this.toId = resultSet.getInt("to_id");
        this.fromId = resultSet.getInt("from_id");
    }
}
