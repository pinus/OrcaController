package open.dolphin.impl.orcon;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Objects;

/**
 * ORCA Controller Panel.
 * @author pns
 */
public class OrconPanel {
    private static final int TEXT_HEIGHT = 26;
    private static final int LABEL_WIDTH = 78;
    private static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(16,16,16,16);

    private JPanel orconPanel;
    private JTextField addressField;
    private JTextField userField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton closeButton;

    public OrconPanel() {
        initComponents();
    }

    private void initComponents() {
        orconPanel = new JPanel();
        orconPanel.setBorder(EMPTY_BORDER);
        GridLayouter layouter = new GridLayouter();
        orconPanel.setLayout(layouter.getGridBagLayout());

        JLabel addressLabel = new JLabel("アドレス：");
        layouter.fixSize(addressLabel, LABEL_WIDTH);
        layouter.setConstraints(addressLabel, 0, 0, 1, 0);
        orconPanel.add(addressLabel);

        addressField = new JTextField();
        layouter.fixSize(addressField, Integer.MAX_VALUE);
        layouter.setConstraints(addressField, 1, 0, GridBagConstraints.REMAINDER, 1);
        orconPanel.add(addressField);

        JLabel userLabel = new JLabel("ユーザー：");
        layouter.fixSize(userLabel, LABEL_WIDTH);
        layouter.setConstraints(userLabel, 0, 1, 1, 0);
        orconPanel.add(userLabel);

        userField = new JTextField();
        layouter.fixSize(userField, 192);
        layouter.setConstraints(userField, 1, 1, 1, 0);
        orconPanel.add(userField);

        JLabel strut = new JLabel();
        layouter.fixSize(strut, 32);
        layouter.setConstraints(strut, 2, 1, 1, 0);
        orconPanel.add(strut);

        JLabel passwordLabel = new JLabel("パスワード：");
        layouter.fixSize(passwordLabel, LABEL_WIDTH);
        layouter.setConstraints(passwordLabel, 3, 1, 1, 0);
        orconPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        layouter.fixSize(passwordField, 192);
        layouter.setConstraints(passwordField, 4, 1, 1, 0);
        orconPanel.add(passwordField);

        JLabel glue = new JLabel();
        layouter.fixSize(glue, Integer.MAX_VALUE);
        layouter.setConstraints(glue, 5, 1, GridBagConstraints.REMAINDER, 1);
        orconPanel.add(glue);

        loginButton = new JButton("ORCA ログイン") {
            // デフォルトボタンの文字を白くする
            private final boolean isMac = System.getProperty("os.name").startsWith("Mac");
            private Window parent;
            public void paint(Graphics g) {
                if (isMac) {
                    parent = SwingUtilities.getWindowAncestor(this);
                    if (model.isEnabled()) {
                        setForeground(Objects.nonNull(parent) && parent.isActive() && isDefaultButton() && !model.isPressed()?
                            Color.WHITE : Color.BLACK);
                    }
                }
                super.paint(g);
            }
        };
        closeButton = new JButton("ORCA 終了");
        closeButton.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 16,0));
        buttonPanel.add(loginButton);
        buttonPanel.add(closeButton);
        layouter.fixSize(buttonPanel, Integer.MAX_VALUE);
        layouter.setConstraints(buttonPanel, 0, 2, GridBagConstraints.REMAINDER, 1);
        orconPanel.add(buttonPanel);

        JLabel dummy = new JLabel("ORCA Controller");
        //dummy.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 28));
        layouter.setConstraints(dummy, 0,3, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 1, 1);
        orconPanel.add(dummy);
    }

    /**
     * ログイン状態に応じて, component enable/disable を制御.
     * @param login ログイン中かどうか
     */
    public void setLoginState(boolean login) {
            loginButton.setEnabled(!login);
            closeButton.setEnabled(login);
            addressField.setEnabled(!login);
            addressField.setFocusable(!login);
            userField.setEnabled(!login);
            userField.setFocusable(!login);
            passwordField.setEnabled(!login);
            passwordField.setFocusable(!login);
    }

    public JPanel getPanel() { return orconPanel; }

    public JTextField getAddressField() { return addressField; }

    public void setAddressField(String address) {
        addressField.setText(address);
    }

    public JTextField getUserField() { return userField; }

    public void setUserField(String user) {
        userField.setText(user);
    }

    public JPasswordField getPasswordField() { return passwordField; }

    public void setPasswordField(String password) {
        passwordField.setText(password);
    }

    public JButton getLoginButton() { return loginButton; }

    public JButton getCloseButton() { return closeButton; }

    /**
     * GridBagLayout utility.
     */
    private static class GridLayouter {
        private final GridBagLayout gb;
        private final GridBagConstraints gbc;

        public GridLayouter() {
            gb = new GridBagLayout();
            gbc = new GridBagConstraints();
        }

        public GridBagLayout getGridBagLayout() { return gb; }

        public GridBagConstraints getGridBagConstraints () { return gbc; }

        // X 方向の constaints をセットする
        public void setConstraints(Component target, int x, int y, int width, double weight) {
            setConstraints(target, x, y, width, 1, weight, 0);
        }

        // 縦横両方の constraints をセットする
        public void setConstraints(Component target, int x, int y, int width, int height, double weightx, double weighty) {
            gbc.gridx = x; gbc.gridy = y;
            gbc.gridwidth = width; gbc.gridheight = height;
            gbc.weightx = weightx; gbc.weighty = weighty;
            gb.setConstraints(target, gbc);
        }

        // component size を固定する
        public void fixSize(Component target, int width) {
            Dimension size = new Dimension(width, TEXT_HEIGHT);
            target.setPreferredSize(size);
            target.setMaximumSize(size);
            target.setMinimumSize(size);
        }
    }
}
