package app.gui.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

public class RoundedButton extends JButton {
    private int arc = 18;
    private int fixedWidth = 140;
    private int fixedHeight = 72;
    private Color bg = new Color(0x0A, 0x1B, 0x2E);
    private Color border = new Color(0xCF, 0xE7, 0xFF);
    private Icon customIcon = null;
    private boolean drawIconCircle = true;

    public RoundedButton(String text) {
        super(wrapHtml(text));
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(new Color(0xEA, 0xF6, 0xFF));
        setFont(getFont().deriveFont(Font.BOLD, 14f));
        setPreferredSize(new Dimension(fixedWidth, fixedHeight));
        setMinimumSize(new Dimension(fixedWidth, fixedHeight));
    }

    public RoundedButton(String text, String iconPath) {
        this(text);
        setCustomIcon(iconPath);
    }

    private static String wrapHtml(String s) {
        if (s == null) return "<html></html>";
        String t = s;
        if (!BasicHTML.isHTMLString(t)) t = String.format("<html>%s</html>", escapeForHtml(t));
        return t;
    }

    private static String escapeForHtml(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
    }

    public void setArc(int arc) {
        this.arc = arc;
        repaint();
    }

    public void setBg(Color bg) {
        this.bg = bg;
        repaint();
    }

    public void setBorderColor(Color color) {
        this.border = color;
        repaint();
    }

    public void setDrawIconCircle(boolean draw) {
        this.drawIconCircle = draw;
        repaint();
    }

    public void setFixedWidth(int width) {
        this.fixedWidth = Math.max(48, width);
        setPreferredSize(new Dimension(fixedWidth, fixedHeight));
        setMinimumSize(new Dimension(fixedWidth, fixedHeight));
        revalidate();
        repaint();
    }

    public void setFixedHeight(int height) {
        this.fixedHeight = Math.max(28, height);
        setPreferredSize(new Dimension(fixedWidth, fixedHeight));
        setMinimumSize(new Dimension(fixedWidth, fixedHeight));
        revalidate();
        repaint();
    }

    public final void setCustomIcon(String path) {
        if (path != null && !path.isEmpty()) {
            Image img = new ImageIcon(path).getImage();
            Image scaled = img.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            this.customIcon = new ImageIcon(scaled);
        } else {
            this.customIcon = null;
        }
        revalidate();
        repaint();
    }

    @Override
    public void setText(String text) {
        super.setText(wrapHtml(text));
        putClientProperty(BasicHTML.propertyKey, null);
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension base = super.getPreferredSize();
        return new Dimension(fixedWidth, Math.max(fixedHeight, base.height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, arc, arc);

            g2.setStroke(new BasicStroke(2f));
            g2.setColor(border);
            g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

            int circleSize = Math.min(h - 20, 48);
            int circleX = 12;
            int circleY = (h - circleSize) / 2;

            if (drawIconCircle) {
                g2.setColor(new Color(0xEA, 0xF6, 0xFF));
                g2.fillOval(circleX - 2, circleY - 2, circleSize + 4, circleSize + 4);
                g2.setColor(Color.BLACK);
                g2.fillOval(circleX, circleY, circleSize, circleSize);

                if (customIcon != null) {
                    int iconW = customIcon.getIconWidth();
                    int iconH = customIcon.getIconHeight();

                    float scale = 1f;
                    int padding = 8;
                    int max = circleSize - padding * 2;
                    if (iconW > max || iconH > max) {
                        scale = Math.min((float) max / iconW, (float) max / iconH);
                    }
                    int drawW = Math.round(iconW * scale);
                    int drawH = Math.round(iconH * scale);
                    int ix = circleX + (circleSize - drawW) / 2;
                    int iy = circleY + (circleSize - drawH) / 2;

                    Graphics2D gIcon = (Graphics2D) g2.create();
                    try {
                        gIcon.translate(ix, iy);
                        customIcon.paintIcon(this, gIcon, 0, 0);
                    } finally {
                        gIcon.dispose();
                    }
                }
            }

            String text = getText();
            int textX = drawIconCircle ? (circleX + circleSize + 12) : 16;
            int textWidth = Math.max(16, w - textX - 12);
            int textHeight = h;

            boolean isHtml = text != null && BasicHTML.isHTMLString(text);

            if (isHtml) {
                View v = (View) getClientProperty(BasicHTML.propertyKey);
                if (v == null) {
                    v = BasicHTML.createHTMLView(this, text);
                    putClientProperty(BasicHTML.propertyKey, v);
                }
                v.setSize(textWidth, textHeight);
                float prefH = (float) v.getPreferredSpan(View.Y_AXIS);
                int y = (int) ((h - prefH) / 2);
                g2.translate(textX, y);
                Shape clip = g2.getClip();
                g2.setClip(0, 0, textWidth, textHeight);
                v.paint(g2, new Rectangle(0, 0, textWidth, textHeight));
                g2.setClip(clip);
                g2.translate(-textX, -y);
            } else {
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int textY = (h + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(getForeground());
                String toDraw = text == null ? "" : text;
                int avail = textWidth - 4;
                if (fm.stringWidth(toDraw) > avail) {
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
}