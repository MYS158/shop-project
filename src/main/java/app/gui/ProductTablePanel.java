package app.gui;

import app.model.Product;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Table panel showing product results. Backed by an in-memory list for now.
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

        // Price renderer
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(4).setCellRenderer(right);

        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);
    }

    public void setProducts(List<Product> products) { model.setProducts(products); }
    public Product getSelected() { int r = table.getSelectedRow(); if (r < 0) return null; return model.getAt(r); }
    public void refresh() { model.fireTableDataChanged(); }

    static class ProductTableModel extends AbstractTableModel {
        private final String[] cols = {"ID","Description","Brand","Content","Price","Category","Status","Date made","Expiration"};
        private final List<Product> data = new ArrayList<>();
        private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        public void setProducts(List<Product> p) { data.clear(); if (p != null) data.addAll(p); fireTableDataChanged(); }
        public Product getAt(int r) { return data.get(r); }

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
    }
}