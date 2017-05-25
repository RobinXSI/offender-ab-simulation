package core;

import agent.Agent;
import agent.AgentType;
import agent.RadiusType;
import geography.Geography;
import graph.GraphCreator;
import graph.Intersection;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import statistics.StepStatistics;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class App {
    public static void main(String[] args) throws SQLException, IOException {
        App app = new App();
        app.simulate();
    }

    public void simulate() throws SQLException, IOException {
        GraphCreator graphCreator = new GraphCreator();
        WeightedGraph<Intersection, DefaultWeightedEdge> graph = graphCreator.createGraph();

        Geography geography = new Geography();

        FileWriter writer = new FileWriter(Simulator.OUTPUT_FILE);


        for (RadiusType radiusType : Simulator.RADIUS_TYPES) {
            for (AgentType agentType : Simulator.AGENT_TYPES) {
                List<Agent> agents = new ArrayList<>(Simulator.NUMBER_OF_AGENTS);
                for (int i = 0; i < Simulator.NUMBER_OF_AGENTS; i++) {
                    Intersection intersection = geography.getRandomIntersection();
                    Agent agent = new Agent(i, intersection, radiusType, agentType);
                    agents.add(agent);
                }

                for (int i = 0; i < Simulator.STEP_NUMBER; i++) {
                    StepStatistics stepStatistics = new StepStatistics(i, radiusType, agentType);
                    for (Agent agent : agents) {
                        System.out.println("Agent index: " + agent.getId() + " step: " + i);
                        agent.step(stepStatistics);
                    }
                    System.out.println(stepStatistics.toString());

                    if (i == 0) {
                        writer.append(StepStatistics.csvHeader() + "\n");
                    }
                    writer.append(stepStatistics.toCSV() + "\n");
                    writer.flush();
                }
            }
        }
        writer.close();
    }
}
