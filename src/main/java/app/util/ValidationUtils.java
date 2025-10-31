package app.util;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public final class ValidationUtils {
    private static final Set<String> ALLOWED_CATEGORIES = new HashSet<>(Arrays.asList(
            "Groceries",
            "Personal Hygiene",
            "Fruits & Vegetables",
            "Wines & Liquors"
    ));

    private ValidationUtils() { }

    public static boolean isValidId(int id) {
        return id >= 0 && id <= 9999;
    }

    public static boolean isValidPrice(double price) {
        return price > 0.0;
    }

    /**
     * If expiration is null -> allowed (nullable). If not null, dateMade must be strictly before expiration.
     */
    public static boolean validDates(Date dateMade, Date expiration) {
        if (dateMade == null) return false;
        if (expiration == null) return true;
        return dateMade.before(expiration);
    }

    public static boolean validCategory(String category) {
        return category != null && ALLOWED_CATEGORIES.contains(category);
    }

    /**
     * Status stored as string; here the GUI uses a boolean 'active'. This helper checks
     * whether status string corresponds to active. Not needed for DAO validation but useful.
     */
    public static boolean statusStringIndicatesActive(String status) {
        if (status == null) return false;
        String s = status.trim().toLowerCase();
        return "checked".equals(s) || "active".equals(s) || "true".equals(s);
    }
}
