package app.gui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

/**
* Search field with placeholder behavior and small round action buttons.
* Supports multi-criteria search by field type.
*/
public class SearchField extends JPanel {
    private final JTextField textField;
    private final RoundedButton searchBtn, refreshBtn;
    private final JComboBox<String> searchTypeCombo;

    public SearchField() {
        setOpaque(false);
        setLayout(new BorderLayout(8, 0));
        setPreferredSize(new Dimension(0, 36)); // Set fixed height

        JPanel left = new JPanel(new BorderLayout(6, 0));
        left.setOpaque(false);

        searchTypeCombo = new JComboBox<>(new String[]{"All", "Description", "Brand", "Category", "ID"});
        searchTypeCombo.setPreferredSize(new Dimension(140, 32));
        searchTypeCombo.setToolTipText("Select search field");
        left.add(searchTypeCombo, BorderLayout.WEST);

        textField = new JTextField();
        textField.setPreferredSize(new Dimension(0, 32)); // Set fixed height
        textField.setToolTipText("Search by description, brand, category, id...");
        left.add(textField, BorderLayout.CENTER);

        add(left, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
        actions.setOpaque(false);

        searchBtn = new RoundedButton("", "/static/icons/search.png");
        searchBtn.setToolTipText("Search (Enter)");
        searchBtn.setFixedWidth(36);
        searchBtn.setFixedHeight(36);
        searchBtn.setBg(new Color(0x00, 0x4E, 0x7A));

        refreshBtn = new RoundedButton("", "/static/icons/refresh.png");
        refreshBtn.setToolTipText("Refresh");
        refreshBtn.setFixedWidth(36);
        refreshBtn.setFixedHeight(36);
        refreshBtn.setBg(new Color(0x00, 0x4E, 0x7A));
        
        // Add Enter key support for search
        textField.addActionListener(e -> searchBtn.doClick());

        /* 
        refreshBtn = new JButton();
        refreshBtn.setToolTipText("Refresh");
        refreshBtn.setPreferredSize(new Dimension(36, 36));
        refreshBtn.setText("⟳");
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);
        */

        actions.add(searchBtn);
        actions.add(refreshBtn);
        add(actions, BorderLayout.EAST);
    }

    public void addSearchListener(ActionListener a) { searchBtn.addActionListener(a); }
    public void addRefreshListener(ActionListener a) { refreshBtn.addActionListener(a); }
    public void addQueryListener(DocumentListener l) { textField.getDocument().addDocumentListener(l); }
    public String getQuery() { return textField.getText(); }
    public void setQuery(String q) { textField.setText(q); }
    public String getSearchType() { return (String) searchTypeCombo.getSelectedItem(); }
    public JComboBox<String> getSearchTypeCombo() { return searchTypeCombo; }
}