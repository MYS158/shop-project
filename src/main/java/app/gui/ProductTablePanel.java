package app.gui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import app.model.Product;

/**
 * Table panel showing product results. Backed by an in-memory list for now.
 * Supports double-click to load product into form.
 */
public class ProductTablePanel extends RoundedPanel {
    private final JTable table;
    private final ProductTableModel model;

    public ProductTablePanel() {
        super(12);
        setLayout(new BorderLayout());
        model = new ProductTableModel();
        table = new JTable(model);
        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        // Price renderer
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(right);

        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);
    }

    /**
     * Adds a double-click listener to the table.
     * When a row is double-clicked, the consumer is invoked with the selected product.
     */
    public void addDoubleClickListener(Consumer<Product> onDoubleClick) {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Product selected = getSelected();
                    if (selected != null) {
                        onDoubleClick.accept(selected);
                    }
                }
            }
        });
    }

    public void setProducts(List<Product> products) { model.setProducts(products); }
    public Product getSelected() { int r = table.getSelectedRow(); if (r < 0) return null; return model.getAt(r); }
    public void refresh() { model.fireTableDataChanged(); }

    public ProductTableModel getModel() { return model; }

    public static class ProductTableModel extends AbstractTableModel {
        private final String[] cols = {"ID", "Description", "Brand", "Content", "Price", "Category", "Status", "Date made", "Expiration"};
        private final List<Product> data = new ArrayList<>();
        private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        public void setProducts(List<Product> p) { data.clear(); if (p != null) data.addAll(p); fireTableDataChanged(); }
        public Product getAt(int r) { return data.get(r); }
        public List<Product> getData() { return data; }

        @Override public int getRowCount() { return data.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c) { return cols[c]; }
        @Override public Object getValueAt(int r, int c) {
            Product p = data.get(r);
            return switch (c) {
                case 0 -> p.getId();
                case 1 -> p.getDescription();
                case 2 -> p.getBrand();
                case 3 -> p.getContent();
                case 4 -> p.getPrice();
                case 5 -> p.getCategory();
                case 6 -> p.isActive() ? "Active" : "Inactive";
                case 7 -> p.getDateMade() != null ? sdf.format(p.getDateMade()) : "";
                case 8 -> p.getExpirationDate() != null ? sdf.format(p.getExpirationDate()) : "";
                default -> "";
            };
        }
        @Override public boolean isCellEditable(int r, int c) { return false; }
    }
}