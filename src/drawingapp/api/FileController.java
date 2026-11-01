package drawingapp.api;

/**
 * Contrat entre l'interface (partie 2) et la gestion des fichiers (partie 3).
 * Tous les messages affiches doivent passer par I18n.get(...).
 */
public interface FileController {
    void newDrawing();
    void open();
    void save();
    void saveAs();
    void print();
    void copy();
    void paste();
    /** Appelee a la fermeture : renvoie false si l'utilisateur annule. */
    boolean confirmClose();
}
