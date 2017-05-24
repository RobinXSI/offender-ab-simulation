import geography.Geography;
import graph.Intersection;
import org.junit.Test;
import org.postgis.PGbox2d;
import org.postgis.Point;

import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;

public class GeographyTest {
    @Test
    public void testGetBoundaries() throws SQLException {
        Geography geography = new Geography();
        PGbox2d boundaries = geography.getBoundaries();

        Point lowerLeftBoundary = boundaries.getLLB();
        Point upperRightBoundary = boundaries.getURT();

        assertNotNull(lowerLeftBoundary);
        assertNotNull(upperRightBoundary);
    }

    @Test
    public void testGetRandomIntersection() throws SQLException {
        Geography geography = new Geography();
        Intersection randomIntersection = geography.getRandomIntersection();
        assertNotNull(randomIntersection);
        assertNotNull(randomIntersection.getPoint());
        assertNotNull(randomIntersection.getId());
    }
}
