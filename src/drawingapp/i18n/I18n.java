package drawingapp.i18n;

import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

/**
 * Point central de l'internationalisation.
 * Regle d'or : AUCUN texte ecrit en dur dans le code, toujours I18n.get("cle").
 */
public final class I18n {

    /** Implemente par les composants qui doivent se rafraichir quand la langue change. */
    public interface Listener {
        void languageChanged();
    }

    private static final String BUNDLE_NAME = "i18n.messages";
    private static final String PREF_KEY = "language";
    private static final Preferences PREFS = Preferences.userNodeForPackage(I18n.class);
    private static final List<Locale> SUPPORTED = List.of(
            Locale.forLanguageTag("fr"),
            Locale.forLanguageTag("en"),
            Locale.forLanguageTag("ar"));
    private static final List<Listener> LISTENERS = new ArrayList<>();

    private static Locale locale;
    private static ResourceBundle bundle;

    static {
        // Derniere langue choisie (francais par defaut)
        apply(Locale.forLanguageTag(PREFS.get(PREF_KEY, "fr")));
    }

    private I18n() { }

    // ---------- langue ----------

    public static List<Locale> getSupportedLocales() {
        return SUPPORTED;
    }

    public static Locale getLocale() {
        return locale;
    }

    /** Change la langue a la volee, l'enregistre et previent les ecouteurs. */
    public static void setLocale(Locale newLocale) {
        apply(newLocale);
        PREFS.put(PREF_KEY, locale.getLanguage());
        for (Listener l : new ArrayList<>(LISTENERS)) {
            l.languageChanged();
        }
    }

    private static void apply(Locale l) {
        locale = SUPPORTED.stream()
                .filter(s -> s.getLanguage().equals(l.getLanguage()))
                .findFirst().orElse(SUPPORTED.get(0));
        bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale,
                ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));
        Locale.setDefault(locale);
        JComponent.setDefaultLocale(locale);
    }

    public static void addListener(Listener l) {
        LISTENERS.add(l);
    }

    public static void removeListener(Listener l) {
        LISTENERS.remove(l);
    }

    /** Nom de la langue ecrit dans sa propre langue (Francais, English, العربية). */
    public static String getNativeName(Locale l) {
        String name = l.getDisplayLanguage(l);
        return name.isEmpty() ? name : name.substring(0, 1).toUpperCase(l) + name.substring(1);
    }

    // ---------- textes ----------

    public static boolean contains(String key) {
        return bundle.containsKey(key);
    }

    public static String get(String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return "!" + key + "!";   // cle manquante bien visible
        }
    }

    /** Texte avec parametres : I18n.format("status.size", 800, 600). Attention : ' s'ecrit '' dans ces textes. */
    public static String format(String key, Object... args) {
        return new MessageFormat(get(key), locale).format(args);
    }

    // ---------- formats date / nombre ----------

    public static String formatDate(Date date) {
        return DateFormat.getDateInstance(DateFormat.LONG, locale).format(date);
    }

    public static String formatNumber(Number n) {
        return NumberFormat.getInstance(locale).format(n);
    }

    public static String formatPercent(double ratio) {
        return NumberFormat.getPercentInstance(locale).format(ratio);
    }

    // ---------- droite a gauche, police ----------

    public static boolean isRightToLeft() {
        return !getOrientation().isLeftToRight();
    }

    public static ComponentOrientation getOrientation() {
        return ComponentOrientation.getOrientation(locale);
    }

    /**
     * A appeler apres le choix du Look and Feel et a chaque changement de langue :
     * police compatible arabe et boutons standards (Oui / Non / Annuler / OK) traduits.
     */
    public static void installUiDefaults() {
        Set<String> available = new HashSet<>(Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        String family = Font.DIALOG;
        for (String candidate : new String[] {"Segoe UI", "Tahoma", "Arial"}) {
            if (available.contains(candidate)) {
                family = candidate;
                break;
            }
        }
        for (Object key : Collections.list(UIManager.getDefaults().keys())) {
            Object value = UIManager.get(key);
            if (value instanceof Font) {
                Font f = (Font) value;
                UIManager.put(key, new FontUIResource(family, f.getStyle(), f.getSize()));
            }
        }
        UIManager.put("OptionPane.yesButtonText", get("dialog.yes"));
        UIManager.put("OptionPane.noButtonText", get("dialog.no"));
        UIManager.put("OptionPane.cancelButtonText", get("dialog.cancel"));
        UIManager.put("OptionPane.okButtonText", get("dialog.ok"));
    }
}
