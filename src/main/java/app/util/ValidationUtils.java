package app.util;

import java.time.LocalDate;

import app.model.Category;

public final class ValidationUtils {
    private ValidationUtils() {}

    public static boolean isValidId(Integer id) {
        return id != null && id >= 1 && id <= 9999;
    }

    public static boolean isValidDescription(String s) {
        return s != null && !s.trim().isEmpty() && s.trim().length() <= 30;
    }

    public static boolean isValidBrand(String s) {
        return s != null && !s.trim().isEmpty() && s.trim().length() <= 30;
    }

    public static boolean isValidContent(String s) {
        return s != null && !s.trim().isEmpty() && s.trim().length() <= 30;
    }

    public static boolean isValidCategory(String s) {
        return Category.contains(s);
    }

    public static boolean isValidPrice(Double price) {
        return price != null && price > 0.0;
    }

    public static boolean isValidStatus(String s) {
        return s != null && (s.equalsIgnoreCase("Active") || s.equalsIgnoreCase("Inactive"));
    }

    public static boolean areDatesValid(LocalDate dateMade, LocalDate expirationDate) {
        if (dateMade == null) return false;
        if (expirationDate == null) return true; // nullable expiration allowed
        return dateMade.isBefore(expirationDate);
    }

    // Composite validator
    public static ValidationResult validateProductFields(
            Integer id,
            String description,
            String brand,
            String content,
            String category,
            Double price,
            String status,
            LocalDate dateMade,
            LocalDate expirationDate
    ) {
        ValidationResult r = new ValidationResult();

        if (!isValidId(id)) r.addError("ID must be integer between 1 and 9999.");
        if (!isValidDescription(description)) r.addError("Description required; max 30 chars.");
        if (!isValidBrand(brand)) r.addError("Brand required; max 30 chars.");
        if (!isValidContent(content)) r.addError("Content required; max 30 chars.");
        if (!isValidCategory(category)) r.addError("Category must be one of the allowed categories.");
        if (!isValidPrice(price)) r.addError("Price must be greater than 0.");
        if (!isValidStatus(status)) r.addError("Status must be 'Active' or 'Inactive'.");
        if (!areDatesValid(dateMade, expirationDate)) r.addError("dateMade must be before expirationDate (or expirationDate empty).");

        return r;
    }

    public static boolean validCategory(String category) {
        return Category.contains(category);
    }
}
