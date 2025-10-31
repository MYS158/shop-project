package app.gui.components;

import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import com.toedter.calendar.JDateChooser;

/**
* Simple wrapper around JDateChooser (from JCalendar library) to unify API.
*/
public class DatePickerField extends JPanel {
    private final JDateChooser chooser;

    public DatePickerField() {
        setOpaque(false);
        chooser = new JDateChooser();
        chooser.setDateFormatString("dd/MM/yyyy");
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(chooser);
    }

    public void setDate(Date d) { chooser.setDate(d); }
    public Date getDate() { return chooser.getDate(); }
    public JDateChooser getComponent() { return chooser; }
}