package app.gui.components;

import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;


/**
* JFormattedTextField configured to edit currency values in the default locale.
* Only accepts numeric input, decimal point, and control keys.
*/
public class CurrencyField extends JFormattedTextField {
    public CurrencyField() {
        super(NumberFormat.getNumberInstance());
        setColumns(16);
        setValue(0.0);
        setToolTipText("Enter price (numeric)");
        
        // Block non-numeric character input
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Allow: digits, decimal point, minus sign, backspace, delete
                if (!Character.isDigit(c) && c != '.' && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume(); // Block the character
                    Toolkit.getDefaultToolkit().beep(); // Provide audio feedback
                }
            }
        });
    }

    public double getDouble() {
        Object v = getValue();
        if (v instanceof Number number) return number.doubleValue();
        try { return Double.parseDouble(String.valueOf(v)); } catch (NumberFormatException e) { return 0.0; }
    }

    public void setDouble(double d) { setValue(d); }
}