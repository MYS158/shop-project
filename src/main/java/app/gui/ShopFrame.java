package app.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import app.database.DatabaseManager;
import app.database.dao.ProductDao;
import app.database.dao.ProductDaoImpl;

/**
 * UI composition only. Business logic is delegated to ShopController.
 */
public class ShopFrame extends JFrame {
    private static final Dimension MIN_DIMENSION = new Dimension(1100, 520);

    private final ProductFormPanel formPanel = new ProductFormPanel();
    private final ProductTablePanel tablePanel = new ProductTablePanel();
    private final SearchPanel searchPanel = new SearchPanel();
    private final RightButtonPanel rightButtonPanel = new RightButtonPanel();

    // DB / DAO references passed to controller (may be null)
    private DatabaseManager db;
    private ProductDao dao;

    public ShopFrame() {
        super("Shop Project - Product Catalog");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(MIN_DIMENSION);
        setSize(MIN_DIMENSION);
        setLocationRelativeTo(null);
        try {
            setIconImage(ImageIO.read(getClass().getResource("/static/icons/app.png")));
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Warning: could not load icon image: " + e.getMessage());
        }

        // attempt DB init; swallow errors and continue with dao == null (controller will fallback)
        try {
            Properties props = loadDbProperties();
            db = DatabaseManager.fromProperties(props);
            dao = new ProductDaoImpl(db);
        } catch (Exception ex) {
            System.err.println("DB init failed: " + ex.getMessage());
            db = null;
            dao = null;
            // Show user-friendly warning
            showDatabaseConnectionWarning(ex);
        }

        // layout
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(new Color(0x07, 0x18, 0x33));
        main.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.BOTH;

        // left column: form, search, table
        c.gridx = 0; c.gridy = 0; c.weightx = 1.0; c.weighty = 0.0; c.gridheight = 1;
        formPanel.setPreferredSize(new Dimension(780, 220));
        main.add(formPanel, c);

        c.gridy = 1; c.weighty = 0.0;
        searchPanel.setPreferredSize(new Dimension(780, 64));
        main.add(searchPanel, c);

        c.gridy = 2; c.weighty = 1.0;
        main.add(tablePanel, c);

        // right column: buttons (encapsulated)
        c.gridx = 1; c.gridy = 0; c.gridheight = 3; c.weightx = 0.0; c.weighty = 1.0;
        main.add(rightButtonPanel, c);

        setContentPane(main);

        // delegate behavior to controller
        new ShopController(formPanel, tablePanel, searchPanel, rightButtonPanel, db, dao);
    }

    private Properties loadDbProperties() throws Exception {
        Properties p = new Properties();
        // try classpath
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config/db.properties")) {
            if (is != null) { p.load(is); return p; }
        }
        // try project root
        try (InputStream is = new FileInputStream("config/db.properties")) {
            p.load(is); return p;
        } catch (Exception ignored) {}
        // try env vars
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

    /**
     * Shows a user-friendly warning dialog when database connection fails.
     * The application continues in demo mode (in-memory storage).
     */
    private void showDatabaseConnectionWarning(Exception ex) {
        StringBuilder message = new StringBuilder();
        message.append("⚠️ Database Connection Failed\n\n");
        message.append("The application could not connect to the database.\n\n");
        
        // Provide specific guidance based on error type
        String errorMsg = ex.getMessage();
        if (errorMsg != null) {
            if (errorMsg.contains("Access denied")) {
                message.append("Issue: Database access denied\n");
                message.append("• Check username and password in config/db.properties\n");
                message.append("• Verify database user has proper permissions\n");
                message.append("• Run: GRANT ALL PRIVILEGES ON shopdb.* TO 'youruser'@'localhost';\n");
            } else if (errorMsg.contains("Unknown database")) {
                message.append("Issue: Database 'shopdb' does not exist\n");
                message.append("• Create the database: CREATE DATABASE shopdb;\n");
                message.append("• Run the schema.sql script to create tables\n");
            } else if (errorMsg.contains("Communications link failure")) {
                message.append("Issue: Cannot connect to MySQL server\n");
                message.append("• Make sure MySQL server is running\n");
                message.append("• Check the connection URL in config/db.properties\n");
            } else {
                message.append("Error: ").append(errorMsg).append("\n");
            }
        }
        
        message.append("\n✓ The application will run in DEMO MODE\n");
        message.append("(Data will be stored in memory only)");
        
        JOptionPane.showMessageDialog(
            this,
            message.toString(),
            "Database Connection Warning",
            JOptionPane.WARNING_MESSAGE
        );
    }

    public static void showDemo() {
        SwingUtilities.invokeLater(() -> {
            ShopFrame f = new ShopFrame();
            f.setVisible(true);
        });
    }
}