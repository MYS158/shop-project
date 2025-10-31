package app.database.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import app.model.Product;

public interface ProductDao {
    Product create(Product product) throws SQLException;
    boolean update(Product product) throws SQLException;
    boolean deleteById(int id) throws SQLException;
    Optional<Product> findById(int id) throws SQLException;
    List<Product> findAll() throws SQLException;
    List<Product> searchByDescription(String descriptionPattern) throws SQLException;
    long count() throws SQLException;
    boolean existsById(int id) throws SQLException;
}