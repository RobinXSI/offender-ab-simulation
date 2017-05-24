package core;

import agent.Agent;
import geography.Geography;
import graph.GraphCreator;
import graph.Intersection;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import java.sql.SQLException;

public class App {
    public static void main(String[] args) throws SQLException {
        GraphCreator graphCreator = new GraphCreator();
        WeightedGraph<Intersection, DefaultWeightedEdge> graph = graphCreator.createGraph();
        Simulator.get().setGraph(graph);

        Geography geography = new Geography();
        Intersection intersection = geography.getRandomIntersection();

        Agent agent = new Agent(intersection.getPoint());

        agent.step();






    }
}
