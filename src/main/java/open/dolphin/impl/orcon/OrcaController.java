package open.dolphin.impl.orcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.output.TeeOutputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

/**
 * オルコン: ORCA を遠隔操作する.
 * @author pns
 */
public class OrcaController { //extends AbstractMainComponent {
    private static final String NAME = "オルコン";
    private OrconPanel orconPanel;
    private OrconProperties orconProps;
    private Macro macro;
    private final Logger logger;

    public OrcaController() {
        //setName(NAME);
        logger = LoggerFactory.getLogger(OrcaController.class);
    }

    //@Override
    public JPanel getUI() {
        return orconPanel.getPanel();
    }

    //@Override
    public void start() {
        orconPanel = new OrconPanel();
        orconPanel.setLoginState(false);

        orconProps = new OrconProperties(orconPanel);
        orconProps.modelToView();
        macro = new Macro(orconPanel, orconProps);

        // Listen KeyEvent on the close button,
        // which is the only component enabled after orca login
        ShortcutListener shortcutListener = new ShortcutListener(macro);
        orconPanel.getCloseButton().addKeyListener(shortcutListener);
        orconPanel.getCloseButton().setFocusTraversalKeysEnabled(false); // tab を取られないように
        orconPanel.getLoginButton().addActionListener(e -> macro.login());
        orconPanel.getCloseButton().addActionListener(e -> macro.close());

        //getContext().getFrame().getRootPane().setDefaultButton(orconPanel.getLoginButton());
    }

//    @Override
//    public void enter() {}

//    @Override
//    public void stop() {}

    private static void redirectConsole() {
        try {
            String applicationSupportDir = System.getProperty("user.home") + "/Library/Application Support/OrcaController/";
            Path p = Paths.get(applicationSupportDir);
            if (!Files.exists(p)) {
                Files.createDirectory(p);
            }
            // tee で file と stdout 両方に出力する
            String logName = applicationSupportDir + "console.log";
            PrintStream fileStream = new PrintStream(new FileOutputStream(logName, true), true); // append, auto flush
            TeeOutputStream teeStream = new TeeOutputStream(System.out, fileStream);
            PrintStream tee = new PrintStream(teeStream);
            System.setOut(tee);
            System.setErr(tee);

        } catch (IOException ex) {
        }
    }

    public static void main(String[] args) {
        redirectConsole();
        OrcaController orcon = new OrcaController();
        orcon.start();
        Preferences prefs = Preferences.userNodeForPackage(OrconProperties.class);

        int x = prefs.getInt("JFLAME_X", 100);
        int y = prefs.getInt("JFLAME_Y", 100);
        int w = prefs.getInt("JFLAME_W", 720);
        int h = prefs.getInt("JFLAME_H", 240);
        System.out.println("prefs loaded");

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
        frame.setBounds(new Rectangle(x, y, w, h));
        frame.add(orcon.getUI());
        frame.getRootPane().setDefaultButton(orcon.orconPanel.getLoginButton());

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                prefs.putInt("JFLAME_X", frame.getBounds().x);
                prefs.putInt("JFLAME_Y", frame.getBounds().y);
                prefs.putInt("JFLAME_W", frame.getBounds().width);
                prefs.putInt("JFLAME_H", frame.getBounds().height);
                System.out.println("prefs saved");
                System.exit(0);
            }
        });

        Desktop desktop = Desktop.getDesktop();
        desktop.setQuitHandler((e, response) -> frame.dispose());

        frame.setVisible(true);
    }
}
