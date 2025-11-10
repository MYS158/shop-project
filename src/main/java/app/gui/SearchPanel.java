package app.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import app.gui.components.SearchField;
import app.gui.components.StyledLabel;

/**
 * Small wrapper panel that contains the search components and exposes listeners.
 */
public class SearchPanel extends RoundedPanel {
    private final SearchField searchField;

    public SearchPanel() {
        super(10);
        setLayout(new BorderLayout());
        StyledLabel title = new StyledLabel("Search data", StyledLabel.Variant.SECTION);
        add(title, BorderLayout.NORTH);

        searchField = new SearchField();
        add(searchField, BorderLayout.CENTER);
    }

    public void addSearchListener(ActionListener a) { searchField.addSearchListener(a); }
    public void addRefreshListener(ActionListener a) { searchField.addRefreshListener(a); }
    public String getQuery() { return searchField.getQuery(); }
    public String getSearchType() { return searchField.getSearchType(); }
}