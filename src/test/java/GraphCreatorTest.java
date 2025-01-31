/*
 * This Java source file was generated by the Gradle 'init' task.
 */
import graph.GraphCreator;
import graph.Intersection;
import graph.Road;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class GraphCreatorTest {

    @Test public void testIntersectionNotEmpty() throws SQLException {
        GraphCreator graphCreator = new GraphCreator();


        HashMap<Integer, Intersection> allIntersections = graphCreator.getAllIntersections();

        assertNotNull("intersect import should not be empty", allIntersections);
        assertTrue("intersect import should import more than one intersection", allIntersections.size() > 1);

        System.out.println(allIntersections.size());
    }

    @Test public void testRoadsNotEmpty() throws SQLException {
        GraphCreator graphCreator = new GraphCreator();

        List<Road> allRoads = graphCreator.getAllRoads();

        assertNotNull("roads import should not be empty", allRoads);
        assertTrue("roads import should import more than one intersection", allRoads.size() > 1);

        System.out.println(allRoads.size());
    }

    @Test public void testCreateGraph() throws SQLException {
        GraphCreator graphCreator = new GraphCreator();
        WeightedGraph<Intersection, DefaultWeightedEdge> graph = graphCreator.createGraph();
        assertTrue("does not contain vertices", graph.vertexSet().size() > 0);
    }
}
