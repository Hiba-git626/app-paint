package drawingapp.ui;

import drawingapp.api.CanvasListener;
import drawingapp.api.DrawingCanvas;
import drawingapp.api.FileController;
import drawingapp.api.Tool;
import drawingapp.i18n.I18n;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 * Fenetre principale (partie 2) : menus, barre d'outils, palette, barre d'etat.
 * Chaque texte est enregistre dans "translators" : au changement de langue,
 * tous sont relus sans redemarrer.
 */
public class MainFrame extends JFrame implements I18n.Listener {

    private final DrawingCanvas canvas;
    private final FileController files;
    private final StatusBar statusBar = new StatusBar();
    private final List<Runnable> translators = new ArrayList<>();
    private final JLabel strokeLabel = new JLabel();

    private String documentName = null;   // null = "Sans titre"
    private boolean modified = false;

    public MainFrame(DrawingCanvas canvas, FileController files) {
        this.canvas = canvas;
        this.files = files;

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        setJMenuBar(buildMenuBar());
        add(buildTopPanel(), BorderLayout.PAGE_START);
        add(buildToolPanel(), BorderLayout.LINE_START);   // LINE_START = a droite en arabe
        add(new JScrollPane(canvas.getComponent()), BorderLayout.CENTER);
        add(statusBar, BorderLayout.PAGE_END);

        canvas.setListener(new CanvasListener() {
            @Override public void cursorMoved(int x, int y) { statusBar.setCursor(x, y); }
            @Override public void cursorExited() { statusBar.clearCursor(); }
            @Override public void zoomChanged(double zoom) { statusBar.setZoom(zoom); }
            @Override public void imageSizeChanged(int w, int h) { statusBar.setImageSize(w, h); }
            @Override public void modified() { setDocumentState(documentName, true); }
        });
        canvas.setTool(Tool.PENCIL);

        I18n.addListener(this);
        refreshTexts();
        applyComponentOrientation(I18n.getOrientation());
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }

    // ---------- API pour la partie 3 ----------

    /** A appeler apres ouvrir / enregistrer / modifier : met a jour le titre de la fenetre. */
    public void setDocumentState(String name, boolean modified) {
        this.documentName = name;
        this.modified = modified;
        refreshTitle();
    }

    // ---------- construction ----------

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu file = menu("file");
        file.add(item("new", files::newDrawing));
        file.add(item("open", files::open));
        file.add(item("save", files::save));
        file.add(item("saveas", files::saveAs));
        file.addSeparator();
        file.add(item("print", files::print));
        file.addSeparator();
        file.add(item("exit", this::exit));

        JMenu edit = menu("edit");
        edit.add(item("undo", canvas::undo));
        edit.add(item("redo", canvas::redo));
        edit.addSeparator();
        edit.add(item("copy", files::copy));
        edit.add(item("paste", files::paste));

        JMenu view = menu("view");
        view.add(item("zoomin", canvas::zoomIn));
        view.add(item("zoomout", canvas::zoomOut));
        view.add(item("zoomreset", canvas::resetZoom));

        JMenu help = menu("help");
        help.add(item("about", () -> JOptionPane.showMessageDialog(this,
                I18n.format("about.text", I18n.formatDate(new Date())),
                I18n.get("menu.about"), JOptionPane.INFORMATION_MESSAGE)));

        bar.add(file);
        bar.add(edit);
        bar.add(view);
        bar.add(buildLanguageMenu());
        bar.add(help);
        return bar;
    }

    private JMenu buildLanguageMenu() {
        JMenu lang = menu("language");
        ButtonGroup group = new ButtonGroup();
        for (Locale l : I18n.getSupportedLocales()) {
            // Le nom de chaque langue reste ecrit dans sa propre langue : pas de traduction.
            JRadioButtonMenuItem radio = new JRadioButtonMenuItem(I18n.getNativeName(l));
            radio.setSelected(l.getLanguage().equals(I18n.getLocale().getLanguage()));
            radio.addActionListener(e -> I18n.setLocale(l));
            group.add(radio);
            lang.add(radio);
        }
        return lang;
    }

    private JComponent buildTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 14, 4));

        ColorPalette palette = new ColorPalette((primary, secondary) -> {
            canvas.setPrimaryColor(primary);
            canvas.setSecondaryColor(secondary);
        });
        canvas.setPrimaryColor(palette.getPrimary());
        canvas.setSecondaryColor(palette.getSecondary());

        JSpinner stroke = new JSpinner(new SpinnerNumberModel(3, 1, 50, 1));
        stroke.addChangeListener(e -> canvas.setStrokeWidth((Integer) stroke.getValue()));
        canvas.setStrokeWidth(3);
        translators.add(() -> strokeLabel.setText(I18n.get("toolbar.stroke")));

        panel.add(palette);
        panel.add(strokeLabel);
        panel.add(stroke);
        return panel;
    }

    private JComponent buildToolPanel() {
        JPanel grid = new JPanel(new GridLayout(0, 1, 0, 2));
        grid.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        ButtonGroup group = new ButtonGroup();
        for (Tool tool : Tool.values()) {
            JToggleButton button = new JToggleButton();
            button.addActionListener(e -> {
                canvas.setTool(tool);
                statusBar.setTool(tool);
            });
            translators.add(() -> {
                button.setText(I18n.get(tool.key()));
                button.setToolTipText(I18n.get(tool.key() + ".tip"));
            });
            button.setSelected(tool == Tool.PENCIL);
            group.add(button);
            grid.add(button);
        }
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(grid, BorderLayout.PAGE_START);
        return wrap;
    }

    // ---------- aides pour les menus ----------

    private JMenu menu(String name) {
        JMenu menu = new JMenu();
        translators.add(() -> menu.setText(I18n.get("menu." + name)));
        return menu;
    }

    private JMenuItem item(String name, Runnable action) {
        JMenuItem item = new JMenuItem();
        item.addActionListener(e -> action.run());
        translators.add(() -> {
            item.setText(I18n.get("menu." + name));
            item.setAccelerator(accelerator(name));
        });
        return item;
    }

    /** Raccourci lu dans le fichier de langue : shortcut.save=S ou shortcut.saveas=shift S. */
    private static KeyStroke accelerator(String name) {
        String key = "shortcut." + name;
        if (!I18n.contains(key)) {
            return null;
        }
        String value = I18n.get(key).trim();
        int modifiers = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        if (value.startsWith("shift ")) {
            modifiers |= InputEvent.SHIFT_DOWN_MASK;
            value = value.substring(6).trim();
        }
        return KeyStroke.getKeyStroke(KeyEvent.getExtendedKeyCodeForChar(value.charAt(0)), modifiers);
    }

    // ---------- langue ----------

    @Override
    public void languageChanged() {
        I18n.installUiDefaults();
        refreshTexts();
        SwingUtilities.updateComponentTreeUI(this);       // applique la nouvelle police
        applyComponentOrientation(I18n.getOrientation()); // droite a gauche pour l'arabe
        revalidate();
        repaint();
    }

    private void refreshTexts() {
        translators.forEach(Runnable::run);
        statusBar.refresh();
        refreshTitle();
    }

    private void refreshTitle() {
        String name = documentName != null ? documentName : I18n.get("file.untitled");
        setTitle(I18n.format("app.title", name, modified ? " *" : ""));
    }

    private void exit() {
        if (files.confirmClose()) {
            dispose();
            System.exit(0);
        }
    }
}
