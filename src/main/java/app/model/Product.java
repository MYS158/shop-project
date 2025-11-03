package app.model;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Represents a product in the Shop Project.
 * This class is a pure data model (POJO) used in GUI forms,
 * DAO operations, and table models.
 */
public class Product {

    private int id;
    private String description;
    private String brand;
    private String content;
    private double price;
    private boolean active;
    private String category;
    private Date dateMade;
    private Date expirationDate;

    // --- Constructors ---

    public Product() {
    }

    public Product(int id, String description, String brand, String content,
                   double price, boolean active, String category,
                   Date dateMade, Date expirationDate) {
        this.id = id;
        this.description = description;
        this.brand = brand;
        this.content = content;
        this.price = price;
        this.active = active;
        this.category = category;
        this.dateMade = dateMade;
        this.expirationDate = expirationDate;
    }

    // --- Getters & Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean getStatus() {
        return active;
    }

    public boolean setStatus(String status) {
        if (status.equalsIgnoreCase("Active")) {
            this.active = true;
            return true;
        } else if (status.equalsIgnoreCase("Inactive")) {
            this.active = false;
            return true;
        }
        return false;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDateMade() {
        return dateMade;
    }

    public void setDateMade(Date dateMade) {
        this.dateMade = dateMade;
    }

    public void setDateMade(LocalDate localDate) {
        if (localDate != null) {
            this.dateMade = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else {
            this.dateMade = null;
        }
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void setExpirationDate(LocalDate localDate) {
        if (localDate != null) {
            this.expirationDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        } else {
            this.expirationDate = null;
        }
    }

    // --- Utility methods ---

    @Override
    public String toString() {
        return String.format("Product{id=%d, desc='%s', brand='%s', price=%s, active=%s}",
                id, description, brand, price, active);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Product)) return false;
        Product other = (Product) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}