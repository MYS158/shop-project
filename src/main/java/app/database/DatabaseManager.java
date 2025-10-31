package app.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

public class DatabaseManager {
    private final String jdbcUrl;
    private final String username;
    private final String password;

    public DatabaseManager(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public static DatabaseManager fromProperties(Properties props) {
        String url = props.getProperty("jdbc.url");
        String user = props.getProperty("jdbc.username");
        String pass = props.getProperty("jdbc.password");
        if (url == null) throw new IllegalArgumentException("jdbc.url property missing");
        return new DatabaseManager(url, user, pass);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public int executeUpdate(String sql, PreparedStatementSetter setter) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (setter != null) setter.setParameters(ps);
            return ps.executeUpdate();
        }
    }

    public <T> List<T> executeQuery(String sql, PreparedStatementSetter setter, ResultSetMapper<T> mapper) throws SQLException {
        List<T> results = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (setter != null) setter.setParameters(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapper.map(rs));
                }
            }
        }
        return results;
    }

    public <T> T inTransaction(Function<Connection, T> transactionalFunction) throws SQLException {
        try (Connection conn = getConnection()) {
            boolean oldAuto = conn.getAutoCommit();
            try {
                conn.setAutoCommit(false);
                T result = transactionalFunction.apply(conn);
                conn.commit();
                return result;
            } catch (RuntimeException | SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(oldAuto);
            }
        }
    }

    /**
     * Utility: run SQL script resource (simple split by semicolon). Good for schema/seed.
     */
    public void runSqlScriptResource(String resourcePath) throws Exception {
        String sql = readResourceAsString(resourcePath);
        if (sql == null || sql.trim().isEmpty()) return;
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {
            conn.setAutoCommit(false);
            try {
                String[] parts = sql.split("(?m);\\s*$");
                for (String part : parts) {
                    String t = part.trim();
                    if (t.isEmpty()) continue;
                    st.execute(t);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private String readResourceAsString(String path) throws Exception {
        String effective = path.startsWith("/") ? path.substring(1) : path;
        try (InputStream is = DatabaseManager.class.getClassLoader().getResourceAsStream(effective)) {
            if (is == null) return null;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            }
        }
    }

    @FunctionalInterface
    public interface PreparedStatementSetter {
        void setParameters(PreparedStatement ps) throws SQLException;
    }

    @FunctionalInterface
    public interface ResultSetMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}
