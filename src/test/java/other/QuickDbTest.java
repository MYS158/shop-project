package other;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QuickDbTest {
    public static void test() throws Exception {
        String url = "jdbc:mysql://127.0.0.1:3306/shopdb?useSSL=false&serverTimezone=UTC",
               user = "test",
               pass = "1234";
        try {
            Connection c = DriverManager.getConnection(url, user, pass);
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery("SELECT CURRENT_USER(), DATABASE()");
            while (rs.next()) System.out.println(String.format("%s - %s", rs.getString(1), rs.getString(2)));
        } catch (SQLException e) {
            System.err.println(String.format("SQL Exception: %s", e.getMessage()));
        }
    }
}