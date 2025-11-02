package app.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import app.database.DatabaseManager;
import app.database.dao.ProductDao;
import app.database.dao.ProductDaoImpl;
import app.database.migration.MigrationRunner;
import app.gui.components.RoundedButton;
import app.model.Product;

/**
 * Main application frame composing the form, search and table panels plus action buttons.
 * Modified to connect to the DB and use ProductDaoImpl for CRUD.
 */
public class ShopFrame extends JFrame {
    private static final Dimension MIN_DIMENSION = new Dimension(1100, 520);
    private ProductFormPanel formPanel;
    private ProductTablePanel tablePanel;
    private SearchPanel searchPanel;

    // DB & DAO
    private DatabaseManager db;
    private ProductDao productDao;

    public ShopFrame() {
        super("Shop Project - Product Catalog");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(MIN_DIMENSION);
        setSize(MIN_DIMENSION);
        setLocationRelativeTo(null);

        // Initialize DB and DAO (safe: show errors to user)
        try {
            initDatabaseAndDao();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to initialize database:\n" + ex.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE);
            // continue in-memory mode (you may also exit)
        }

        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(new Color(0x07,0x18,0x33));
        main.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8,8,8,8);
        c.fill = GridBagConstraints.BOTH;

        // Left Column (form + search + table)
        c.gridx = 0; c.gridy = 0; c.weightx = 1.0; c.weighty = 0.0; c.gridheight = 1;
        formPanel = new ProductFormPanel();
        formPanel.setPreferredSize(new Dimension(700, 200));
        main.add(formPanel, c);

        c.gridy = 1; c.weighty = 0.0;
        searchPanel = new SearchPanel();
        searchPanel.setPreferredSize(new Dimension(700, 64));
        main.add(searchPanel, c);

        c.gridy = 2; c.weighty = 1.0;
        tablePanel = new ProductTablePanel();
        main.add(tablePanel, c);

        // Right Column (buttons)
        JPanel rightCol = new JPanel();
        rightCol.setOpaque(false);
        rightCol.setLayout(new BoxLayout(rightCol, BoxLayout.Y_AXIS));
        rightCol.setPreferredSize(new Dimension(160, 0));

        RoundedButton addBtn = new RoundedButton("Add");
        RoundedButton upBtn = new RoundedButton("Update");
        RoundedButton delBtn = new RoundedButton("Delete");
        RoundedButton conBtn = new RoundedButton("Consult");
        RoundedButton refreshBtn = new RoundedButton("Refresh");
        rightCol.add(addBtn); rightCol.add(Box.createVerticalStrut(8));
        rightCol.add(upBtn); rightCol.add(Box.createVerticalStrut(8));
        rightCol.add(delBtn); rightCol.add(Box.createVerticalStrut(8));
        rightCol.add(conBtn); rightCol.add(Box.createVerticalStrut(8));
        rightCol.add(refreshBtn);

        c.gridx = 1; c.gridy = 0; c.gridheight = 3; c.weightx = 0.0; c.weighty = 1.0;
        main.add(rightCol, c);

        setContentPane(main);

        // Load initial data from DB (or fallback to demo list)
        loadProductsFromDbOrDemo();

        // --- Button wiring using DAO ---

        addBtn.addActionListener(e -> {
            try {
                Product p = formPanel.toProduct();
                // create via DAO if available
                if (productDao != null) {
                    Product created = productDao.create(p);
                    // reload all
                    tablePanel.setProducts(productDao.findAll());
                } else {
                    // fallback: assign id and append to memory table
                    p.setId((int)(Math.random()*9000)+100);
                    List<Product> cur = tablePanel.getModel().getData(); // not recommended in prod; we keep compatibility
                    cur.add(p);
                    tablePanel.setProducts(cur);
                }
                formPanel.clear();
            } catch (SQLException ex) {
                showError("Add failed", ex);
            }
        });

        upBtn.addActionListener(e -> {
            Product sel = tablePanel.getSelected();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select a row to update"); return; }
            // populate form with selected
            formPanel.fromProduct(sel);
        });

