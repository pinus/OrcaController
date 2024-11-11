package open.dolphin.impl.orcon;

import java.awt.*;
import java.util.prefs.Preferences;

/**
 * プロジェクトで使用する変数.
 * @author pns
 */
public class OrconProperties {
    public static String ORCA_ADDRESS = "ORCA_ADDRESS";
    public static String ORCA_USER = "ORCA_USER";
    public static String ORCA_PASSWORD = "ORCA_PASSWORD";
    public static String ORCA_BOUNDS_X = "ORCA_BOUNDS_X";
    public static String ORCA_BOUNDS_Y = "ORCA_BOUNDS_Y";
    public static String ORCA_BOUNDS_W = "ORCA_BOUNDS_W";
    public static String ORCA_BOUNDS_H = "ORCA_BOUNDS_H";

    private final Preferences prefs;
    private final OrconPanel orconPanel;

    public OrconProperties(OrconPanel panel) {
        prefs = Preferences.userNodeForPackage(OrconProperties.class);
        orconPanel = panel;
    }

    public void modelToView() {
        orconPanel.getAddressField().setText(prefs.get(ORCA_ADDRESS, "http://weborca:8000"));
        orconPanel.getUserField().setText(prefs.get(ORCA_USER, "ormaster"));
        orconPanel.getPasswordField().setText(prefs.get(ORCA_PASSWORD, "ormater"));
    }

    public void viewToModel() {
        prefs.put(ORCA_ADDRESS, orconPanel.getAddressField().getText());
        prefs.put(ORCA_USER, orconPanel.getUserField().getText());
        prefs.put(ORCA_PASSWORD, new String(orconPanel.getPasswordField().getPassword()));
    }

    public Rectangle loadBounds() {
        int x = prefs.getInt(ORCA_BOUNDS_X, 0);
        int y = prefs.getInt(ORCA_BOUNDS_Y, 0);
        int w = prefs.getInt(ORCA_BOUNDS_W, 1280);
        int h = prefs.getInt(ORCA_BOUNDS_H, 1024);
        return new Rectangle(x, y, w, h);
    }

    public void saveBounds(Rectangle bounds) {
        prefs.putInt(ORCA_BOUNDS_X, bounds.x);
        prefs.putInt(ORCA_BOUNDS_Y, bounds.y);
        prefs.putInt(ORCA_BOUNDS_W, bounds.width);
        prefs.putInt(ORCA_BOUNDS_H, bounds.height);
    }
}
