import agent.Agent;
import agent.RadiusType;
import geography.Geography;
import graph.GraphCreator;
import org.junit.Test;

import java.sql.SQLException;


public class AgentTest {

    public AgentTest() {
        GraphCreator graphCreator = new GraphCreator();
        graphCreator.createGraph();
    }

    @Test
    public void testAgentStep() throws SQLException {
        Geography geography = new Geography();
        Agent agent = new Agent(0, geography.getRandomIntersection(), RadiusType.STATIC);
//        agent.step(stepStatistics);
    }

    @Test
    public void testAgentFindGoal() throws SQLException {
        Geography geography = new Geography();
        Agent agent = new Agent(0, geography.getRandomIntersection(), RadiusType.STATIC);
    }
}
