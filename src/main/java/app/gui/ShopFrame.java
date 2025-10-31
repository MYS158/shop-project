package app.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import app.gui.components.RoundedButton;
import app.model.Product;

/**
 * Main application frame composing the form, search and table panels plus action buttons.
 */
public class ShopFrame extends JFrame {
    private static final Dimension MIN_DIMENSION = new Dimension(1100, 520);
    private ProductFormPanel formPanel;
    private ProductTablePanel tablePanel;
    private SearchPanel searchPanel;

    public ShopFrame() {
        super("Shop Project - Product Catalog");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(MIN_DIMENSION);
        setSize(MIN_DIMENSION);
        setLocationRelativeTo(null);

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

        RoundedButton addBtn = new RoundedButton("<html>Add</html>");
        RoundedButton upBtn = new RoundedButton("<html>Update</html>");
        RoundedButton delBtn = new RoundedButton("<html>Delete</html>");
        RoundedButton conBtn = new RoundedButton("<html>Consult</html>");
        rightCol.add(addBtn); rightCol.add(Box.createVerticalStrut(8));
        rightCol.add(upBtn); rightCol.add(Box.createVerticalStrut(8));
        rightCol.add(delBtn); rightCol.add(Box.createVerticalStrut(8));
        rightCol.add(conBtn);

        c.gridx = 1; c.gridy = 0; c.gridheight = 3; c.weightx = 0.0; c.weighty = 1.0;
        main.add(rightCol, c);

        setContentPane(main);

        // demo wiring: add some sample products and wire buttons
        List<Product> demo = new ArrayList<>();
        Product p1 = new Product(); p1.setId(1); p1.setDescription("Sample product A"); p1.setBrand("Generic"); p1.setPrice(12.5); p1.setActive(true); p1.setCategory("Groceries"); p1.setDateMade(new Date());
        demo.add(p1);
        tablePanel.setProducts(demo);

        addBtn.addActionListener(e -> {
            Product p = formPanel.toProduct();
            // For demo: assign id and append
            p.setId((int)(Math.random()*9000)+100);
            demo.add(p);
            tablePanel.setProducts(demo);
            formPanel.clear();
        });

        upBtn.addActionListener(e -> {
            Product sel = tablePanel.getSelected();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select a row to update"); return; }
            // populate form
            formPanel.fromProduct(sel);
        });

        delBtn.addActionListener(e -> {
            Product sel = tablePanel.getSelected();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select a row to delete"); return; }
            if (JOptionPane.showConfirmDialog(this, "Delete selected product?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                demo.remove(sel);
                tablePanel.setProducts(demo);
            }
        });

        conBtn.addActionListener(e -> {
            Product sel = tablePanel.getSelected();
            if (sel == null) { JOptionPane.showMessageDialog(this, "Select a row to consult"); return; }
            JOptionPane.showMessageDialog(this, sel.toString(), "Product details", JOptionPane.INFORMATION_MESSAGE);
        });

        searchPanel.addSearchListener(ae -> {
            String q = searchPanel.getQuery().toLowerCase();
            if (q.isBlank()) { tablePanel.setProducts(demo); return; }
            List<Product> filtered = new ArrayList<>();
            for (Product pr: demo) if ((pr.getDescription()!=null && pr.getDescription().toLowerCase().contains(q)) || (pr.getBrand()!=null && pr.getBrand().toLowerCase().contains(q))) filtered.add(pr);
            tablePanel.setProducts(filtered);
        });

        searchPanel.addRefreshListener(ae -> tablePanel.setProducts(demo));
    }

    public static void showDemo() {
        SwingUtilities.invokeLater(() -> {
            ShopFrame f = new ShopFrame();
            f.setVisible(true);
        });
    }
}