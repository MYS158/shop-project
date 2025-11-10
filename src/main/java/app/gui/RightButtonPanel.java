package app.gui;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import app.gui.components.RoundedButton;

/**
 * Encapsulates the right-side vertical action buttons.
 * Exposes methods to add listeners rather than exposing internal buttons directly.
 */
public class RightButtonPanel extends JPanel {
    private final RoundedButton addBtn = new RoundedButton("<html><b>Add</b><br><small>new</small></html>", "/static/icons/add.png"), 
                                updateBtn = new RoundedButton("<html><b>Update</b><br><small>save</small></html>", "/static/icons/update.png"), 
                                deleteBtn = new RoundedButton("<html><b>Delete</b><br><small>remove</small></html>", "/static/icons/delete.png"), 
                                consultBtn = new RoundedButton("<html><b>Consult</b><br><small>view</small></html>", "/static/icons/consult.png");

    public RightButtonPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setPreferredSize(new Dimension(160, 0));

        // Make buttons consistent
        int fixedWidth = 140;
        int fixedHeight = 72;
        for (RoundedButton b : new RoundedButton[]{addBtn, updateBtn, deleteBtn, consultBtn}) {
            b.setFixedWidth(fixedWidth);
            b.setFixedHeight(fixedHeight);
        }

        add(addBtn);
        add(Box.createVerticalStrut(8));
        add(updateBtn);
        add(Box.createVerticalStrut(8));
        add(deleteBtn);
        add(Box.createVerticalStrut(8));
        add(consultBtn);
        add(Box.createVerticalStrut(8));
    }

    // Listener registration methods (keeps internal buttons private)
    public void addAddListener(ActionListener l) { addBtn.addActionListener(l); }
    public void addUpdateListener(ActionListener l) { updateBtn.addActionListener(l); }
    public void addDeleteListener(ActionListener l) { deleteBtn.addActionListener(l); }
    public void addConsultListener(ActionListener l) { consultBtn.addActionListener(l); }

    // Also expose the underlying buttons if needed for advanced customization
    public RoundedButton getAddButton() { return addBtn; }
    public RoundedButton getUpdateButton() { return updateBtn; }
    public RoundedButton getDeleteButton() { return deleteBtn; }
    public RoundedButton getConsultButton() { return consultBtn; }
}
