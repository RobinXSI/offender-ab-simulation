package graph;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Road {
    private int id;
    private double roadLength;
    private int fromId;
    private int toId;

    public Road(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getInt("id");
        this.roadLength = resultSet.getDouble("road_length");
        this.toId = resultSet.getInt("to_id");
        this.fromId = resultSet.getInt("from_id");
    }

    public int getId() {
        return id;
    }

    public double getRoadLength() {
        return roadLength;
    }

    public int getFromId() {
        return fromId;
    }

    public int getToId() {
        return toId;
    }
}
