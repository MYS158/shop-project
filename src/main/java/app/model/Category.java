package app.model;

public enum Category {
    GROCERIES("Groceries"),
    PERSONAL_HYGIENE("Personal Hygiene"),
    FRUITS_VEGETABLES("Fruits & Vegetables"),
    WINES_LIQUORS("Wines & Liquors");

    private final String label;
    Category(String label) { this.label = label; }
    @Override public String toString() { return label; }

    public static boolean contains(String s) {
        if (s == null) return false;
        for (Category c : values()) if (c.label.equalsIgnoreCase(s.trim())) return true;
        return false;
    }
}