package app.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import app.model.Product;

/**
 * Dialog showing product statistics and analytics.
 */
public class StatisticsDialog extends JDialog {
    
    public StatisticsDialog(JFrame parent, List<Product> products) {
        super(parent, "Product Statistics", true);
        setSize(650, 550);
        setLocationRelativeTo(parent);
        
        // Main panel with app's dark background
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(0x07, 0x18, 0x33)); // Match app background
        
        // Title panel with gradient background
        RoundedPanel titlePanel = new RoundedPanel(12);
        titlePanel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Product Statistics Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setForeground(new Color(0x07, 0x2B, 0x4A));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Statistics panel with rounded style
        RoundedPanel statsWrapper = new RoundedPanel(12);
        statsWrapper.setLayout(new BorderLayout());
        
        JPanel statsPanel = new JPanel(new GridLayout(0, 2, 12, 12));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Calculate statistics
        int totalProducts = products.size();
        int activeProducts = (int) products.stream().filter(Product::isActive).count();
        int inactiveProducts = totalProducts - activeProducts;
        
        double totalValue = products.stream().mapToDouble(Product::getPrice).sum();
        double avgPrice = totalProducts > 0 ? totalValue / totalProducts : 0;
        double maxPrice = products.stream().mapToDouble(Product::getPrice).max().orElse(0);
        double minPrice = products.stream().mapToDouble(Product::getPrice).min().orElse(0);
        
        // Category breakdown
        Map<String, Integer> categoryCount = new HashMap<>();
        Map<String, Integer> brandCount = new HashMap<>();
        
        for (Product p : products) {
            String cat = p.getCategory() != null ? p.getCategory() : "Unknown";
            categoryCount.put(cat, categoryCount.getOrDefault(cat, 0) + 1);
            
            String brand = p.getBrand() != null ? p.getBrand() : "Unknown";
            brandCount.put(brand, brandCount.getOrDefault(brand, 0) + 1);
        }
        
        // Add stat cards with app's color scheme
        statsPanel.add(createStatCard("Total Products", String.valueOf(totalProducts), new Color(0x1F, 0x80, 0xFF)));
        statsPanel.add(createStatCard("Active Products", String.valueOf(activeProducts), new Color(0x43, 0xA0, 0x47)));
        statsPanel.add(createStatCard("Inactive Products", String.valueOf(inactiveProducts), new Color(0xE5, 0x73, 0x73)));
        statsPanel.add(createStatCard("Total Value", String.format("$%.2f", totalValue), new Color(0xFF, 0x93, 0x00)));
        statsPanel.add(createStatCard("Average Price", String.format("$%.2f", avgPrice), new Color(0x9C, 0x27, 0xB0)));
        statsPanel.add(createStatCard("Max Price", String.format("$%.2f", maxPrice), new Color(0x00, 0x96, 0x88)));
        statsPanel.add(createStatCard("Min Price", String.format("$%.2f", minPrice), new Color(0x00, 0xBB, 0xD3)));
        statsPanel.add(createStatCard("Categories", String.valueOf(categoryCount.size()), new Color(0x3F, 0x51, 0xB5)));
        
        statsWrapper.add(statsPanel, BorderLayout.CENTER);
        
        JScrollPane scrollPane = new JScrollPane(statsWrapper);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Details panel with rounded style
        RoundedPanel detailsWrapper = new RoundedPanel(12);
        detailsWrapper.setLayout(new GridLayout(1, 2, 10, 10));
        detailsWrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Category breakdown
        StringBuilder catBreakdown = new StringBuilder("<html><b style='color:#072B4A;'>Top Categories:</b><br>");
        categoryCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .forEach(entry -> catBreakdown.append(String.format("<span style='color:#072B4A;'>• %s: <b>%d</b></span><br>", entry.getKey(), entry.getValue())));
        catBreakdown.append("</html>");
        
        JLabel catLabel = new JLabel(catBreakdown.toString());
        catLabel.setVerticalAlignment(SwingConstants.TOP);
        catLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Brand breakdown
        StringBuilder brandBreakdown = new StringBuilder("<html><b style='color:#072B4A;'>Top Brands:</b><br>");
        brandCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .forEach(entry -> brandBreakdown.append(String.format("<span style='color:#072B4A;'>• %s: <b>%d</b></span><br>", entry.getKey(), entry.getValue())));
        brandBreakdown.append("</html>");
        
        JLabel brandLabel = new JLabel(brandBreakdown.toString());
        brandLabel.setVerticalAlignment(SwingConstants.TOP);
        brandLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        detailsWrapper.add(catLabel);
        detailsWrapper.add(brandLabel);
        
        mainPanel.add(detailsWrapper, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private JPanel createStatCard(String label, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(new Color(0xF8, 0xFC, 0xFF)); // Light blue-white
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        labelLabel.setForeground(new Color(0x07, 0x2B, 0x4A));
        labelLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(labelLabel, BorderLayout.SOUTH);
        
        card.setPreferredSize(new Dimension(160, 100));
        
        return card;
    }
}