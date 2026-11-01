package drawingapp.api;

/** Evenements que la zone de dessin (partie 1) envoie a l'interface (partie 2). */
public interface CanvasListener {
    void cursorMoved(int x, int y);
    void cursorExited();
    void zoomChanged(double zoom);          // 1.0 = 100 %
    void imageSizeChanged(int width, int height);
    void modified();                        // le dessin a change
}