        // Commit update action: you probably want a separate "Save" button in form; for demo we'll use same Update button to save changes
        // When user edits the form and clicks Update, this will write to DB.
        upBtn.addActionListener(e -> {
            // If product exists in form (edited), commit update
            try {
                Product edited = formPanel.toProduct();
                if (edited.getId() == 0) {
                    JOptionPane.showMessageDialog(this, "No product ID present; use Add for new products");
                    return;
                }
                if (productDao != null) {
                    boolean ok = productDao.update(edited);
                    if (!ok) JOptionPane.showMessageDialog(this, "Update reported no rows changed (maybe ID not found)");
                    tablePanel.setProducts(productDao.findAll());
                } else {
                    // fallback: update memory list
                    List<Product> cur = tablePanel.getModel().getData();
                    cur.removeIf(p -> p.getId() == edited.getId());
                    cur.add(edited);
                    tablePanel.setProducts(cur);
                }
                formPanel.clear();
            } catch (HeadlessException | SQLException ex) {
                showError("Update failed", ex);
            }
        });

        delBtn.addActionListener(e -> {
            Product sel = tablePanel.getSelected();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select a row to delete"); return; }
            if (JOptionPane.showConfirmDialog(this, "Delete selected product?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try {
                    if (productDao != null) {
                        boolean ok = productDao.deleteById(sel.getId());
                        if (!ok) JOptionPane.showMessageDialog(this, "Delete reported no rows changed (maybe ID not found)");
                        tablePanel.setProducts(productDao.findAll());
                    } else {
                        List<Product> cur = tablePanel.getModel().getData();
                        cur.removeIf(p -> p.getId() == sel.getId());
                        tablePanel.setProducts(cur);
                    }
                } catch (HeadlessException | SQLException ex) {
                    showError("Delete failed", ex);
                }
            }
        });

