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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

/**
 * Simple rounded button with optional icon support.
 *
 * Behavior:
 *  - Renders HTML text if provided (wraps plain text into simple HTML).
 *  - Will only paint an icon if one was successfully set via the provided setters.
 *  - Fixed preferred width/height can be changed via setters.
 *
 * This version intentionally does NOT draw any default circular icon or plus glyph.
 */
public class RoundedButton extends JButton {

    // --- defaults ---
    private static final int DEFAULT_ARC = 18;
    private static final int DEFAULT_WIDTH = 140;
    private static final int DEFAULT_HEIGHT = 72;
    private static final Color DEFAULT_BG = new Color(0x0A, 0x1B, 0x2E);
    private static final Color DEFAULT_BORDER = new Color(0xCF, 0xE7, 0xFF);
    private static final Color DEFAULT_FG = new Color(0xEA, 0xF6, 0xFF);

    // --- instance state ---
    private int arc = DEFAULT_ARC;
    private int fixedWidth = DEFAULT_WIDTH;
    private int fixedHeight = DEFAULT_HEIGHT;
    private Color bg = DEFAULT_BG;
    private Color border = DEFAULT_BORDER;
    private Icon customIcon = null; // only painted when non-null

    // --- constructors ---
    public RoundedButton(String text) {
        super(wrapHtml(text));
        initialize();
    }

    public RoundedButton(String text, String iconResourcePath) {
        super(wrapHtml(text));
        initialize();
        if (iconResourcePath != null && !iconResourcePath.isBlank()) {
            setCustomIconResource(iconResourcePath);
        }
    }

    private void initialize() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(DEFAULT_FG);
        setFont(getFont().deriveFont(Font.BOLD, 14f));
        setPreferredSize(new Dimension(fixedWidth, fixedHeight));
        setMinimumSize(new Dimension(fixedWidth, fixedHeight));
    }

    // --- simple HTML wrapper for convenience ---
    private static String wrapHtml(String s) {
        if (s == null) return "<html></html>";
        if (BasicHTML.isHTMLString(s)) return s;
        // escape minimal risky chars and wrap
        String safe = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
        return "<html>" + safe + "</html>";
    }

    // --- appearance setters ---
    public void setArc(int arc) { this.arc = Math.max(0, arc); repaint(); }
    public void setBg(Color bg) { this.bg = bg == null ? DEFAULT_BG : bg; repaint(); }
    public void setBorderColor(Color c) { this.border = c == null ? DEFAULT_BORDER : c; repaint(); }

    public void setFixedWidth(int width) {
        this.fixedWidth = Math.max(48, width);
        setPreferredSize(new Dimension(fixedWidth, fixedHeight));
        setMinimumSize(new Dimension(fixedWidth, fixedHeight));
        revalidate(); repaint();
    }

    public void setFixedHeight(int height) {
        this.fixedHeight = Math.max(28, height);
        setPreferredSize(new Dimension(fixedWidth, fixedHeight));
        setMinimumSize(new Dimension(fixedWidth, fixedHeight));
        revalidate(); repaint();
    }

    // --- icon setters (simple and explicit) ---

    /**
     * Set an Icon instance directly (useful for pre-loaded ImageIcon).
     */
    public void setCustomIcon(Icon icon) {
        this.customIcon = icon;
        revalidate(); repaint();
    }

    /**
     * Load icon from classpath resource. Example: "/static/icons/add.png"
     * Tries the given path and also toggles leading slash if needed.
     * If loading fails, icon is cleared.
     */
    public final void setCustomIconResource(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) { setCustomIcon((Icon) null); return; }
        try {
            URL res = getClass().getResource(resourcePath);
            if (res == null) {
                // try adding/removing leading slash
                String alt = resourcePath.startsWith("/") ? resourcePath.substring(1) : "/" + resourcePath;
                res = getClass().getResource(alt);
            }
            if (res == null) {
                // not found on classpath
                this.customIcon = null;
            } else {
                BufferedImage img = ImageIO.read(res);
                this.customIcon = img == null ? null : scaleToIcon(img);
            }
        } catch (IOException ex) {
            System.err.println("RoundedButton: failed to load resource '" + resourcePath + "': " + ex.getMessage());
            this.customIcon = null;
        }
        revalidate(); repaint();
    }

    /**
     * Load icon from a filesystem path or URL string. If loading fails, icon is cleared.
     */
    public void setCustomIconPath(String pathOrUrl) {
        if (pathOrUrl == null || pathOrUrl.isBlank()) { setCustomIcon((Icon) null); return; }
        try {
            BufferedImage img = null;
            File f = new File(pathOrUrl);
            if (f.exists()) {
                img = ImageIO.read(f);
            } else {
                try {
                    img = ImageIO.read(new URL(pathOrUrl));
                } catch (IOException ignored) { /* not a URL or failed */ }
            }
            this.customIcon = img == null ? null : scaleToIcon(img);
        } catch (IOException ex) {
            System.err.println("RoundedButton: failed to load path/url '" + pathOrUrl + "': " + ex.getMessage());
            this.customIcon = null;
        }
        revalidate(); repaint();
    }

    // helper to scale a BufferedImage into an Icon (keeps aspect ratio)
    private Icon scaleToIcon(BufferedImage img) {
        int target = Math.min(48, Math.max(16, fixedHeight - 24)); // fit inside button height
        int iw = img.getWidth();
        int ih = img.getHeight();
        float scale = 1f;
        if (iw > target || ih > target) {
            scale = Math.min((float) target / iw, (float) target / ih);
        }
        int w = Math.max(1, Math.round(iw * scale));
        int h = Math.max(1, Math.round(ih * scale));
        Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // --- text override to keep HTML wrapping consistent ---
    @Override
    public void setText(String text) {
        super.setText(wrapHtml(text));
        putClientProperty(BasicHTML.propertyKey, null);
        revalidate(); repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension base = super.getPreferredSize();
        return new Dimension(fixedWidth, Math.max(fixedHeight, base.height));
    }

    // --- painting: background, border, optional icon, text ---
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // background
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, w, h, arc, arc);

            // border
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(border);
            g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);

            // icon area: if customIcon is set, draw it aligned to the left with some padding
            int iconAreaLeft = 12;
            int iconAreaRightPadding = 12;
            int textX = 16; // default text start if no icon

            if (customIcon != null) {
                int iconW = customIcon.getIconWidth();
                int iconH = customIcon.getIconHeight();
                int ix = iconAreaLeft;
                int iy = (h - iconH) / 2;
                customIcon.paintIcon(this, g2, ix, iy);
                textX = ix + iconW + iconAreaRightPadding;
            }

            // compute available text area
            int textWidth = Math.max(16, w - textX - 12);
            int textHeight = h;

            String text = getText();
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