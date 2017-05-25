package core;

import graph.Intersection;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Simulator {
    private static Simulator instance = new Simulator();

    public final static int SRID = 2263;
    public final static double STATIC_RADIUS = 1000;
    public final static int STEP_NUMBER = 100;
    public final static int NUMBER_OF_AGENTS = 50;
    public final static int NUMBER_OF_TRIES_TO_RESET_LOCATION = 3;
    public static String OUTPUT_FILE;

    private WeightedGraph<Intersection, DefaultWeightedEdge> graph;
    private HashMap<Integer, Intersection> verticesMap;

    private Connection sqlConnection;

    private Simulator() {
        OUTPUT_FILE = "./data/out/simulation_output_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".csv";
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

    public void setGraph(WeightedGraph<Intersection, DefaultWeightedEdge> graph, HashMap<Integer, Intersection> verticesMap) {
        this.graph = graph;
        this.verticesMap = verticesMap;
    }

    public Connection getSqlConnection() {
        return sqlConnection;
    }

    public Intersection getIntersectionById(int id) {
        return verticesMap.get(id);
    }


}
