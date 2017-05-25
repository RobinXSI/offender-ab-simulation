package core;

import agent.Agent;
import geography.Geography;
import graph.GraphCreator;
import graph.Intersection;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import statistics.StepStatistics;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws SQLException {
        App app = new App();
        app.simulate();
    }

    public void simulate() throws SQLException {
        GraphCreator graphCreator = new GraphCreator();
        WeightedGraph<Intersection, DefaultWeightedEdge> graph = graphCreator.createGraph();

        Geography geography = new Geography();

        List<Agent> agents = new ArrayList<>(Simulator.NUMBER_OF_AGENTS);
        for (int i = 0; i < Simulator.NUMBER_OF_AGENTS; i++) {
            Intersection intersection = geography.getRandomIntersection();
            Agent agent = new Agent(i, intersection);
            agents.add(agent);
        }

        for (int i = 0; i < Simulator.STEP_NUMBER; i++) {
            StepStatistics stepStatistics = new StepStatistics(i);
            for (Agent agent : agents) {
                System.out.println("Agent index: " + agent.getId() + " step: " + i);
                agent.step(stepStatistics);
            }
            System.out.println(stepStatistics.toString());
        }
    }
}
