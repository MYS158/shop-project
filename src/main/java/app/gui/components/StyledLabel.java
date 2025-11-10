package app.gui.components;


import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;


/**
* Pre-configured label styles for titles and field labels.
*/
public class StyledLabel extends JLabel {
    public enum Variant { TITLE, SECTION, FIELD }

    public StyledLabel(String text, Variant variant) {
        super(text);
        switch (variant) {
            case TITLE -> {
                setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 16));
                setForeground(new Color(0xE9, 0xF6, 0xFF));
            }
            case SECTION -> {
                setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 14));
                setForeground(new Color(0x1F, 0x80, 0xFF));
            }
            default -> {
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                setForeground(new Color(0x07, 0x2B, 0x4A));
            }
        }
    }
}