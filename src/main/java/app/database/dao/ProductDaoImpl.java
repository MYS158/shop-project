package app.database.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import app.database.DatabaseManager;
import app.model.Product;

public class ProductDaoImpl implements ProductDao {
    private final Supplier<Connection> connectionSupplier;

    public ProductDaoImpl(DatabaseManager dbManager) {
        this.connectionSupplier = () -> {
            try {
                return dbManager.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };
    }

    public ProductDaoImpl(Supplier<Connection> connectionSupplier) {
        this.connectionSupplier = connectionSupplier;
    }

    @Override
    public Product create(Product p) throws SQLException {
        String sql = "INSERT INTO Product (id, description, brand, content, category, price, status, dateMade, expirationDate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connectionSupplier.get();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getId());
            ps.setString(2, p.getDescription());
            ps.setString(3, p.getBrand());
            ps.setString(4, p.getContent());
            ps.setString(5, p.getCategory());
            ps.setDouble(6, p.getPrice());
            ps.setString(7, p.isActive() ? "Active" : "Inactive");
            
            if (p.getDateMade() != null) {
                ps.setDate(8, new Date(p.getDateMade().getTime()));
            } else {
                ps.setNull(8, Types.DATE);
            }
            
            if (p.getExpirationDate() != null) {
                ps.setDate(9, new Date(p.getExpirationDate().getTime()));
            } else {
                ps.setNull(9, Types.DATE);
            }
            
            ps.executeUpdate();
            return p;
        }
    }

    @Override
    public Optional<Product> findById(int id) throws SQLException {
        String sql = "SELECT * FROM Product WHERE id = ?";
        try (Connection conn = connectionSupplier.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Product p = mapRow(rs);
                    return Optional.of(p);
                } else {
                    return Optional.empty();
                }
            }
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setDescription(rs.getString("description"));
        p.setBrand(rs.getString("brand"));
        p.setContent(rs.getString("content"));
        p.setCategory(rs.getString("category"));
        p.setPrice(rs.getDouble("price"));
        p.setStatus(rs.getString("status"));
        Date dm = rs.getDate("dateMade");
        if (dm != null) p.setDateMade(dm.toLocalDate());
        Date ed = rs.getDate("expirationDate");
        if (ed != null) p.setExpirationDate(ed.toLocalDate());
        return p;
    }

    @Override
    public boolean update(Product p) throws SQLException {
        String sql = "UPDATE Product SET description=?, brand=?, content=?, category=?, price=?, status=?, dateMade=?, expirationDate=? WHERE id=?";
        try (Connection conn = connectionSupplier.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getDescription());
            ps.setString(2, p.getBrand());
            ps.setString(3, p.getContent());
            ps.setString(4, p.getCategory());
            ps.setDouble(5, p.getPrice());
            ps.setString(6, p.isActive() ? "Active" : "Inactive");
            
            if (p.getDateMade() != null) {
                ps.setDate(7, new Date(p.getDateMade().getTime()));
            } else {
                ps.setNull(7, Types.DATE);
            }
            
            if (p.getExpirationDate() != null) {
                ps.setDate(8, new Date(p.getExpirationDate().getTime()));
            } else {
                ps.setNull(8, Types.DATE);
            }
            
            ps.setInt(9, p.getId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteById(int id) throws SQLException {
        String sql = "DELETE FROM Product WHERE id = ?";
        try (Connection conn = connectionSupplier.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public List<Product> findAll() throws SQLException {
        String sql = "SELECT * FROM Product";
        List<Product> products = new ArrayList<>();
        try (Connection conn = connectionSupplier.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        }
        return products;
    }

    @Override
    public List<Product> searchByDescription(String descriptionPattern) throws SQLException {
        String sql = "SELECT * FROM Product WHERE description LIKE ?";
        List<Product> products = new ArrayList<>();
        try (Connection conn = connectionSupplier.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + descriptionPattern + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }
        }
        return products;
    }

    @Override
    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Product";
        try (Connection conn = connectionSupplier.get();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        }
    }

    @Override
    public boolean existsById(int id) throws SQLException {
        String sql = "SELECT 1 FROM Product WHERE id = ?";
        try (Connection conn = connectionSupplier.get();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}