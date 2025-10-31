package app.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
* Reusable rounded panel with optional gradient background and border.
*/
public class RoundedPanel extends JPanel {
    private int cornerRadius = 16;
    private Color topColor = new Color(0xDD, 0xF5, 0xFF);
    private Color bottomColor = new Color(0xC7, 0xEF, 0xFF);
    private Color borderColor = new Color(0x8F, 0xC7, 0xFF);
    private int borderThickness = 2;


    public RoundedPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
    }


    public RoundedPanel(int cornerRadius) {
        this();
        this.cornerRadius = cornerRadius;
    }


    @Override
    protected void paintComponent(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        // Shadow (subtle)
        g2.setColor(new Color(0, 0, 0, 24));
        g2.fillRoundRect(4, 4, width - 8, height - 8, cornerRadius, cornerRadius);


        // Gradient background
        GradientPaint gp = new GradientPaint(0, 0, topColor, 0, height, bottomColor);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, width - 8, height - 8, cornerRadius, cornerRadius);


        // Border
        g2.setStroke(new BasicStroke(borderThickness));
        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, width - 8, height - 8, cornerRadius, cornerRadius);


        g2.dispose();
        super.paintComponent(g);
    }


    // setters for customization
    public void setCornerRadius(int cornerRadius) { this.cornerRadius = cornerRadius; }
    public void setTopColor(Color topColor) { this.topColor = topColor; }
    public void setBottomColor(Color bottomColor) { this.bottomColor = bottomColor; }
    public void setBorderColor(Color borderColor) { this.borderColor = borderColor; }
    public void setBorderThickness(int borderThickness) { this.borderThickness = borderThickness; }
}