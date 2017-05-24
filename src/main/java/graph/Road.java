package graph;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Road {
    int id;
    double roadLength;
    int fromId;
    int toId;

    public Road(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("id");
        this.roadLength = resultSet.getDouble("road_length");
        this.toId = resultSet.getInt("to_id");
        this.fromId = resultSet.getInt("from_id");
    }
}
