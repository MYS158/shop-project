package app.gui.components;


import java.text.NumberFormat;

import javax.swing.JFormattedTextField;


/**
* JFormattedTextField configured to edit currency values in the default locale.
*/
public class CurrencyField extends JFormattedTextField {
    public CurrencyField() {
        super(NumberFormat.getNumberInstance());
        setColumns(8);
        setValue(0.0);
        setToolTipText("Enter price (numeric)");
    }

    public double getDouble() {
        Object v = getValue();
        if (v instanceof Number number) return number.doubleValue();
        try { return Double.parseDouble(String.valueOf(v)); } catch (NumberFormatException e) { return 0.0; }
    }

    public void setDouble(double d) { setValue(d); }
}