package drawingapp.demo;

import drawingapp.api.FileController;
import drawingapp.i18n.I18n;
import javax.swing.JOptionPane;

/** Gestion de fichiers BIDON pour tester l'interface. A remplacer par la partie 3. */
public class StubFileController implements FileController {

    private void todo() {
        JOptionPane.showMessageDialog(null, I18n.get("msg.notImplemented"));
    }

    @Override public void newDrawing() { todo(); }
    @Override public void open() { todo(); }
    @Override public void save() { todo(); }
    @Override public void saveAs() { todo(); }
    @Override public void print() { todo(); }
    @Override public void copy() { todo(); }
    @Override public void paste() { todo(); }
    @Override public boolean confirmClose() { return true; }
}
