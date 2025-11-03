package integration;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import app.database.dao.ProductDao;
import app.database.dao.ProductDaoImpl;
import app.model.Product;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationProductDaoTest {

    private Connection realConn;
    private ProductDao dao;

    @BeforeAll
    void setup() throws Exception {
        realConn = DriverManager.getConnection("jdbc:h2:mem:shopdb;DB_CLOSE_DELAY=-1;MODE=MySQL", "sa", "");
        
        // H2-compatible schema (removed MySQL-specific syntax)
        String schemaSql = """
            CREATE TABLE IF NOT EXISTS Product (
                id INT NOT NULL PRIMARY KEY,
                description VARCHAR(30) NOT NULL,
                brand VARCHAR(30) NOT NULL,
                content VARCHAR(30) NOT NULL,
                category VARCHAR(30) NOT NULL,
                price DECIMAL(10,2) NOT NULL,
                status VARCHAR(15) NOT NULL,
                dateMade DATE NOT NULL,
                expirationDate DATE NULL
            );
        """;
        realConn.createStatement().execute(schemaSql);

        // Use a supplier that returns a non-closing wrapper
        dao = new ProductDaoImpl(() -> new NonClosingConnectionWrapper(realConn));
    }

    @BeforeEach
    void clearTable() throws Exception {
        realConn.createStatement().execute("DELETE FROM Product");
    }

    @AfterAll
    void cleanup() throws Exception {
        if (realConn != null) realConn.close();
    }

    /**
     * Wrapper that delegates all methods to the real connection
     * but ignores close() calls, allowing tests to reuse a single connection.
     */
    private static class NonClosingConnectionWrapper implements Connection {
        private final Connection delegate;

        NonClosingConnectionWrapper(Connection delegate) {
            this.delegate = delegate;
        }

        @Override public void close() throws SQLException {
            // Do nothing - don't close the underlying connection
        }

        // Delegate all other methods
        @Override public Statement createStatement() throws SQLException { return delegate.createStatement(); }
        @Override public PreparedStatement prepareStatement(String sql) throws SQLException { return delegate.prepareStatement(sql); }
        @Override public CallableStatement prepareCall(String sql) throws SQLException { return delegate.prepareCall(sql); }
        @Override public String nativeSQL(String sql) throws SQLException { return delegate.nativeSQL(sql); }
        @Override public void setAutoCommit(boolean autoCommit) throws SQLException { delegate.setAutoCommit(autoCommit); }
        @Override public boolean getAutoCommit() throws SQLException { return delegate.getAutoCommit(); }
        @Override public void commit() throws SQLException { delegate.commit(); }
        @Override public void rollback() throws SQLException { delegate.rollback(); }
        @Override public boolean isClosed() throws SQLException { return delegate.isClosed(); }
        @Override public DatabaseMetaData getMetaData() throws SQLException { return delegate.getMetaData(); }
        @Override public void setReadOnly(boolean readOnly) throws SQLException { delegate.setReadOnly(readOnly); }
        @Override public boolean isReadOnly() throws SQLException { return delegate.isReadOnly(); }
        @Override public void setCatalog(String catalog) throws SQLException { delegate.setCatalog(catalog); }
        @Override public String getCatalog() throws SQLException { return delegate.getCatalog(); }
        @Override public void setTransactionIsolation(int level) throws SQLException { delegate.setTransactionIsolation(level); }
        @Override public int getTransactionIsolation() throws SQLException { return delegate.getTransactionIsolation(); }
        @Override public SQLWarning getWarnings() throws SQLException { return delegate.getWarnings(); }
        @Override public void clearWarnings() throws SQLException { delegate.clearWarnings(); }
        @Override public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException { return delegate.createStatement(resultSetType, resultSetConcurrency); }
        @Override public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency); }
        @Override public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return delegate.prepareCall(sql, resultSetType, resultSetConcurrency); }
        @Override public java.util.Map<String,Class<?>> getTypeMap() throws SQLException { return delegate.getTypeMap(); }
        @Override public void setTypeMap(java.util.Map<String,Class<?>> map) throws SQLException { delegate.setTypeMap(map); }
        @Override public void setHoldability(int holdability) throws SQLException { delegate.setHoldability(holdability); }
        @Override public int getHoldability() throws SQLException { return delegate.getHoldability(); }
        @Override public Savepoint setSavepoint() throws SQLException { return delegate.setSavepoint(); }
        @Override public Savepoint setSavepoint(String name) throws SQLException { return delegate.setSavepoint(name); }
        @Override public void rollback(Savepoint savepoint) throws SQLException { delegate.rollback(savepoint); }
        @Override public void releaseSavepoint(Savepoint savepoint) throws SQLException { delegate.releaseSavepoint(savepoint); }
        @Override public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability); }
        @Override public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability); }
        @Override public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability); }
        @Override public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException { return delegate.prepareStatement(sql, autoGeneratedKeys); }
        @Override public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException { return delegate.prepareStatement(sql, columnIndexes); }
        @Override public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException { return delegate.prepareStatement(sql, columnNames); }
        @Override public Clob createClob() throws SQLException { return delegate.createClob(); }
        @Override public Blob createBlob() throws SQLException { return delegate.createBlob(); }
        @Override public NClob createNClob() throws SQLException { return delegate.createNClob(); }
        @Override public SQLXML createSQLXML() throws SQLException { return delegate.createSQLXML(); }
        @Override public boolean isValid(int timeout) throws SQLException { return delegate.isValid(timeout); }
        @Override public void setClientInfo(String name, String value) throws SQLClientInfoException { delegate.setClientInfo(name, value); }
        @Override public void setClientInfo(java.util.Properties properties) throws SQLClientInfoException { delegate.setClientInfo(properties); }
        @Override public String getClientInfo(String name) throws SQLException { return delegate.getClientInfo(name); }
        @Override public java.util.Properties getClientInfo() throws SQLException { return delegate.getClientInfo(); }
        @Override public Array createArrayOf(String typeName, Object[] elements) throws SQLException { return delegate.createArrayOf(typeName, elements); }
        @Override public Struct createStruct(String typeName, Object[] attributes) throws SQLException { return delegate.createStruct(typeName, attributes); }
        @Override public void setSchema(String schema) throws SQLException { delegate.setSchema(schema); }
        @Override public String getSchema() throws SQLException { return delegate.getSchema(); }
        @Override public void abort(java.util.concurrent.Executor executor) throws SQLException { delegate.abort(executor); }
        @Override public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) throws SQLException { delegate.setNetworkTimeout(executor, milliseconds); }
        @Override public int getNetworkTimeout() throws SQLException { return delegate.getNetworkTimeout(); }
        @Override public <T> T unwrap(Class<T> iface) throws SQLException { return delegate.unwrap(iface); }
        @Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return delegate.isWrapperFor(iface); }
    }

    @Test
    void createAndFindById() throws Exception {
        Product p = new Product();
        p.setId(100);
        p.setDescription("Test Product");
        p.setBrand("TestBrand");
        p.setContent("500g");
        p.setCategory("Abarrotes");
        p.setPrice(12.50);
        p.setActive(true);
        p.setDateMade(LocalDate.now().minusDays(10));
        p.setExpirationDate(LocalDate.now().plusDays(100));

        dao.create(p);
        Product fetched = dao.findById(100).orElseThrow();
        
        assertThat(fetched.getDescription()).isEqualTo("Test Product");
        assertThat(fetched.getBrand()).isEqualTo("TestBrand");
        assertThat(fetched.getPrice()).isEqualTo(12.50);
        assertThat(fetched.isActive()).isTrue();
    }

    @Test
    void findByIdNotFound() throws Exception {
        assertThat(dao.findById(9999)).isEmpty();
    }

    @Test
    void updateProduct() throws Exception {
        // Create initial product
        Product p = new Product();
        p.setId(200);
        p.setDescription("Original");
        p.setBrand("OriginalBrand");
        p.setContent("1L");
        p.setCategory("Abarrotes");
        p.setPrice(10.0);
        p.setActive(true);
        p.setDateMade(LocalDate.now().minusDays(5));
        p.setExpirationDate(LocalDate.now().plusDays(30));

        dao.create(p);

        // Update it
        p.setDescription("Updated");
        p.setBrand("UpdatedBrand");
        p.setPrice(15.0);
        p.setActive(false);

        boolean updated = dao.update(p);
        assertThat(updated).isTrue();

        // Verify changes
        Product fetched = dao.findById(200).orElseThrow();
        assertThat(fetched.getDescription()).isEqualTo("Updated");
        assertThat(fetched.getBrand()).isEqualTo("UpdatedBrand");
        assertThat(fetched.getPrice()).isEqualTo(15.0);
        assertThat(fetched.isActive()).isFalse();
    }

    @Test
    void deleteById() throws Exception {
        Product p = new Product();
        p.setId(300);
        p.setDescription("To Delete");
        p.setBrand("Brand");
        p.setContent("100g");
        p.setCategory("Abarrotes");
        p.setPrice(5.0);
        p.setActive(true);
        p.setDateMade(LocalDate.now());

        dao.create(p);
        assertThat(dao.existsById(300)).isTrue();

        boolean deleted = dao.deleteById(300);
        assertThat(deleted).isTrue();
        assertThat(dao.existsById(300)).isFalse();
    }

    @Test
    void findAll() throws Exception {
        // Clear and add test data
        Product p1 = new Product();
        p1.setId(401);
        p1.setDescription("Product 1");
        p1.setBrand("Brand1");
        p1.setContent("100g");
        p1.setCategory("Abarrotes");
        p1.setPrice(10.0);
        p1.setActive(true);
        p1.setDateMade(LocalDate.now());

        Product p2 = new Product();
        p2.setId(402);
        p2.setDescription("Product 2");
        p2.setBrand("Brand2");
        p2.setContent("200g");
        p2.setCategory("Abarrotes");
        p2.setPrice(20.0);
        p2.setActive(true);
        p2.setDateMade(LocalDate.now());

        dao.create(p1);
        dao.create(p2);

        var all = dao.findAll();
        assertThat(all).hasSizeGreaterThanOrEqualTo(2);
        assertThat(all).anyMatch(p -> p.getId() == 401);
        assertThat(all).anyMatch(p -> p.getId() == 402);
    }

    @Test
    void searchByDescription() throws Exception {
        Product p1 = new Product();
        p1.setId(501);
        p1.setDescription("Chocolate Bar");
        p1.setBrand("Brand");
        p1.setContent("50g");
        p1.setCategory("Abarrotes");
        p1.setPrice(5.0);
        p1.setActive(true);
        p1.setDateMade(LocalDate.now());

        Product p2 = new Product();
        p2.setId(502);
        p2.setDescription("Vanilla Ice Cream");
        p2.setBrand("Brand");
        p2.setContent("1L");
        p2.setCategory("Abarrotes");
        p2.setPrice(15.0);
        p2.setActive(true);
        p2.setDateMade(LocalDate.now());

        dao.create(p1);
        dao.create(p2);

        var results = dao.searchByDescription("Chocolate");
        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(p -> p.getId() == 501);
        assertThat(results).noneMatch(p -> p.getId() == 502);
    }

    @Test
    void count() throws Exception {
        long initialCount = dao.count();

        Product p = new Product();
        p.setId(600);
        p.setDescription("Count Test");
        p.setBrand("Brand");
        p.setContent("1kg");
        p.setCategory("Abarrotes");
        p.setPrice(25.0);
        p.setActive(true);
        p.setDateMade(LocalDate.now());

        dao.create(p);

        long newCount = dao.count();
        assertThat(newCount).isEqualTo(initialCount + 1);
    }

    @Test
    void existsById() throws Exception {
        Product p = new Product();
        p.setId(700);
        p.setDescription("Exists Test");
        p.setBrand("Brand");
        p.setContent("500ml");
        p.setCategory("Abarrotes");
        p.setPrice(8.0);
        p.setActive(true);
        p.setDateMade(LocalDate.now());

        assertThat(dao.existsById(700)).isFalse();
        dao.create(p);
        assertThat(dao.existsById(700)).isTrue();
    }

    @Test
    void handleNullExpirationDate() throws Exception {
        Product p = new Product();
        p.setId(800);
        p.setDescription("No Expiration");
        p.setBrand("Brand");
        p.setContent("1 unit");
        p.setCategory("Abarrotes");
        p.setPrice(50.0);
        p.setActive(true);
        p.setDateMade(LocalDate.now());
        p.setExpirationDate((LocalDate) null); // explicitly null

        dao.create(p);
        Product fetched = dao.findById(800).orElseThrow();
        assertThat(fetched.getExpirationDate()).isNull();
    }
}