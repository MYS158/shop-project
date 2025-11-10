package app.gui;

import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import app.gui.components.RoundedButton;

/**
 * Additional action panel for export/import and other utilities.
 */
public class UtilityButtonPanel extends JPanel {
    private final RoundedButton exportBtn = new RoundedButton("<html><b>Export</b><br><small>CSV</small></html>", "/static/icons/export.png");
    private final RoundedButton importBtn = new RoundedButton("<html><b>Import</b><br><small>CSV</small></html>", "/static/icons/import.png");
    private final RoundedButton statsBtn = new RoundedButton("<html><b>Stats</b><br><small>view</small></html>", "/static/icons/stats.png");

    public UtilityButtonPanel() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        Dimension btnSize = new Dimension(120, 60);
        exportBtn.setPreferredSize(btnSize);
        exportBtn.setMaximumSize(btnSize);
        importBtn.setPreferredSize(btnSize);
        importBtn.setMaximumSize(btnSize);
        statsBtn.setPreferredSize(btnSize);
        statsBtn.setMaximumSize(btnSize);

        add(exportBtn);
        add(Box.createVerticalStrut(12));
        add(importBtn);
        add(Box.createVerticalStrut(12));
        add(statsBtn);
        add(Box.createVerticalGlue());
    }

    public void addExportListener(ActionListener l) { exportBtn.addActionListener(l); }
    public void addImportListener(ActionListener l) { importBtn.addActionListener(l); }
    public void addStatsListener(ActionListener l) { statsBtn.addActionListener(l); }

    public RoundedButton getExportButton() { return exportBtn; }
    public RoundedButton getImportButton() { return importBtn; }
    public RoundedButton getStatsButton() { return statsBtn; }
}
