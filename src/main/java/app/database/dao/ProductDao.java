package app.database.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import app.model.Product;

/**
 * DAO interface defining CRUD operations for Product.
 */
public interface ProductDao {

    /** Inserts a new product and returns the persisted instance. */
    Product create(Product product) throws SQLException;

    /** Updates an existing product. Returns true if updated successfully. */
    boolean update(Product product) throws SQLException;

    /** Deletes a product by ID. Returns true if deleted successfully. */
    boolean deleteById(int id) throws SQLException;

    /** Finds a product by its ID. */
    Optional<Product> findById(int id) throws SQLException;

    /** Returns all products in the database. */
    List<Product> findAll() throws SQLException;

    /** Searches for products whose description matches a pattern. */
    List<Product> searchByDescription(String descriptionPattern) throws SQLException;

    /** Counts total products in the database. */
    long count() throws SQLException;

    /** Checks if a product exists by ID. */
    boolean existsById(int id) throws SQLException;
}