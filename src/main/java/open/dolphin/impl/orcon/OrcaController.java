package open.dolphin.impl.orcon;

import com.formdev.flatlaf.FlatLightLaf;
import io.github.bonigarcia.wdm.WebDriverManager;
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
    public enum Mode { DISABLE, FULL, STEALTH }

    private Mode mode;
    private OrconPanel orconPanel;
    private OrconProperties orconProps;
    private OrconMacro orconMacro;
    private OrconKeyDispatcher keyDispatcher;
    private final Logger logger;

    public OrcaController() {
        logger = LoggerFactory.getLogger(OrcaController.class);
        FlatLightLaf.setup();
        UIManager.put( "Component.focusWidth", 2);
        UIManager.put( "TextComponent.arc", 8 );
        UIManager.put( "Button.arc", 8 );
        Thread.ofVirtual().start(() -> {
            logger.info("Setting up chrome driver...");
            WebDriverManager.chromedriver().setup();
            logger.info("Chrome driver setting up done");
        });
    }

    public Mode getMode() {
        return mode;
    }

    public OrconPanel getOrconPanel() { return orconPanel; }

    public OrconProperties getOrconProps() { return orconProps; }

    public OrconMacro getOrconMacro() { return orconMacro; }

    public JPanel getUI() {
        return orconPanel.getPanel();
    }

    public void start() {
        orconPanel = new OrconPanel();
        orconPanel.setLoginState(false);

        orconProps = new OrconProperties(orconPanel);
        orconProps.modelToView();
        orconMacro = new OrconMacro(this);

        // key dispatcher
        keyDispatcher = new OrconKeyDispatcher(this);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyDispatcher);
        // login したら　enables する
        orconPanel.getLoginButton().addActionListener(e -> {
            orconMacro.login();
            mode = Mode.FULL;
        });
        orconPanel.getCloseButton().addActionListener(e -> {
            orconMacro.close();
            mode = Mode.DISABLE;
        });

        JButton backtoGyomu = orconPanel.getBtn1();
        backtoGyomu.setText("業務メニューに戻る");
        backtoGyomu.addActionListener(e -> orconMacro.backToGyomu());

        JButton kanjasu = orconPanel.getBtn2();
        kanjasu.setText("患者数一覧表");
        kanjasu.addActionListener(e -> orconMacro.m01ToKanjasu());

        JButton nikkei = orconPanel.getBtn3();
        nikkei.setText("日計表");
        nikkei.addActionListener(e -> orconMacro.m01ToNikkei());
    }

//    @Override
//    public void enter() {}

//    @Override
//    public void stop() {}

//    @Override
//    public Callable<Boolean> getStoppingTask() {}

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
        int h = prefs.getInt("JFLAME_H", 340);
        System.out.println("prefs loaded");

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getRootPane().putClientProperty("apple.awt.transparentTitleBar", true);
        frame.setBounds(new Rectangle(x, y, w, h));
        frame.add(orcon.getUI());
        frame.getRootPane().setDefaultButton(orcon.orconPanel.getLoginButton());

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowActivated(WindowEvent e) {
                orcon.orconPanel.setActive(orcon.orconPanel.getCloseButton().isEnabled());
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                orcon.orconPanel.setActive(false);
            }

            @Override
            public void windowClosed(WindowEvent e) {}
            @Override
            public void windowIconified(WindowEvent e) {}
            @Override
            public void windowDeiconified(WindowEvent e) {}
            @Override
            public void windowOpened(WindowEvent e) {}
            @Override
            public void windowClosing(WindowEvent e) {}
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (orcon.orconPanel.getCloseButton().isEnabled()) {
                orcon.orconMacro.close();
            }
            prefs.putInt("JFLAME_X", frame.getBounds().x);
            prefs.putInt("JFLAME_Y", frame.getBounds().y);
            prefs.putInt("JFLAME_W", frame.getBounds().width);
            prefs.putInt("JFLAME_H", frame.getBounds().height);
            System.out.println("prefs saved");
        }));
        frame.setVisible(true);
    }
}
