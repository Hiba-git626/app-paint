package drawingapp.ui;

import drawingapp.i18n.I18n;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.BiConsumer;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/** Palette de couleurs : clic gauche = couleur principale, clic droit = couleur secondaire. */
public class ColorPalette extends JPanel {

    private static final Color[] COLORS = {
        Color.BLACK, Color.DARK_GRAY, Color.GRAY, Color.LIGHT_GRAY, Color.WHITE,
        new Color(136, 0, 21), Color.RED, Color.ORANGE, Color.YELLOW, new Color(181, 230, 29),
        Color.GREEN, new Color(0, 162, 232), Color.BLUE, new Color(63, 72, 204),
        new Color(163, 73, 164), Color.PINK, new Color(185, 122, 87), Color.CYAN,
        Color.MAGENTA, new Color(255, 242, 0)
    };

    private final BiConsumer<Color, Color> onChange;
    private final Swatch primaryBox = new Swatch(Color.BLACK, "palette.primary");
    private final Swatch secondaryBox = new Swatch(Color.WHITE, "palette.secondary");

    public ColorPalette(BiConsumer<Color, Color> onChange) {
        super(new FlowLayout(FlowLayout.LEADING, 6, 0));
        this.onChange = onChange;

        primaryBox.setPreferredSize(new Dimension(34, 34));
        secondaryBox.setPreferredSize(new Dimension(34, 34));
        primaryBox.addMouseListener(chooserOnClick(true));
        secondaryBox.addMouseListener(chooserOnClick(false));
        add(primaryBox);
        add(secondaryBox);

        JPanel grid = new JPanel(new GridLayout(2, 10, 2, 2));
        for (Color c : COLORS) {
            Swatch s = new Swatch(c, "palette.tip");
            s.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (SwingUtilities.isRightMouseButton(e)) {
                        setSecondary(c);
                    } else {
                        setPrimary(c);
                    }
                }
            });
            grid.add(s);
        }
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.add(grid, BorderLayout.CENTER);
        add(wrap);
    }

    public Color getPrimary() {
        return primaryBox.color;
    }

    public Color getSecondary() {
        return secondaryBox.color;
    }

    private void setPrimary(Color c) {
        primaryBox.color = c;
        primaryBox.repaint();
        fire();
    }

    private void setSecondary(Color c) {
        secondaryBox.color = c;
        secondaryBox.repaint();
        fire();
    }

    private void fire() {
        onChange.accept(primaryBox.color, secondaryBox.color);
    }

    private MouseAdapter chooserOnClick(boolean primary) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Swatch box = primary ? primaryBox : secondaryBox;
                Color chosen = JColorChooser.showDialog(ColorPalette.this,
                        I18n.get("palette.chooser.title"), box.color);
                if (chosen != null) {
                    if (primary) {
                        setPrimary(chosen);
                    } else {
                        setSecondary(chosen);
                    }
                }
            }
        };
    }

    /** Petit carre de couleur. L'info-bulle est relue dans la langue courante a chaque affichage. */
    private static class Swatch extends JComponent {
        Color color;
        private final String tipKey;

        Swatch(Color color, String tipKey) {
            this.color = color;
            this.tipKey = tipKey;
            setPreferredSize(new Dimension(20, 20));
            setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            setToolTipText("");   // active le mecanisme d'info-bulle
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            return I18n.get(tipKey);
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(color);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
