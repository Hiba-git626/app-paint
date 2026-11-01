# Guide d'équipe – Mini Paint (Projet 1, ENSA Safi)

La partie 2 (interface + internationalisation) est prête. Elle fonctionne déjà avec un canvas et une
gestion de fichiers « bidons » (`drawingapp.demo`). Il reste à brancher vos vraies classes.

## Démarrage
1. `./build.sh` (ou `build.bat`), puis `java -cp out drawingapp.Main`
2. Java 17 ou plus. Dossier source = `src`.
3. Git : une branche par personne (`partie1-outils`, `partie2-interface`, `partie3-fichiers`).

## Personne 1 : moteur de dessin
Créer `drawingapp.engine.DrawingPanel` qui `extends JPanel implements DrawingCanvas`.
- Implémenter toutes les méthodes de `DrawingCanvas` (outil, couleurs, épaisseur, undo/redo, zoom).
- Prévenir l'interface avec le `CanvasListener` reçu dans `setListener(...)` :
  `cursorMoved`, `cursorExited`, `zoomChanged`, `imageSizeChanged`, `modified`.
  Appeler `modified()` à chaque changement du dessin (la partie 3 en a besoin pour « non enregistré »).
- Ajouter dans `DrawingPanel` (pas dans l'interface) : `BufferedImage getImage()` et
  `void setImage(BufferedImage img)` : la personne 3 en a besoin.
- Le canvas utilise `getImage()` pour l'image affichée ; `PlaceholderCanvas` montre un exemple simple.
- Texte, police, messages : toujours via `I18n` (voir ci-dessous).

## Personne 3 : fichiers et livrables
Créer `drawingapp.files.FileManager implements FileController`.
- Constructeur : `FileManager(DrawingPanel panel)` ; ajouter `setFrame(MainFrame f)` pour
  appeler `frame.setDocumentState(nomFichier, modifie)` après ouvrir / enregistrer.
- `confirmClose()` : si le dessin est modifié, demander Enregistrer / Ne pas enregistrer / Annuler.
- Boîtes de dialogue : `JOptionPane` utilise déjà les boutons traduits (Oui / Non / Annuler / OK).
  Pour `JFileChooser`, traduire avec `UIManager.put("FileChooser.openButtonText", I18n.get("..."))`
  (et `saveButtonText`, `cancelButtonText`, `fileNameLabelText`, etc.) avant de l'afficher.
- Tous les messages d'erreur (fichier invalide, droits d'écriture) : clés dans les 3 fichiers de langue.

## Branchement final (dans `Main.java`)
```java
DrawingPanel panel = new DrawingPanel();
FileManager files = new FileManager(panel);
MainFrame frame = new MainFrame(panel, files);
files.setFrame(frame);
frame.applyComponentOrientation(I18n.getOrientation());
frame.setVisible(true);
```
Ensuite, supprimer le dossier `drawingapp.demo`.

## Règles d'internationalisation (tout le monde)
1. Aucun texte en dur : `I18n.get("cle")`, ou `I18n.format("cle", params)` pour les textes avec {0}, {1}.
2. Dans un texte utilisé avec `format`, l'apostrophe s'écrit `''`.
3. Toute nouvelle clé s'ajoute dans **les trois** fichiers : `src/i18n/messages_fr / _en / _ar.properties`.
   Une clé manquante s'affiche sous la forme `!cle!`.
4. Un composant qui affiche du texte et qui reste à l'écran doit implémenter `I18n.Listener`
   et s'enregistrer avec `I18n.addListener(this)` pour se mettre à jour au changement de langue.
5. Utiliser `ComponentOrientation` via les layouts (`LINE_START`, `LEADING`) plutôt que `WEST`, `LEFT`,
   pour que l'arabe s'affiche bien de droite à gauche.

## Livrables (partie 3)
- Exécutable : `jar` avec `Main-Class: drawingapp.Main` (inclure `i18n/*.properties`), ou `.exe` via `jpackage`.
- Code source commenté, README, rapport.
