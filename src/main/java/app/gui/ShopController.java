package app.gui;

import java.awt.HeadlessException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;

import app.database.DatabaseManager;
import app.database.dao.ProductDao;
import app.model.Product;
import app.util.ValidationResult;
import app.util.ValidationUtils;

/**
 * Controller that wires UI components (form, table, search, actions) with the DAO
 * and contains the "application logic" for CRUD/search/refresh.
 *
 * Responsibilities:
 *  - load initial data (from DAO or demo list)
 *  - perform create/read/update/delete using ProductDao or fallback memory list
 *  - wire listeners for RightButtonPanel and SearchPanel
 */
public class ShopController {
    private final ProductFormPanel form;
    private final ProductTablePanel table;
    private final SearchPanel search;
    private final RightButtonPanel actions;
    private final ProductDao dao;

    // fallback in-memory store when dao == null
    private final List<Product> memory = new ArrayList<>();

    public ShopController(ProductFormPanel form, ProductTablePanel table, SearchPanel search,
                          RightButtonPanel actions, DatabaseManager db, ProductDao dao) {
        this.form = form;
        this.table = table;
        this.search = search;
        this.actions = actions;
        this.dao = dao;

        wire();
        loadInitialData();
    }

    private void wire() {
        actions.addAddListener(e -> onAdd());
        actions.addUpdateListener(e -> onUpdate());
        actions.addDeleteListener(e -> onDelete());
        actions.addConsultListener(e -> onConsult());

        // search wiring
        search.addSearchListener(ae -> onSearch());
        search.addRefreshListener(ae -> loadInitialData());
    }

    private void loadInitialData() {
        try {
            if (dao != null) {
                table.setProducts(dao.findAll());
            } else {
                // demo fallback (preserve existing memory content if any)
                if (memory.isEmpty()) {
                    memory.add(sampleProduct(1, "Sample product A", "Generic", 12.5, true, "Groceries"));
                    memory.add(sampleProduct(2, "Sample product B", "BrandZ", 35.0, false, "Personal hygiene"));
                }
                table.setProducts(new ArrayList<>(memory));
            }
        } catch (SQLException ex) {
            showDatabaseError("Failed to load products", ex);
            // Fall back to demo mode
            if (memory.isEmpty()) {
                memory.add(sampleProduct(1, "Sample product A", "Generic", 12.5, true, "Groceries"));
                memory.add(sampleProduct(2, "Sample product B", "BrandZ", 35.0, false, "Personal hygiene"));
            }
            table.setProducts(new ArrayList<>(memory));
        }
    }

