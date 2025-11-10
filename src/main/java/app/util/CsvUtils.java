package app.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import app.model.Product;

/**
 * Utility class for exporting and importing products to/from CSV files.
 */
public final class CsvUtils {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final String CSV_SEPARATOR = ",";
    private static final String CSV_HEADER = "ID,Description,Brand,Content,Price,Category,Status,DateMade,ExpirationDate";

    private CsvUtils() {}

    /**
     * Exports a list of products to a CSV file.
     */
    public static void exportToCsv(List<Product> products, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write header
            writer.write(CSV_HEADER);
            writer.newLine();

            // Write products
            for (Product p : products) {
                writer.write(productToCsvLine(p));
                writer.newLine();
            }
        }
    }

    /**
     * Imports products from a CSV file.
     */
    public static List<Product> importFromCsv(File file) throws IOException {
        List<Product> products = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // Skip header
            
            while ((line = reader.readLine()) != null) {
                try {
                    Product p = csvLineToProduct(line);
                    if (p != null) {
                        products.add(p);
                    }
                } catch (Exception e) {
                    System.err.println(String.format("Error parsing line: %s - %s", line, e.getMessage()));
                }
            }
        }
        
        return products;
    }

    /**
     * Converts a product to a CSV line.
     */
    private static String productToCsvLine(Product p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.getId()).append(CSV_SEPARATOR);
        sb.append(escape(p.getDescription())).append(CSV_SEPARATOR);
        sb.append(escape(p.getBrand())).append(CSV_SEPARATOR);
        sb.append(escape(p.getContent())).append(CSV_SEPARATOR);
        sb.append(p.getPrice()).append(CSV_SEPARATOR);
        sb.append(escape(p.getCategory())).append(CSV_SEPARATOR);
        sb.append(p.isActive() ? "Active" : "Inactive").append(CSV_SEPARATOR);
        sb.append(p.getDateMade() != null ? DATE_FORMAT.format(p.getDateMade()) : "").append(CSV_SEPARATOR);
        sb.append(p.getExpirationDate() != null ? DATE_FORMAT.format(p.getExpirationDate()) : "");
        return sb.toString();
    }

    /**
     * Converts a CSV line to a product.
     */
    private static Product csvLineToProduct(String line) {
        String[] parts = line.split(CSV_SEPARATOR, -1);
        if (parts.length < 9) return null;

        Product p = new Product();
        try {
            p.setId(Integer.parseInt(parts[0].trim()));
            p.setDescription(unescape(parts[1]));
            p.setBrand(unescape(parts[2]));
            p.setContent(unescape(parts[3]));
            p.setPrice(Double.parseDouble(parts[4].trim()));
            p.setCategory(unescape(parts[5]));
            p.setActive("Active".equalsIgnoreCase(parts[6].trim()));
            
            if (!parts[7].trim().isEmpty()) {
                p.setDateMade(DATE_FORMAT.parse(parts[7].trim()));
            }
            
            if (!parts[8].trim().isEmpty()) {
                p.setExpirationDate(DATE_FORMAT.parse(parts[8].trim()));
            }
        } catch (NumberFormatException | ParseException e) {
            System.err.println("Error parsing product data: " + e.getMessage());
            return null;
        }

        return p;
    }

    /**
     * Escapes special characters in CSV fields.
     */
    private static String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * Unescapes CSV fields.
     */
    private static String unescape(String value) {
        if (value == null) return "";
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }
}
