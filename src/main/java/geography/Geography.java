package geography;


import core.Simulator;
import graph.Intersection;
import org.postgis.PGbox2d;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Geography {
    public PGbox2d getBoundaries() throws SQLException {

        Connection c = Simulator.get().getSqlConnection();

        Statement stmt = c.createStatement();
        String sql = "SELECT ST_Extent(point) as table_extent FROM intersection;";

        ResultSet resultSet = stmt.executeQuery(sql);
        resultSet.next();
        return  (PGbox2d) resultSet.getObject("table_extent");
    }

    public Intersection getRandomIntersection() throws SQLException {

        Connection c = Simulator.get().getSqlConnection();

        Statement stmt = c.createStatement();
        String sql = "SELECT * FROM intersection ORDER BY RANDOM() LIMIT 1;";

        ResultSet resultSet = stmt.executeQuery(sql);
        resultSet.next();

        return new Intersection(resultSet);



    }

}
