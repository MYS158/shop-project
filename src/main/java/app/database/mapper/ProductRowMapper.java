package app.database.mapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import app.model.Product;

public class ProductRowMapper {

    public Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        // Columns: id, description, brand, content, category, price, status, dateMade, expirationDate
        p.setId(rs.getInt("id"));
        p.setDescription(rs.getString("description"));
        p.setBrand(rs.getString("brand"));
        p.setContent(rs.getString("content"));
        p.setCategory(rs.getString("category"));
        p.setPrice(rs.getDouble("price"));

        String status = rs.getString("status");
        boolean active = false;
        if (status != null) {
            String s = status.trim().toLowerCase();
            if ("checked".equals(s) || "active".equals(s) || "true".equals(s)) active = true;
        }
        p.setActive(active);

        Date dm = rs.getDate("dateMade");
        if (dm != null) p.setDateMade(new java.util.Date(dm.getTime()));

        Date exp = rs.getDate("expirationDate");
        if (exp != null) p.setExpirationDate(new java.util.Date(exp.getTime()));

        return p;
    }
}