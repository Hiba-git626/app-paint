package drawingapp.api;

import java.awt.Color;
import javax.swing.JComponent;

/**
 * Contrat entre l'interface (partie 2) et le moteur de dessin (partie 1).
 * La personne 1 fait implementer cette interface a son DrawingPanel.
 */
public interface DrawingCanvas {
    JComponent getComponent();
    void setTool(Tool tool);
    void setPrimaryColor(Color color);
    void setSecondaryColor(Color color);
    void setStrokeWidth(int width);
    void undo();
    void redo();
    void zoomIn();
    void zoomOut();
    void resetZoom();
    void setListener(CanvasListener listener);
}
