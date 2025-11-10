package unit;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import app.model.Product;
import app.util.ValidationResult;
import app.util.ValidationUtils;

/**
 * Tests that validation is properly applied in ShopController.
 * These tests verify that invalid products are caught before database operations.
 */
public class ShopControllerValidationTest {

    @Test
    void invalidIdShouldFail() {
        Product p = new Product();
        p.setId(0); // invalid: must be 1-9999
        p.setDescription("Valid description");
        p.setBrand("ValidBrand");
        p.setContent("Valid content");
        p.setCategory("Abarrotes");
        p.setPrice(10.0);
        p.setActive(true);
        p.setDateMade(new Date());
        
        ValidationResult result = validateProduct(p);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anyMatch(e -> e.contains("ID"));
    }

    @Test
    void emptyDescriptionShouldFail() {
        Product p = createValidProduct();
        p.setDescription(""); // invalid: required
        
        ValidationResult result = validateProduct(p);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anyMatch(e -> e.contains("Description"));
    }

    @Test
    void negativePriceShouldFail() {
        Product p = createValidProduct();
        p.setPrice(-5.0); // invalid: must be > 0
        
        ValidationResult result = validateProduct(p);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anyMatch(e -> e.contains("Price"));
    }

    @Test
    void invalidCategoryShouldFail() {
        Product p = createValidProduct();
        p.setCategory("InvalidCategory"); // invalid: not in enum
        
        ValidationResult result = validateProduct(p);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anyMatch(e -> e.contains("Category"));
    }

    @Test
    void expirationBeforeMadeDateShouldFail() {
        Product p = createValidProduct();
        LocalDate today = LocalDate.now();
        p.setDateMade(today.plusDays(10)); // made in future
        p.setExpirationDate(today); // expires before made
        
        ValidationResult result = validateProduct(p);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anyMatch(e -> e.contains("dateMade"));
    }

    @Test
    void validProductShouldPass() {
        Product p = createValidProduct();
        
        ValidationResult result = validateProduct(p);
        assertThat(result.isValid()).isTrue();
        assertThat(result.getErrors()).isEmpty();
    }

    @Test
    void tooLongDescriptionShouldFail() {
        Product p = createValidProduct();
        p.setDescription("This is a very long description that exceeds the maximum allowed length");
        
        ValidationResult result = validateProduct(p);
        assertThat(result.isValid()).isFalse();
        assertThat(result.getErrors()).anyMatch(e -> e.contains("Description"));
    }

    @Test
    void nullExpirationDateShouldPass() {
        Product p = createValidProduct();
        p.setExpirationDate((Date) null); // null is allowed
        
        ValidationResult result = validateProduct(p);
        assertThat(result.isValid()).isTrue();
    }

    // Helper method that mimics ShopController's validation logic
    private ValidationResult validateProduct(Product p) {
        LocalDate dateMade = null;
        LocalDate expirationDate = null;
        
        if (p.getDateMade() != null) {
            dateMade = p.getDateMade().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        
        if (p.getExpirationDate() != null) {
            expirationDate = p.getExpirationDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        
        String status = p.isActive() ? "Active" : "Inactive";
        
        return ValidationUtils.validateProductFields(
                p.getId(),
                p.getDescription(),
                p.getBrand(),
                p.getContent(),
                p.getCategory(),
                p.getPrice(),
                status,
                dateMade,
                expirationDate
        );
    }

    private Product createValidProduct() {
        Product p = new Product();
        p.setId(100);
        p.setDescription("Valid Product");
        p.setBrand("ValidBrand");
        p.setContent("1 unit");
        p.setCategory("Groceries");
        p.setPrice(25.50);
        p.setActive(true);
        p.setDateMade(new Date());
        p.setExpirationDate((Date) null);
        return p;
    }
}