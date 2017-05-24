import agent.Agent;
import core.Simulator;
import geography.Geography;
import graph.GraphCreator;
import graph.Intersection;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import java.sql.SQLException;


public class AgentTest {

    public AgentTest() {
        GraphCreator graphCreator = new GraphCreator();
        WeightedGraph<Intersection, DefaultWeightedEdge> graph = graphCreator.createGraph();
        Simulator.get().setGraph(graph);
    }

    @Test
    public void testAgentStep() throws SQLException {
        Geography geography = new Geography();
        Agent agent = new Agent(geography.getRandomIntersection().getPoint());
        agent.step();
    }

    @Test
    public void testAgentFindGoal() throws SQLException {
        Geography geography = new Geography();
        Agent agent = new Agent(geography.getRandomIntersection().getPoint());
    }
}
