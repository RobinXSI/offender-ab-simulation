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
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Agent {
    private Intersection actualLocation;
    private int id;

    private RadiusType radiusType;
    private AgentType agentType;
    private Random random = new Random();

    public Agent(int id, Intersection actualLocation, RadiusType radiusType, AgentType agentType) {
        this.id = id;
        this.actualLocation = actualLocation;
        this.radiusType = radiusType;
        this.agentType = agentType;
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


            String sql = null;

            switch (agentType) {
                case INTERSECTION:
                    sql = "SELECT * FROM intersection " +
                            "WHERE ST_DWithin(ST_GeomFromText(?), intersection.point, ?) " +
                            "AND NOT ST_DWithin(ST_GeomFromText(?), intersection.point, ?);";
                    break;
                case VENUE:
                    sql = "SELECT intersection.id, intersection.point FROM venue " +
                            "JOIN road ON venue.road_id  = road.id " +
                            "JOIN intersection ON road.from_id = intersection.id OR road.to_id = intersection.id " +
                            "WHERE ST_DWithin(ST_GeomFromText(?), venue.point, ?) " +
                            "AND NOT ST_DWithin(ST_GeomFromText(?), venue.point, ?);";

                    break;
                case VENUE_PRIORITY:
                    sql = "SELECT intersection.id, intersection.point FROM venue " +
                            "JOIN road ON venue.road_id  = road.id " +
                            "JOIN intersection ON road.from_id = intersection.id OR road.to_id = intersection.id " +
                            "WHERE ST_DWithin(ST_GeomFromText(?), venue.point, ?) " +
                            "AND NOT ST_DWithin(ST_GeomFromText(?), venue.point, ?)" +
                            "ORDER BY venue.checkins_counter DESC;";
                    break;
                default:
                    throw new NotImplementedException();
            }

            List<Intersection> goals = findGoalsIntersection(searchRadius, sql);

            if (agentType == AgentType.VENUE_PRIORITY) {
                goal = selectRandomGoalPrioritized(goals);
            } else {
                goal = selectRandomGoal(goals);
            }



            path = getPath(goal);

            counter++;
        }

        if (counter == Simulator.NUMBER_OF_TRIES_TO_RESET_LOCATION) {
            Geography geography = new Geography();
            this.actualLocation = geography.getRandomIntersection();
            this.step(stepStatistics);
            return;
        }

        if (path == null || path.getVertexList() == null) {

            System.out.println();
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
            System.out.println("CHECK ROAD MISMATCHES");

        }

//        assert path.getLength() == roads.size();
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

    private Intersection selectRandomGoalPrioritized(List<Intersection> goals) {
        int maxIndex = (int) Math.floor(goals.size() / 5.0);
        Random random = new Random();
        int chosenIndex = random.nextInt(maxIndex) / 2;

        Intersection intersection = goals.get(chosenIndex);
        return intersection;
    }

    private Intersection selectRandomGoal(List<Intersection> goals) {
        int maxIndex = goals.size();
        Random random = new Random();
        int chosenIndex = random.nextInt(maxIndex);

        Intersection intersection = goals.get(chosenIndex);
        return intersection;
    }

    private List<Intersection> findGoalsIntersection(double radius, String sql) throws SQLException {
        List<Intersection> goal = findGoalsInDistance(radius, radius * 0.9, sql);

        if (goal.isEmpty()) {
            goal = findGoalsInDistance(radius * 1.2, radius * 0.8, sql);
        }

        if (goal.isEmpty()) {
            throw new RuntimeException("No goal could be found");
        }

        return goal;
    }

    private List<Intersection> findGoalsInDistance(double radiusFrom, double radiusTo, String sql) throws SQLException {
        Connection c = Simulator.get().getSqlConnection();
        PreparedStatement preparedStatement = c.prepareStatement(sql);


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
        switch (radiusType) {
            case STATIC:
                double staticRadius = Simulator.STATIC_RADIUS;
                return staticRadius;
            case UNIFORM:
                double uniformUpperBound = Simulator.STATIC_RADIUS * 2;
                return random.nextInt((int) uniformUpperBound); // TODO is double needed or int ok?
            case LEVY:

                double b = 1.59;
                //mnimal travel distance in km for NYC is 2.5 km
                double dmin = 2.5;
                //maximal distance ( length) for NYC
                double dmax = 530;
                double pmax = Math.pow(dmin, -1.6);
                double pmin = Math.pow(dmax, -1.6);

                // TODO Use better random generator
                double difference = pmax - pmin;
                double uniformProb = random.nextDouble() * difference + pmin;

//                double uniformProb = uniformDistribution.nextDoubleFromTo(pmin, pmax);
                //levy flight: P(x) = Math.pow(x, -1.59) - find out x? given random probability within range
                double levyflight = (1/uniformProb) * Math.exp(1/b);
                //levy flight gives distance in km - transform km to foot
                double levyradius = levyflight * 3280.84;
                return levyradius;
            default:
                return 0;
        }
    }

}
