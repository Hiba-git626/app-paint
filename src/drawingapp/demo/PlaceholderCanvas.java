package drawingapp.demo;

import drawingapp.api.CanvasListener;
import drawingapp.api.DrawingCanvas;
import drawingapp.api.Tool;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * Zone de dessin MINIMALE (crayon + gomme) pour tester l'interface sans attendre la partie 1.
 * A remplacer par le vrai DrawingPanel de la personne 1.
 */
public class PlaceholderCanvas extends JPanel implements DrawingCanvas {

    private final BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
    private double zoom = 1.0;
    private Color primary = Color.BLACK;
    private int strokeWidth = 3;
    private Tool tool = Tool.PENCIL;
    private CanvasListener listener;
    private Point last;

    public PlaceholderCanvas() {
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.dispose();

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                last = toImage(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point p = toImage(e);
                if (last != null && (tool == Tool.PENCIL || tool == Tool.ERASER)) {
                    Graphics2D g = image.createGraphics();
                    g.setColor(tool == Tool.ERASER ? Color.WHITE : primary);
                    g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.drawLine(last.x, last.y, p.x, p.y);
                    g.dispose();
                    repaint();
                    if (listener != null) {
                        listener.modified();
                    }
                }
                last = p;
                moved(p);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                moved(toImage(e));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (listener != null) {
                    listener.cursorExited();
                }
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    private Point toImage(MouseEvent e) {
        return new Point((int) (e.getX() / zoom), (int) (e.getY() / zoom));
    }

    private void moved(Point p) {
        if (listener != null) {
            listener.cursorMoved(p.x, p.y);
        }
    }

    private void setZoom(double z) {
        zoom = Math.max(0.25, Math.min(8.0, z));
        revalidate();
        repaint();
        if (listener != null) {
            listener.zoomChanged(zoom);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int) (image.getWidth() * zoom), (int) (image.getHeight() * zoom));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(zoom, zoom);
        g2.drawImage(image, 0, 0, null);
    }

    @Override public JComponent getComponent() { return this; }
    @Override public void setTool(Tool tool) { this.tool = tool; }
    @Override public void setPrimaryColor(Color color) { this.primary = color; }
    @Override public void setSecondaryColor(Color color) { }
    @Override public void setStrokeWidth(int width) { this.strokeWidth = width; }
    @Override public void undo() { }
    @Override public void redo() { }
    @Override public void zoomIn() { setZoom(zoom * 1.25); }
    @Override public void zoomOut() { setZoom(zoom / 1.25); }
    @Override public void resetZoom() { setZoom(1.0); }

    @Override
    public void setListener(CanvasListener listener) {
        this.listener = listener;
        listener.imageSizeChanged(image.getWidth(), image.getHeight());
        listener.zoomChanged(zoom);
    }
}
