package unit;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import app.util.ValidationResult;
import app.util.ValidationUtils;

public class ValidationUtilsTest {

    @Test
    void validId() {
        assertThat(ValidationUtils.isValidId(1)).isTrue();
        assertThat(ValidationUtils.isValidId(9999)).isTrue();
        assertThat(ValidationUtils.isValidId(0)).isFalse();
        assertThat(ValidationUtils.isValidId(10000)).isFalse();
    }

    @Test
    void priceValidation() {
        assertThat(ValidationUtils.isValidPrice(0.01)).isTrue();
        assertThat(ValidationUtils.isValidPrice(0.0)).isFalse();
        assertThat(ValidationUtils.isValidPrice(-5.0)).isFalse();
        assertThat(ValidationUtils.isValidPrice(null)).isFalse();
    }

    @Test
    void dateValidation() {
        LocalDate d1 = LocalDate.of(2024,1,1);
        LocalDate d2 = LocalDate.of(2024,1,2);
        assertThat(ValidationUtils.areDatesValid(d1, d2)).isTrue();
        assertThat(ValidationUtils.areDatesValid(d2, d1)).isFalse();
        assertThat(ValidationUtils.areDatesValid(d1, null)).isTrue();
    }

    @Test
    void compositeValidation() {
        ValidationResult r = ValidationUtils.validateProductFields(
                5,
                "Milk 1L",
                "BrandX",
                "1L bottle",
                "Groceries",
                10.5,
                "Active",
                LocalDate.now().minusDays(2),
                LocalDate.now().plusDays(30)
        );
        assertThat(r.isValid()).isTrue();
    }
}