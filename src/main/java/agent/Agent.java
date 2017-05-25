package agent;

import core.Simulator;
import geography.Geography;
import graph.Intersection;
import graph.Road;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import statistics.StepStatistics;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Agent {
    private Intersection actualLocation;
    private int id;

    public Agent(int id, Intersection actualLocation) {
        this.id = id;
        this.actualLocation = actualLocation;
    }

    public int getId() {
        return id;
    }

    public void step(StepStatistics stepStatistics) throws SQLException {
        double searchRadius = getSearchRadius();

        Intersection goal = null;
        GraphPath<Intersection, DefaultWeightedEdge> path = null;


        int counter = 0;
        while (path == null && counter < Simulator.NUMBER_OF_TRIES_TO_RESET_LOCATION) {
            List<Intersection> goals = findGoals(searchRadius);
            goal = selectRandomGoal(goals);
            path = getPath(goal);



            counter++;
        }

        if (counter == Simulator.NUMBER_OF_TRIES_TO_RESET_LOCATION) {
            Geography geography = new Geography();
            this.actualLocation = geography.getRandomIntersection();
            this.step(stepStatistics);
        }

        assert path != null;
        assert path.getVertexList() != null;
        
        moveToGoal(path, stepStatistics);

        this.actualLocation = goal;
    }

    private void moveToGoal(GraphPath<Intersection, DefaultWeightedEdge> path, StepStatistics stepStatistics) throws SQLException {
        List<Intersection> intersectionsOnPath = path.getVertexList();

        String ids = intersectionsOnPath.stream().map(i -> String.valueOf(i.getId())).collect(Collectors.joining(", "));

        String sql = "SELECT road.id, road.road_length, road.to_id, road.from_id, count(venue.id) as venue_count, count(crime.id) as crime_count FROM road " +
                "LEFT OUTER JOIN venue ON (road.id = venue.road_id) " +
                "LEFT OUTER JOIN crime ON (road.id = crime.road_id) " +
                "WHERE from_id IN (" + ids + ") " +
                "AND to_id IN (" + ids + ") " +
                "GROUP BY road.id, road.road_length;";

        Connection c = Simulator.get().getSqlConnection();
        Statement statement = c.createStatement();;
        ResultSet resultSet = statement.executeQuery(sql);

        List<Road> roads = new ArrayList<>();

        while (resultSet.next()) {
            Road road = new Road(resultSet);
            roads.add(road);
            int venueCount = resultSet.getInt("venue_count");
            int crimeCount = resultSet.getInt("crime_count");

            stepStatistics.walkDistance.addValue(road.getRoadLength());
            stepStatistics.numberOfVenues.addValue(venueCount);
            stepStatistics.numberOfCrimes.addValue(crimeCount);
        }

        if (path.getLength() != roads.size()) {
            System.out.println("");

        }

        assert path.getLength() == roads.size();
    }


    private GraphPath<Intersection, DefaultWeightedEdge> getPath(Intersection goal) {
        WeightedGraph<Intersection, DefaultWeightedEdge> graph = Simulator.get().getGraph();
        DijkstraShortestPath<Intersection, DefaultWeightedEdge> dijkstraAlgorithm = new DijkstraShortestPath<>(graph);

        Intersection actualVertex = Simulator.get().getIntersectionById(actualLocation.getId());
        Intersection goalVertex = Simulator.get().getIntersectionById(goal.getId());

        assert actualVertex != null;
        assert goalVertex != null;

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

        Connection c = Simulator.get().getSqlConnection();
        PreparedStatement preparedStatement = c.prepareStatement("SELECT * FROM intersection " +
                "WHERE ST_DWithin(ST_GeomFromText(?), intersection.point, ?) " +
                "AND NOT ST_DWithin(ST_GeomFromText(?), intersection.point, ?);");


        preparedStatement.setString(1, actualLocation.getPoint().toString());
        preparedStatement.setDouble(2, radiusFrom);
        preparedStatement.setString(3, actualLocation.getPoint().toString());
        preparedStatement.setDouble(4, radiusTo);

//        System.out.println(preparedStatement.toString());

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
