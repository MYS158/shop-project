package app.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.JButton;

/**
* Rounded button with an optional circular icon area on the left.
*/
public class RoundedButton extends JButton {
    private int arc = 18;
    private Color bg = new Color(0x0A, 0x1B, 0x2E);
    private Color border = new Color(0xCF, 0xE7, 0xFF);
    private boolean drawIconCircle = true;

    public RoundedButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(new Color(0xEA, 0xF6, 0xFF));
        setFont(getFont().deriveFont(Font.BOLD, 14f));
        setPreferredSize(new Dimension(140, 72));
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w, h, arc, arc);


        // Border
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(border);
        g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);


        // Icon circle area
        int circleSize = Math.min(h - 20, 48);
        int circleX = 12;
        int circleY = (h - circleSize) / 2;
        if (drawIconCircle) {
        // outer ring
        g2.setColor(new Color(0xEA, 0xF6, 0xFF));
        g2.fillOval(circleX - 2, circleY - 2, circleSize + 4, circleSize + 4);
        // inner (black) circle
        g2.setColor(Color.BLACK);
        g2.fillOval(circleX, circleY, circleSize, circleSize);
        // draw a simple plus icon
        g2.setColor(Color.WHITE);
        Stroke old = g2.getStroke();
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int px = circleX + circleSize / 2;
        int py = circleY + circleSize / 2;
        int len = circleSize / 3;
        g2.drawLine(px - len, py, px + len, py);
        g2.drawLine(px, py - len, px, py + len);
        g2.setStroke(old);
        }


        // Text
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int textX = drawIconCircle ? (circleX + circleSize + 12) : 16;
        int textY = (h + fm.getAscent() - fm.getDescent()) / 2;
        g2.setColor(getForeground());
        g2.drawString(getText(), textX, textY);


        g2.dispose();
    }


    public void setArc(int arc) { this.arc = arc; }
    public void setBg(Color bg) { this.bg = bg; }
    public void setBorderColor(Color c) { this.border = c; }
    public void setDrawIconCircle(boolean draw) { this.drawIconCircle = draw; }
}