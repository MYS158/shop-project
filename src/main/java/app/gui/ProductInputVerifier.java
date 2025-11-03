package app.gui;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

import app.util.ValidationUtils;

public class ProductInputVerifier extends InputVerifier {
    @Override
    public boolean verify(JComponent input) {
        if (input instanceof JTextField jTextField) {
            String name = input.getName();
            String txt = jTextField.getText();
            if (txt == null) return false;

            switch (name) {
                case "id" -> {
                    try { return ValidationUtils.isValidId(Integer.valueOf(txt.trim())); }
                    catch (NumberFormatException e) { return false; }
                }
                case "price" -> {
                    try { return ValidationUtils.isValidPrice(Double.valueOf(txt.trim())); }
                    catch (NumberFormatException e) { return false; }
                }
                case "description" -> {
                    return ValidationUtils.isValidDescription(txt);
                }
                case "brand" -> {
                    return ValidationUtils.isValidBrand(txt);
                }
                case "content" -> {
                    return ValidationUtils.isValidContent(txt);
                }
                default -> {
                    return true;
                }
            }
        }
        return true;
    }
}
