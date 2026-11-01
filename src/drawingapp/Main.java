package drawingapp;

import drawingapp.demo.PlaceholderCanvas;
import drawingapp.demo.StubFileController;
import drawingapp.i18n.I18n;
import drawingapp.ui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // on garde le Look and Feel par defaut
            }
            I18n.installUiDefaults();

            // A l'integration : remplacer par DrawingPanel (partie 1) et FileManager (partie 3)
            MainFrame frame = new MainFrame(new PlaceholderCanvas(), new StubFileController());
            frame.applyComponentOrientation(I18n.getOrientation());
            frame.setVisible(true);
        });
    }
}
