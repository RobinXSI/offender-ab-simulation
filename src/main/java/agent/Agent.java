package agent;

import core.Simulator;
import graph.Intersection;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.postgis.Point;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Agent {
    private Intersection actualLocation;

    public Agent(Intersection actualLocation) {
        this.actualLocation = actualLocation;
    }

    public void step() throws SQLException {
        double searchRadius = getSearchRadius();

        List<Intersection> goals = findGoals(searchRadius);

        Intersection goal = selectRandomGoal(goals);

        GraphPath<Intersection, DefaultWeightedEdge> path = getPath(goal);

        moveToGoal(path);

        this.actualLocation = goal;


        // move to goal
        // log the movement
    }

    private void moveToGoal(GraphPath<Intersection, DefaultWeightedEdge> path) {
        System.out.println(path.getWeight());
        System.out.println(path.getLength());


    }

    private GraphPath<Intersection, DefaultWeightedEdge> getPath(Intersection goal) {
        WeightedGraph<Intersection, DefaultWeightedEdge> graph = Simulator.get().getGraph();
        DijkstraShortestPath<Intersection, DefaultWeightedEdge> dijkstraAlgorithm = new DijkstraShortestPath<>(graph);

        Intersection actualVertex = Simulator.get().getIntersectionById(actualLocation.getId());
        Intersection goalVertex = Simulator.get().getIntersectionById(goal.getId());

        GraphPath<Intersection, DefaultWeightedEdge> path = dijkstraAlgorithm.getPath(actualVertex, goalVertex);

        return path;
    }

    private Intersection selectRandomGoal(List<Intersection> goals) {
        int maxIndex = goals.size();
        Random random = new Random();
        int chosenIndex = random.nextInt(maxIndex);

        Intersection intersection = goals.get(chosenIndex);
        return intersection;
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
        String location = actualLocation.toString();
        System.out.println(location);

        Connection c = Simulator.get().getSqlConnection();
        PreparedStatement preparedStatement = c.prepareStatement("SELECT * FROM intersection " +
                "WHERE ST_DWithin(ST_GeomFromText(?), intersection.point, ?) " +
                "AND NOT ST_DWithin(ST_GeomFromText(?), intersection.point, ?);");


        preparedStatement.setString(1, actualLocation.getPoint().toString());
        preparedStatement.setDouble(2, radiusFrom);
        preparedStatement.setString(3, actualLocation.getPoint().toString());
        preparedStatement.setDouble(4, radiusTo);

        System.out.println(preparedStatement.toString());

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
