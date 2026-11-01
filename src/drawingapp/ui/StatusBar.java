package drawingapp.ui;

import drawingapp.api.Tool;
import drawingapp.i18n.I18n;
import java.awt.GridLayout;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Barre d'etat : outil, position du curseur, taille de l'image, zoom, date (formats selon la langue). */
public class StatusBar extends JPanel {

    private final JLabel toolLabel = new JLabel();
    private final JLabel positionLabel = new JLabel();
    private final JLabel sizeLabel = new JLabel();
    private final JLabel zoomLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();

    private Tool tool = Tool.PENCIL;
    private int x = -1;
    private int y = -1;
    private int imgWidth = 0;
    private int imgHeight = 0;
    private double zoom = 1.0;

    public StatusBar() {
        super(new GridLayout(1, 5, 10, 0));
        setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        add(toolLabel);
        add(positionLabel);
        add(sizeLabel);
        add(zoomLabel);
        add(dateLabel);
        refresh();
    }

    public void setTool(Tool tool) {
        this.tool = tool;
        refresh();
    }

    public void setCursor(int x, int y) {
        this.x = x;
        this.y = y;
        refresh();
    }

    public void clearCursor() {
        this.x = -1;
        this.y = -1;
        refresh();
    }

    public void setImageSize(int w, int h) {
        this.imgWidth = w;
        this.imgHeight = h;
        refresh();
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        refresh();
    }

    /** Relit tous les textes dans la langue courante. */
    public void refresh() {
        toolLabel.setText(I18n.get(tool.key()));
        positionLabel.setText(x < 0 ? " " : I18n.format("status.position", x, y));
        sizeLabel.setText(I18n.format("status.size", imgWidth, imgHeight));
        zoomLabel.setText(I18n.format("status.zoom", I18n.formatPercent(zoom)));
        dateLabel.setText(I18n.formatDate(new Date()));
    }
}
