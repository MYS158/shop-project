package app.gui;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import app.database.DatabaseManager;
import app.database.dao.ProductDao;
import app.model.Product;
import app.util.CsvUtils;
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
 *  - handle export/import/statistics operations
 */
public class ShopController {
    private final ProductFormPanel form;
    private final ProductTablePanel table;
    private final SearchPanel search;
    private final RightButtonPanel actions;
    private final UtilityButtonPanel utilityActions;
    private final ProductDao dao;

    // fallback in-memory store when dao == null
    private final List<Product> memory = new ArrayList<>();

    public ShopController(ProductFormPanel form, ProductTablePanel table, SearchPanel search,
                          RightButtonPanel actions, UtilityButtonPanel utilityActions, DatabaseManager db, ProductDao dao) {
        this.form = form;
        this.table = table;
        this.search = search;
        this.actions = actions;
        this.utilityActions = utilityActions;
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
        
        // double-click to load product into form
        table.addDoubleClickListener(product -> form.fromProduct(product));
        
        // utility actions
        utilityActions.addExportListener(e -> onExport());
        utilityActions.addImportListener(e -> onImport());
        utilityActions.addStatsListener(e -> onStats());
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
                    String.format("Product with ID %d already exists.\nPlease use a different ID.", form.toProduct().getId()), 
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
        String q = search.getQuery().trim();
        if (q.isBlank()) { loadInitialData(); return; }

        String searchType = search.getSearchType();
        String lowerQuery = q.toLowerCase();

        try {
            if (dao != null) {
                // For database mode, get all and filter (since DAO only has searchByDescription)
                List<Product> allProducts = dao.findAll();
                List<Product> filtered = allProducts.stream()
                        .filter(pr -> matchesSearchCriteria(pr, lowerQuery, searchType))
                        .toList();
                table.setProducts(filtered);
            } else {
                // For demo mode, implement multi-criteria search
                List<Product> filtered = memory.stream()
                        .filter(pr -> matchesSearchCriteria(pr, lowerQuery, searchType))
                        .toList();
                table.setProducts(filtered);
            }
        } catch (SQLException ex) {
            showDatabaseError("Failed to search products", ex);
        }
    }

    /**
     * Checks if a product matches the search criteria based on the selected field.
     */
    private boolean matchesSearchCriteria(Product pr, String query, String searchType) {
        return switch (searchType) {
            case "Description" -> pr.getDescription() != null && pr.getDescription().toLowerCase().contains(query);
            case "Brand" -> pr.getBrand() != null && pr.getBrand().toLowerCase().contains(query);
            case "Category" -> pr.getCategory() != null && pr.getCategory().toLowerCase().contains(query);
            case "ID" -> {
                try {
                    int searchId = Integer.parseInt(query);
                    yield pr.getId() == searchId;
                } catch (NumberFormatException e) {
                    yield false;
                }
            }
            default -> // "All" - search across all fields
                    (pr.getDescription() != null && pr.getDescription().toLowerCase().contains(query)) ||
                    (pr.getBrand() != null && pr.getBrand().toLowerCase().contains(query)) ||
                    (pr.getCategory() != null && pr.getCategory().toLowerCase().contains(query)) ||
                    (pr.getContent() != null && pr.getContent().toLowerCase().contains(query)) ||
                    String.valueOf(pr.getId()).contains(query);
        };
    }

    private Product sampleProduct(int id, String desc, String brand, double price, boolean active, String category) {
        Product p = new Product();
        p.setId(id);
        p.setDescription(desc);
        p.setBrand(brand);
        p.setContent("1 unit");
        // adapt if Product.price is BigDecimal in model; for this demo double setter exists
        try {
            // prefer double -> BigDecimal safe setter
            p.setPrice(price);
        } catch (Throwable ignore) { /* if BigDecimal is used in Product, adapt accordingly */ }
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
        JOptionPane.showMessageDialog(null, String.format("%s:\n%s", title, ex.getMessage()), title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Exports all products to a CSV file.
     */
    private void onExport() {
        try {
            List<Product> products = dao != null ? dao.findAll() : new ArrayList<>(memory);
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Products to CSV");
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
            fileChooser.setSelectedFile(new File("products_export.csv"));
            
            int result = fileChooser.showSaveDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (!file.getName().endsWith(".csv")) {
                    file = new File(String.format("%s.csv", file.getAbsolutePath()));
                }
                
                CsvUtils.exportToCsv(products, file);
                JOptionPane.showMessageDialog(null, 
                    String.format("Successfully exported %d products to:\n%s", products.size(), file.getAbsolutePath()),
                    "Export Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (HeadlessException | IOException | SQLException ex) {
            showError("Export failed", ex);
        }
    }

    /**
     * Imports products from a CSV file.
     */
    private void onImport() {
        int confirm = JOptionPane.showConfirmDialog(null,
                "Import will add products from CSV file.\nContinue?",
                "Confirm Import",
                JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) return;
        
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Import Products from CSV");
            fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
            
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                List<Product> imported = CsvUtils.importFromCsv(file);
                
                int successCount = 0;
                int errorCount = 0;
                
                for (Product p : imported) {
                    try {
                        if (dao != null) {
                            dao.create(p);
                        } else {
                            memory.add(p);
                        }
                        successCount++;
                    } catch (SQLException e) {
                        errorCount++;
                        System.err.println(String.format("Failed to import product ID %d: %s", p.getId(), e.getMessage()));
                    }
                }
                
                loadInitialData();
                
                JOptionPane.showMessageDialog(null,
                    String.format("""
                    Import completed!
                    Successfully imported: %d
                    Failed: %d""", successCount, errorCount),
                    "Import Complete",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (HeadlessException | IOException ex) {
            showError("Import failed", ex);
        }
    }

    /**
     * Shows statistics dialog with product analytics.
     */
    private void onStats() {
        try {
            List<Product> products = dao != null ? dao.findAll() : new ArrayList<>(memory);
            
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(table);
            StatisticsDialog dialog = new StatisticsDialog(parentFrame, products);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            showError("Failed to load statistics", ex);
        }
    }
}
