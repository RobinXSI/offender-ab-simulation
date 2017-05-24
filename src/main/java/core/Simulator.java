package core;

import graph.Intersection;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Simulator {
    private static Simulator instance = new Simulator();

    public final static int SRID = 2263;
    public final static double STATIC_RADIUS = 1000;

    WeightedGraph<Intersection, DefaultWeightedEdge> graph;
    Connection sqlConnection;

    private Simulator() {
        try {
            Class.forName("org.postgresql.Driver");
            sqlConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/geo-ny", "robin", "");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("SQL Connection issues");
        }
    }

    public static Simulator get() {
        return instance;
    }

    public WeightedGraph<Intersection, DefaultWeightedEdge> getGraph() {
        return graph;
    }

    public void setGraph(WeightedGraph<Intersection, DefaultWeightedEdge> graph) {
        this.graph = graph;
    }

    public Connection getSqlConnection() {
        return sqlConnection;
    }
}
