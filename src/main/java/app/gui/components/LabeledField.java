package app.gui.components;


import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;


/**
* Small helper combining a label and an input component in a compact horizontal layout.
*/
public class LabeledField extends JPanel {
    private StyledLabel label;
    private JComponent field;

    public LabeledField(String labelText, JComponent field) {
        this(labelText, field, 120);
    }

    public LabeledField(String labelText, JComponent field, int labelWidth) {
        setOpaque(false);
        setLayout(new BorderLayout(6, 0));
        this.label = new StyledLabel(labelText, StyledLabel.Variant.FIELD);
        this.field = field;
        label.setPreferredSize(new Dimension(labelWidth, 24));
        add(label, BorderLayout.WEST);
        add(field, BorderLayout.CENTER);
    }

    public JComponent getFieldComponent() { return field; }
}