        conBtn.addActionListener(e -> {
            Product sel = tablePanel.getSelected();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select a row to consult"); return; }
            // If you want the latest from DB, re-fetch by id
            try {
                if (productDao != null) {
                    productDao.findById(sel.getId()).ifPresentOrElse(
                        p -> JOptionPane.showMessageDialog(this, p.toString(), "Product details", JOptionPane.INFORMATION_MESSAGE),
                        () -> JOptionPane.showMessageDialog(this, "Product not found in DB")
                    );
                } else {
                    JOptionPane.showMessageDialog(this, sel.toString(), "Product details", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (HeadlessException | SQLException ex) {
                showError("Consult failed", ex);
            }
        });

        refreshBtn.addActionListener(e -> {
            loadProductsFromDbOrDemo();
        });

        searchPanel.addSearchListener(ae -> {
            String q = searchPanel.getQuery().toLowerCase();
            if (q.isBlank()) { loadProductsFromDbOrDemo(); return; }
            try {
                if (productDao != null) {
                    // use search in DAO (searchByDescription implemented earlier)
                    tablePanel.setProducts(productDao.searchByDescription(q));
                } else {
                    List<Product> cur = tablePanel.getModel().getData();
                    List<Product> filtered = cur.stream()
                        .filter(pr -> (pr.getDescription()!=null && pr.getDescription().toLowerCase().contains(q)) ||
                                      (pr.getBrand()!=null && pr.getBrand().toLowerCase().contains(q)))
                        .toList();
                    tablePanel.setProducts(filtered);
                }
            } catch (SQLException ex) {
                showError("Search failed", ex);
            }
        });

        searchPanel.addRefreshListener(ae -> loadProductsFromDbOrDemo());
    }

    // -------------------- Helpers --------------------

    /**
     * Initialize DatabaseManager and ProductDaoImpl.
     * Loads properties from config/db.properties on classpath or project root.
     * Also runs schema migration once.
     */
    private void initDatabaseAndDao() throws Exception {
        Properties props = loadDbProperties();

        // DatabaseManager.fromProperties expects keys: jdbc.url, jdbc.username, jdbc.password
        // For backward compatibility we accept db.url / db.username as well.
        Properties normalized = new Properties();
        if (props.getProperty("jdbc.url") != null) normalized.setProperty("jdbc.url", props.getProperty("jdbc.url"));
        else if (props.getProperty("db.url") != null) normalized.setProperty("jdbc.url", props.getProperty("db.url"));

        if (props.getProperty("jdbc.username") != null) normalized.setProperty("jdbc.username", props.getProperty("jdbc.username"));
        else if (props.getProperty("db.username") != null) normalized.setProperty("jdbc.username", props.getProperty("db.username"));

        if (props.getProperty("jdbc.password") != null) normalized.setProperty("jdbc.password", props.getProperty("jdbc.password"));
        else if (props.getProperty("db.password") != null) normalized.setProperty("jdbc.password", props.getProperty("db.password"));

        // If the file used "db.url" with jdbc:mysql... then normalized will contain jdbc.url
        // System.out.println("Loaded DB props: " + normalized.getProperty("jdbc.url") + " user=" + normalized.getProperty("jdbc.username"));
        db = DatabaseManager.fromProperties(normalized);
        /*
        try (Connection c = db.getConnection()) {
            java.sql.Statement st = c.createStatement();
            try (java.sql.ResultSet rs = st.executeQuery("SELECT CURRENT_USER(), DATABASE()")) {
                if (rs.next()) {
                    System.out.println("App DB test -> auth: " + rs.getString(1) + " database: " + rs.getString(2));
                    JOptionPane.showMessageDialog(this, "DB test ok: " + rs.getString(1) + " -> " + rs.getString(2));
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "DB test FAILED: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            throw ex;
        }
        */

        // run schema (safe: ignore if already exists)
        MigrationRunner runner = new MigrationRunner(db);
        runner.runSchema();

        // create DAO
        productDao = new ProductDaoImpl(db);
    }

    private Properties loadDbProperties() throws Exception {
        Properties p = new Properties();

        // try classpath first (config/db.properties)
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config/db.properties")) {
            if (is != null) {
                p.load(is);
                return p;
            }
        }

        // fallback: try project root config/db.properties
        try (InputStream is = new FileInputStream("config/db.properties")) {
            p.load(is);
            return p;
        } catch (Exception ignored) { }

        // fallback: attempt environment variables (optional)
        String url = System.getenv("SHOP_JDBC_URL");
        String user = System.getenv("SHOP_JDBC_USER");
        String pass = System.getenv("SHOP_JDBC_PASS");
        if (url != null) {
            p.setProperty("jdbc.url", url);
            if (user != null) p.setProperty("jdbc.username", user);
            if (pass != null) p.setProperty("jdbc.password", pass);
            return p;
        }

        throw new IllegalStateException("Could not find DB config: place config/db.properties on classpath or project root");
    }

    private void loadProductsFromDbOrDemo() {
        try {
            if (productDao != null) {
                List<Product> all = productDao.findAll();
                tablePanel.setProducts(all);
            } else {
                // fallback demo data if db not available
                List<Product> demo = Arrays.asList(
                    sampleProduct(1, "Sample product A", "Generic", 12.5, true, "Abarrotes"),
                    sampleProduct(2, "Sample product B", "BrandZ", 35.0, false, "Personal Hygiene")
                );
                tablePanel.setProducts(demo);
            }
        } catch (SQLException ex) {
            showError("Failed to load products", ex);
        }
    }

    private Product sampleProduct(int id, String desc, String brand, double price, boolean active, String category) {
        Product p = new Product();
        p.setId(id);
        p.setDescription(desc);
        p.setBrand(brand);
        p.setContent("1 unit");
        p.setPrice(price);
        p.setActive(active);
        p.setCategory(category);
        p.setDateMade(new Date());
        p.setExpirationDate(null);
        return p;
    }

    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(this, title + ":\n" + ex.getMessage(), title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showDemo() {
        SwingUtilities.invokeLater(() -> {
            ShopFrame f = new ShopFrame();
            f.setVisible(true);
        });
    }
}