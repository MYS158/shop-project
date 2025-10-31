package app.database.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

import app.database.DatabaseManager;
import app.database.mapper.ProductRowMapper;
import app.model.Product;
import app.util.ValidationUtils;

public class ProductDaoImpl implements ProductDao {
    private final DatabaseManager db;
    private final ProductRowMapper mapper = new ProductRowMapper();

    public ProductDaoImpl(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public Product create(Product product) throws SQLException {
        validateForCreate(product);

        String sql = "INSERT INTO products (description, brand, content, category, price, status, dateMade, expirationDate) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setCommonParameters(ps, product);
            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new SQLException("Creating product failed, no rows affected.");
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getInt(1));
                }
            }
            return product;
        }
    }

    @Override
    public boolean update(Product product) throws SQLException {
        if (!ValidationUtils.isValidId(product.getId())) {
            throw new IllegalArgumentException("Invalid product id");
        }
        validateForUpdate(product);

        String sql = "UPDATE products SET description = ?, brand = ?, content = ?, category = ?, price = ?, status = ?, dateMade = ?, expirationDate = ? WHERE id = ?";
        return db.executeUpdate(sql, ps -> {
            setCommonParameters(ps, product);
            ps.setInt(9, product.getId());
        }) > 0;
    }

    @Override
    public boolean deleteById(int id) throws SQLException {
        if (!ValidationUtils.isValidId(id)) throw new IllegalArgumentException("Invalid id");
        String sql = "DELETE FROM products WHERE id = ?";
        return db.executeUpdate(sql, ps -> ps.setInt(1, id)) > 0;
    }

    @Override
    public Optional<Product> findById(int id) throws SQLException {
        if (!ValidationUtils.isValidId(id)) throw new IllegalArgumentException("Invalid id");
        String sql = "SELECT id, description, brand, content, category, price, status, dateMade, expirationDate FROM products WHERE id = ?";
        List<Product> list = db.executeQuery(sql, ps -> ps.setInt(1, id), rs -> mapper.map(rs));
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    @Override
    public List<Product> findAll() throws SQLException {
        String sql = "SELECT id, description, brand, content, category, price, status, dateMade, expirationDate FROM products ORDER BY id";
        return db.executeQuery(sql, null, rs -> mapper.map(rs));
    }

    @Override
    public List<Product> searchByDescription(String descriptionPattern) throws SQLException {
        String sql = "SELECT id, description, brand, content, category, price, status, dateMade, expirationDate FROM products WHERE description LIKE ? ORDER BY description";
        String pattern = "%" + descriptionPattern + "%";
        return db.executeQuery(sql, ps -> ps.setString(1, pattern), rs -> mapper.map(rs));
    }

    @Override
    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) AS cnt FROM products";
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong("cnt");
            return 0L;
        }
    }

    @Override
    public boolean existsById(int id) throws SQLException {
        if (!ValidationUtils.isValidId(id)) return false;
        String sql = "SELECT 1 FROM products WHERE id = ? LIMIT 1";
        List<Integer> res = db.executeQuery(sql, ps -> ps.setInt(1, id), rs -> rs.getInt(1));
        return !res.isEmpty();
    }

    // ---------- helpers ----------

    private void setCommonParameters(PreparedStatement ps, Product product) throws SQLException {
        // 1..8 parameters
        ps.setString(1, product.getDescription());
        ps.setString(2, product.getBrand());
        ps.setString(3, product.getContent());
        ps.setString(4, product.getCategory());
        ps.setBigDecimal(5, java.math.BigDecimal.valueOf(product.getPrice()));
        // status mapping: store 'Checked' if active true, else 'Unchecked'
        ps.setString(6, product.isActive() ? "Checked" : "Unchecked");

        // dateMade (NOT NULL)
        if (product.getDateMade() == null) {
            throw new SQLException("dateMade cannot be null");
        }
        ps.setDate(7, new Date(product.getDateMade().getTime()));

        // expirationDate may be null
        if (product.getExpirationDate() != null) {
            ps.setDate(8, new Date(product.getExpirationDate().getTime()));
        } else {
            ps.setNull(8, Types.DATE);
        }
    }

    private void validateForCreate(Product product) {
        if (product == null) throw new IllegalArgumentException("product is null");
        if (!ValidationUtils.isValidPrice(product.getPrice()))
            throw new IllegalArgumentException("price must be > 0");
        if (!ValidationUtils.validCategory(product.getCategory()))
            throw new IllegalArgumentException("invalid category");
        if (!ValidationUtils.validDates(product.getDateMade(), product.getExpirationDate()))
            throw new IllegalArgumentException("dateMade must be before expirationDate");
        // id should not be provided for create (AUTO_INCREMENT) — but if provided, validate range
        if (product.getId() != 0 && !ValidationUtils.isValidId(product.getId()))
            throw new IllegalArgumentException("id out of range (1-9999)");
        // description/brand/content length constraints: ensure not null and length <= 30
        checkStringField("description", product.getDescription(), 30);
        checkStringField("brand", product.getBrand(), 30);
        checkStringField("content", product.getContent(), 30);
        checkStringField("category", product.getCategory(), 30);
    }

    private void validateForUpdate(Product product) {
        if (product == null) throw new IllegalArgumentException("product is null");
        if (!ValidationUtils.isValidId(product.getId()))
            throw new IllegalArgumentException("invalid id");
        // reuse create validation for other fields
        if (!ValidationUtils.isValidPrice(product.getPrice()))
            throw new IllegalArgumentException("price must be > 0");
        if (!ValidationUtils.validCategory(product.getCategory()))
            throw new IllegalArgumentException("invalid category");
        if (!ValidationUtils.validDates(product.getDateMade(), product.getExpirationDate()))
            throw new IllegalArgumentException("dateMade must be before expirationDate");
        checkStringField("description", product.getDescription(), 30);
        checkStringField("brand", product.getBrand(), 30);
        checkStringField("content", product.getContent(), 30);
        checkStringField("category", product.getCategory(), 30);
    }

    private void checkStringField(String name, String value, int maxLen) {
        if (value == null) throw new IllegalArgumentException(name + " cannot be null");
        if (value.length() > maxLen) throw new IllegalArgumentException(name + " exceeds max length " + maxLen);
    }
}