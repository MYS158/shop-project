package app.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import app.gui.components.CurrencyField;
import app.gui.components.DatePickerField;
import app.gui.components.LabeledField;
import app.gui.components.StyledLabel;
import app.model.Product;

/**
 * Form panel to add / edit a Product. Uses the small component primitives from app.gui.components.
 */
public class ProductFormPanel extends RoundedPanel {
    private final JTextField idField;
    private final JTextField descField;
    private final JComboBox<String> brandCombo;
    private final JTextField contentField;
    private final CurrencyField priceField;
    private final JCheckBox activeCheck;
    private final JPanel categoryPanel;
    private final ButtonGroup categoryGroup;
    private final DatePickerField madeField;
    private final DatePickerField expField;
    private final JButton clearButton;

    public ProductFormPanel() {
        super(16);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Title
        c.gridx = 0; c.gridy = 0; c.gridwidth = 4; c.weightx = 1.0;
        StyledLabel title = new StyledLabel("Product data", StyledLabel.Variant.SECTION);
        add(title, c);

        // Row 1: ID + Description
        idField = new JTextField(); idField.setColumns(8); idField.setEditable(true);
        LabeledField idLf = new LabeledField("ID:", idField, 80);
        c.gridy = 1; c.gridwidth = 1; c.weightx = 0;
        add(idLf, c);

        descField = new JTextField(); descField.setColumns(30);
        LabeledField descLf = new LabeledField("Description:", descField, 100);
        c.gridx = 1; c.gridwidth = 3; c.weightx = 1.0;
        add(descLf, c);

        // Row 2: Brand / Content / Price
        c.gridy = 2; c.gridx = 0; c.gridwidth = 1; c.weightx = 0.2;
        brandCombo = new JComboBox<>(new String[]{"", "Generic", "Acme", "BrandX"});
        LabeledField brandLf = new LabeledField("Brand:", brandCombo, 80);
        add(brandLf, c);

        c.gridx = 1; c.gridwidth = 2; c.weightx = 0.6;
        contentField = new JTextField();
        LabeledField contentLf = new LabeledField("Content:", contentField, 80);
        add(contentLf, c);

        c.gridx = 3; c.gridwidth = 1; c.weightx = 0.2;
        priceField = new CurrencyField();
        LabeledField priceLf = new LabeledField("Price:", priceField, 64);
        add(priceLf, c);

        // Row 3: Status + Categories
        c.gridy = 3; c.gridx = 0; c.gridwidth = 1; c.weightx = 0.0;
        activeCheck = new JCheckBox("Active");
        activeCheck.setOpaque(false);
        LabeledField statusLf = new LabeledField("Status:", activeCheck, 80);
        add(statusLf, c);

        c.gridx = 1; c.gridwidth = 3; c.weightx = 1.0;
        categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        categoryPanel.setOpaque(false);
        categoryPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Category"));
        categoryGroup = new ButtonGroup();
        String[] cats = {"Groceries", "Personal hygiene", "Fruits and vegetables", "Wines and spirits"};
        for (String s: cats) {
            JRadioButton r = new JRadioButton(s);
            r.setOpaque(false);
            categoryGroup.add(r);
            categoryPanel.add(r);
        }
        add(categoryPanel, c);

        // Row 4: Dates and Clear Button
        c.gridy = 4; c.gridx = 0; c.gridwidth = 1; c.weightx = 0.5;
        madeField = new DatePickerField();
        LabeledField madeLf = new LabeledField("Date made:", madeField, 80);
        add(madeLf, c);

        c.gridx = 1; c.gridwidth = 1; c.weightx = 0.5;
        expField = new DatePickerField();
        LabeledField expLf = new LabeledField("Expiration date:", expField, 120);
        add(expLf, c);

        c.gridx = 2; c.gridwidth = 1; c.weightx = 0.5;
        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> clear());
        add(clearButton, c);
    }

    public Product toProduct() {
        Product p = new Product();
        try { p.setId(Integer.parseInt(idField.getText())); } catch (NumberFormatException ignored) {}
        p.setDescription(descField.getText());
        p.setBrand((String) brandCombo.getSelectedItem());
        p.setContent(contentField.getText());
        p.setPrice(priceField.getDouble());
        p.setActive(activeCheck.isSelected());
        // category
        for (Component comp: categoryPanel.getComponents()) {
            if (comp instanceof JRadioButton r && r.isSelected()) { p.setCategory(r.getText()); break; }
        }
        Date d1 = madeField.getDate();
        Date d2 = expField.getDate();
        p.setDateMade(d1);
        p.setExpirationDate(d2);
        return p;
    }

    public void fromProduct(Product p) {
        if (p == null) return;
        idField.setText(String.valueOf(p.getId()));
        descField.setText(p.getDescription());
        brandCombo.setSelectedItem(p.getBrand());
        contentField.setText(p.getContent());
        priceField.setDouble(p.getPrice());
        activeCheck.setSelected(p.isActive());
        for (Component comp: categoryPanel.getComponents()) if (comp instanceof JRadioButton r) r.setSelected(r.getText().equals(p.getCategory()));
        madeField.setDate(p.getDateMade());
        expField.setDate(p.getExpirationDate());
    }

    public void clear() {
        idField.setText(""); descField.setText(""); brandCombo.setSelectedIndex(0);
        contentField.setText(""); priceField.setDouble(0.0); activeCheck.setSelected(false);
        categoryGroup.clearSelection(); madeField.setDate(null); expField.setDate(null);
    }
}