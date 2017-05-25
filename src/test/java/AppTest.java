import agent.Agent;
import core.App;
import geography.Geography;
import graph.GraphCreator;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;


public class AppTest {

    @Test
    public void testSimulation() throws SQLException, IOException {
        App app = new App();
        app.simulate();
    }
}
