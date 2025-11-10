package app.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationResult {
    private final List<String> errors = new ArrayList<>();

    public void addError(String error) { errors.add(error); }
    public boolean isValid() { return errors.isEmpty(); }
    public List<String> getErrors() { return Collections.unmodifiableList(errors); }

    @Override
    public String toString() {
        return String.format("ValidationResult{valid=%s, errors=%s}", isValid(), errors);
    }
}