    private void onAdd() {
        try {
            Product p = form.toProduct();
            
            // Validate product before adding to database
            ValidationResult validation = validateProduct(p);
            if (!validation.isValid()) {
                showValidationErrors("Cannot add product", validation);
                return;
            }
            
            if (dao != null) {
                dao.create(p);
                table.setProducts(dao.findAll());
            } else {
                // give it a pseudo id and append
                p.setId((int) (Math.random() * 9000) + 100);
                memory.add(p);
                table.setProducts(new ArrayList<>(memory));
            }
            form.clear();
            JOptionPane.showMessageDialog(null, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().contains("Duplicate entry")) {
                JOptionPane.showMessageDialog(null, 
                    "Product with ID " + form.toProduct().getId() + " already exists.\nPlease use a different ID.", 
                    "Duplicate Product", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                showDatabaseError("Failed to add product", ex);
            }
        } catch (HeadlessException ex) {
            showError("Add failed", ex);
        }
    }

    private void onUpdate() {
        try {
            Product edited = form.toProduct();
            if (edited.getId() == 0) {
                JOptionPane.showMessageDialog(null, "No product ID present; use Add for new products");
                return;
            }
            
            // Validate product before updating
            ValidationResult validation = validateProduct(edited);
            if (!validation.isValid()) {
                showValidationErrors("Cannot update product", validation);
                return;
            }
            
            if (dao != null) {
                boolean ok = dao.update(edited);
                if (!ok) {
                    JOptionPane.showMessageDialog(null, "Update reported no rows changed (maybe ID not found)", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                table.setProducts(dao.findAll());
            } else {
                memory.removeIf(p -> p.getId() == edited.getId());
                memory.add(edited);
                table.setProducts(new ArrayList<>(memory));
                JOptionPane.showMessageDialog(null, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            form.clear();
        } catch (SQLException ex) {
            showDatabaseError("Failed to update product", ex);
        } catch (HeadlessException ex) {
            showError("Update failed", ex);
        }
    }

    private void onDelete() {
        Product sel = table.getSelected();
        if (sel == null) { JOptionPane.showMessageDialog(null, "Select a row to delete"); return; }
        if (JOptionPane.showConfirmDialog(null, "Delete selected product?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                if (dao != null) {
                    boolean ok = dao.deleteById(sel.getId());
                    if (!ok) {
                        JOptionPane.showMessageDialog(null, "Delete reported no rows changed (maybe ID not found)", "Warning", JOptionPane.WARNING_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                    table.setProducts(dao.findAll());
                } else {
                    memory.removeIf(p -> p.getId() == sel.getId());
                    table.setProducts(new ArrayList<>(memory));
                    JOptionPane.showMessageDialog(null, "Product deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                showDatabaseError("Failed to delete product", ex);
            }
        }
    }

    private void onConsult() {
        Product sel = table.getSelected();
        if (sel == null) { JOptionPane.showMessageDialog(null, "Select a row to consult"); return; }
        try {
            if (dao != null) {
                Optional<Product> maybe = dao.findById(sel.getId());
                maybe.ifPresentOrElse(
                    p -> form.fromProduct(p),
                    () -> JOptionPane.showMessageDialog(null, "Product not found in DB", "Not Found", JOptionPane.WARNING_MESSAGE)
                );
            } else {
                form.fromProduct(sel);
            }
        } catch (SQLException ex) {
            showDatabaseError("Failed to consult product", ex);
        }
    }

    private void onSearch() {
        String q = search.getQuery().trim().toLowerCase();
        if (q.isBlank()) { loadInitialData(); return; }

        try {
            if (dao != null) {
                table.setProducts(dao.searchByDescription(q));
            } else {
                List<Product> filtered = memory.stream()
                        .filter(pr -> (pr.getDescription() != null && pr.getDescription().toLowerCase().contains(q)) ||
                                      (pr.getBrand() != null && pr.getBrand().toLowerCase().contains(q)))
                        .toList();
                table.setProducts(filtered);
            }
        } catch (SQLException ex) {
            showDatabaseError("Failed to search products", ex);
        }
    }

    private Product sampleProduct(int id, String desc, String brand, double price, boolean active, String category) {
        Product p = new Product();
        p.setId(id);
        p.setDescription(desc);
        p.setBrand(brand);
        p.setContent("1 unit");
        // adapt if Product.price is BigDecimal in your model; for this demo assume double setter exists
        try {
            // prefer double -> BigDecimal safe setter
            p.setPrice(price);
        } catch (Throwable ignore) { /* if your Product uses BigDecimal, adapt accordingly */ }
        p.setActive(active);
        p.setCategory(category);
        p.setDateMade(new java.util.Date());
        p.setExpirationDate((java.util.Date) null);
        return p;
    }

    /**
     * Validates a product using ValidationUtils.
     * Converts java.util.Date to LocalDate for validation.
     */
    private ValidationResult validateProduct(Product p) {
        // Convert dates from java.util.Date to LocalDate for validation
        java.time.LocalDate dateMade = null;
        java.time.LocalDate expirationDate = null;
        
        if (p.getDateMade() != null) {
            dateMade = p.getDateMade().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        
        if (p.getExpirationDate() != null) {
            expirationDate = p.getExpirationDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        
        // Convert boolean status to string for validation
        String status = p.isActive() ? "Active" : "Inactive";
        
        return ValidationUtils.validateProductFields(
                p.getId(),
                p.getDescription(),
                p.getBrand(),
                p.getContent(),
                p.getCategory(),
                p.getPrice(),
                status,
                dateMade,
                expirationDate
        );
    }

    /**
     * Shows validation errors in a user-friendly dialog.
     */
    private void showValidationErrors(String title, ValidationResult validation) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append(":\n\n");
        sb.append("Please fix the following errors:\n");
        for (String error : validation.getErrors()) {
            sb.append("• ").append(error).append("\n");
        }
        JOptionPane.showMessageDialog(null, sb.toString(), "Validation Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows database-related errors with helpful context.
     */
    private void showDatabaseError(String title, SQLException ex) {
        StringBuilder message = new StringBuilder();
        message.append(title).append("\n\n");
        
        String errorMsg = ex.getMessage();
        if (errorMsg != null) {
            if (errorMsg.contains("Access denied")) {
                message.append("Database access denied.\n");
                message.append("Check your database credentials in config/db.properties");
            } else if (errorMsg.contains("Unknown database")) {
                message.append("Database does not exist.\n");
                message.append("Create the database and run schema.sql");
            } else if (errorMsg.contains("Communications link failure")) {
                message.append("Cannot connect to MySQL server.\n");
                message.append("Make sure MySQL is running");
            } else if (errorMsg.contains("Duplicate entry")) {
                message.append("Duplicate ID detected.\n");
                message.append("This product ID already exists");
            } else {
                message.append("Database error: ").append(errorMsg);
            }
        }
        
        message.append("\n\nSwitching to demo mode (in-memory storage).");
        
        JOptionPane.showMessageDialog(null, message.toString(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(null, title + ":\n" + ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }
}
