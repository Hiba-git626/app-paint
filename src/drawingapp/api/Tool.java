package drawingapp.api;

import java.util.Locale;

/** Outils de dessin (partie 1). La cle sert a retrouver le nom traduit dans les fichiers de langue. */
public enum Tool {
    PENCIL, ERASER, LINE, RECTANGLE, ELLIPSE, TEXT, FILL, PICKER, SELECT;

    /** Cle de traduction, par exemple "tool.pencil". */
    public String key() {
        return "tool." + name().toLowerCase(Locale.ROOT);
    }
}
