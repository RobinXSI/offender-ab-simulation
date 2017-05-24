package graph;

import core.Simulator;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphCreator {

    public WeightedGraph<Intersection, DefaultWeightedEdge> createGraph() {
        WeightedGraph<Intersection, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

        HashMap<Integer, Intersection> intersections = null;
        try {
            intersections = getAllIntersections();

            for (Intersection intersection : intersections.values()) {
                graph.addVertex(intersection);
            }

            for (Road road : getAllRoads()) {
                Intersection from = intersections.get(road.fromId);
                Intersection to = intersections.get(road.toId);

                DefaultWeightedEdge edge = graph.addEdge(from, to);


                graph.setEdgeWeight(edge, road.roadLength);
            }

            Simulator.get().setGraph(graph, intersections);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Problems with SQL Query");
        }



        return graph;
    }


    public HashMap<Integer, Intersection> getAllIntersections() throws SQLException {
        Connection c = Simulator.get().getSqlConnection();
        Statement stmt = c.createStatement();
        String sql = "SELECT id, point FROM intersection;";

        ResultSet resultSet = stmt.executeQuery(sql);
        HashMap<Integer, Intersection> intersections = new HashMap<>();

        while (resultSet.next()) {
            Intersection intersection = new Intersection(resultSet);
            intersections.put(intersection.id, intersection);
        }

        return intersections;
    }

    public List<Road> getAllRoads() throws SQLException {
        Connection c = Simulator.get().getSqlConnection();
        Statement stmt = c.createStatement();
        String sql = "SELECT id, road_length, from_id, to_id FROM road;";

        ResultSet resultSet = stmt.executeQuery(sql);
        List<Road> roads = new ArrayList<>();

        while (resultSet.next()) {
            Road road = new Road(resultSet);
            roads.add(road);
        }

        return roads;
    }

}
