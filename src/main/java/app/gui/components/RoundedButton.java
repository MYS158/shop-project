package app.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

/**
 * Rounded button with an optional circular icon area on the left.
 * - Supports HTML text (use setText(\"<html>...\")) and will render it.
 * - Accepts a custom Icon (setCustomIcon) which is painted inside the circle.
 * - Has a configurable fixed width (setFixedWidth).
 */
public class RoundedButton extends JButton {
    private int arc = 18;
    private Color bg = new Color(0x0A, 0x1B, 0x2E);
    private Color border = new Color(0xCF, 0xE7, 0xFF);
    private boolean drawIconCircle = true;

    // new fields
    private Icon customIcon = null;
    private int fixedWidth = 140;
    private int fixedHeight = 72;

    public RoundedButton(String text) {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(new Color(0xEA, 0xF6, 0xFF));
        setFont(getFont().deriveFont(Font.BOLD, 14f));
        // prefer the fixed size but respect layout managers if needed
        setPreferredSize(new Dimension(fixedWidth, fixedHeight));
        setMinimumSize(new Dimension(fixedWidth, fixedHeight));
    }

    /**
     * Set a custom Icon that will be painted inside the circular icon area.
     * If null, the classic plus glyph will be drawn.
     */
    public void setCustomIcon(Icon icon) {
        this.customIcon = icon;
        repaint();
    }

    /**
     * Configure fixed width for the button. Preferred/minimum sizes are updated.
     */
    public void setFixedWidth(int width) {
        this.fixedWidth = Math.max(48, width);
        setPreferredSize(new Dimension(fixedWidth, fixedHeight));
        setMinimumSize(new Dimension(fixedWidth, fixedHeight));
        revalidate();
        repaint();
    }

    public void setFixedHeight(int h) {
        this.fixedHeight = Math.max(28, h);
        setPreferredSize(new Dimension(fixedWidth, fixedHeight));
        setMinimumSize(new Dimension(fixedWidth, fixedHeight));
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        // enforce fixed width while allowing layout managers to change height if necessary
        Dimension base = super.getPreferredSize();
        return new Dimension(fixedWidth, Math.max(fixedHeight, base.height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
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

                if (customIcon != null) {
                    // paint icon scaled (if necessary) centered inside the circle
                    int iconW = customIcon.getIconWidth();
                    int iconH = customIcon.getIconHeight();
                    float scale = 1f;
                    int padding = 8; // keep some padding inside circle
                    int max = circleSize - padding * 2;
                    if (iconW > max || iconH > max) {
                        scale = Math.min((float) max / iconW, (float) max / iconH);
                    }
                    int drawW = Math.round(iconW * scale);
                    int drawH = Math.round(iconH * scale);
                    int ix = circleX + (circleSize - drawW) / 2;
                    int iy = circleY + (circleSize - drawH) / 2;
                    // draw the icon by temporarily translating graphics
                    g2.translate(ix, iy);
                    customIcon.paintIcon(this, g2, 0, 0);
                    g2.translate(-ix, -iy);
                } else {
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
            }

            // Text rendering (support HTML)
            // If the text is HTML, use BasicHTML to create a View that can render multi-line/wrapped HTML.
            String text = getText();
            boolean isHtml = text != null && text.toLowerCase().startsWith("<html>");
            int textX = drawIconCircle ? (circleX + circleSize + 12) : 16;
            int textWidth = Math.max(16, w - textX - 12);
            int textHeight = h;

            if (isHtml) {
                View v = (View) getClientProperty(BasicHTML.propertyKey);
                if (v == null || !text.equals(getClientProperty("cachedHtml"))) {
                    v = BasicHTML.createHTMLView(this, text);
                    putClientProperty(BasicHTML.propertyKey, v);
                    putClientProperty("cachedHtml", text);
                }
                v.setSize(textWidth, textHeight);
                // compute top offset to center vertically
                float prefH = (float) v.getPreferredSpan(View.Y_AXIS);
                int y = (int) ((h - prefH) / 2);
                g2.translate(textX, y);
                Shape clip = g2.getClip();
                g2.setClip(0, 0, textWidth, textHeight);
                v.paint(g2, new Rectangle(0, 0, textWidth, textHeight));
                g2.setClip(clip);
                g2.translate(-textX, -y);
            } else {
                // plain text: draw single-line centered vertically
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textY = (h + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(getForeground());
                // if text too wide, clip (basic)
                String toDraw = text;
                int avail = textWidth - 4;
                if (fm.stringWidth(toDraw) > avail) {
                    // truncate with ellipsis
                    String ell = "...";
                    int len = toDraw.length();
                    while (len > 0 && fm.stringWidth(toDraw.substring(0, len) + ell) > avail) len--;
                    toDraw = toDraw.substring(0, Math.max(0, len)) + ell;
                }
                g2.drawString(toDraw, textX, textY);
            }
        } finally {
            g2.dispose();
        }
    }

    // setters for customization
    public void setArc(int arc) { this.arc = arc; }
    public void setBg(Color bg) { this.bg = bg; repaint(); }
    public void setBorderColor(Color c) { this.border = c; repaint(); }
    public void setDrawIconCircle(boolean draw) { this.drawIconCircle = draw; repaint(); }
}
