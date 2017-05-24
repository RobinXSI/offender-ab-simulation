package agent;

import core.Simulator;
import graph.Intersection;
import org.postgis.Point;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Agent {
    private Point actualLocation;

    public Agent(Point actualLocation) {
        this.actualLocation = actualLocation;
    }

    public void step() throws SQLException {
        double searchRadius = getSearchRadius();
        
        List<Intersection> goal = findGoals(searchRadius);










        // move to goal
        // log the movement
    }

    private List<Intersection> findGoals(double radius) throws SQLException {
        List<Intersection> goal = findGoalsInDistance(radius, radius * 0.9);

        if (goal.isEmpty()) {
            goal = findGoalsInDistance(radius * 1.2, radius * 0.8);
        }

        if (goal.isEmpty()) {
            throw new RuntimeException("No goal could be found");
        }

        return goal;
    }



    private List<Intersection> findGoalsInDistance(double radiusFrom, double radiusTo) throws SQLException {
        int srid = Simulator.SRID;

        String location = actualLocation.toString();
        System.out.println(location);

        Connection c = Simulator.get().getSqlConnection();
        PreparedStatement preparedStatement = c.prepareStatement("SELECT * FROM intersection " +
                "WHERE ST_DWithin(ST_GeomFromText(?), intersection.point, ?) " +
                "AND NOT ST_DWithin(ST_GeomFromText(?), intersection.point, ?);");

        System.out.println(preparedStatement.toString());

        preparedStatement.setString(1, actualLocation.toString());
        preparedStatement.setDouble(2, radiusTo);
        preparedStatement.setString(3, actualLocation.toString());
        preparedStatement.setDouble(4, radiusFrom);

        ResultSet resultSet = preparedStatement.executeQuery();

        List<Intersection> intersections = new ArrayList<>();

        while (resultSet.next()) {
            Intersection intersection = new Intersection(resultSet);
            intersections.add(intersection);
        }

        return intersections;
    }

    private double getSearchRadius() {
        double staticRadius = Simulator.STATIC_RADIUS;
        return staticRadius;
    }

}
