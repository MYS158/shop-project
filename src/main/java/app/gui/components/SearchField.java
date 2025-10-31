package app.gui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

/**
* Search field with placeholder behavior and small round action buttons.
*/
public class SearchField extends JPanel {
    private final JTextField textField;
    private final JButton searchBtn;
    private final JButton refreshBtn;

    public SearchField() {
        setOpaque(false);
        setLayout(new BorderLayout(8, 0));

        JPanel left = new JPanel(new BorderLayout(6, 0));
        left.setOpaque(false);

        JComboBox<String> combo = new JComboBox<>(new String[]{"All", "Description", "Brand", "ID"});
        combo.setPreferredSize(new Dimension(140, 28));
        left.add(combo, BorderLayout.WEST);

        textField = new JTextField();
        textField.setToolTipText("Search by description, brand, id...");
        left.add(textField, BorderLayout.CENTER);

        add(left, BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
        actions.setOpaque(false);
        searchBtn = new JButton();
        searchBtn.setToolTipText("Search (Enter)");
        searchBtn.setPreferredSize(new Dimension(36, 36));
        searchBtn.setFocusPainted(false);
        searchBtn.setBorderPainted(false);
        searchBtn.setText("🔍");

        refreshBtn = new JButton();
        refreshBtn.setToolTipText("Refresh");
        refreshBtn.setPreferredSize(new Dimension(36, 36));
        refreshBtn.setText("⟳");
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorderPainted(false);

        actions.add(searchBtn);
        actions.add(refreshBtn);
        add(actions, BorderLayout.EAST);
    }

    public void addSearchListener(ActionListener a) { searchBtn.addActionListener(a); }
    public void addRefreshListener(ActionListener a) { refreshBtn.addActionListener(a); }
    public void addQueryListener(DocumentListener l) { textField.getDocument().addDocumentListener(l); }
    public String getQuery() { return textField.getText(); }
    public void setQuery(String q) { textField.setText(q); }
}