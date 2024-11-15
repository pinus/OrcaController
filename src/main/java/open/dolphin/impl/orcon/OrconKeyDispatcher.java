package open.dolphin.impl.orcon;

import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class OrconKeyDispatcher implements KeyEventDispatcher {
    private boolean enabled = false;
    private OrconMacro orconMacro;
    private final Logger logger = LoggerFactory.getLogger(OrconKeyDispatcher.class);

    private boolean shift;
    private boolean ctrl;
    private boolean alt;
    private boolean meta;

    public OrconKeyDispatcher(OrconMacro orconMacro) {
        this.orconMacro = orconMacro;
    }

    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }

    /**
     * KeyEvent の modifier 状態をチェックする.
     * @param e KeyEvent
     */
    private void setModifierState(KeyEvent e) {
        shift = (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0;
        ctrl = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
        alt = (e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
        meta = (e.getModifiersEx() & InputEvent.META_DOWN_MASK) != 0;
    }

    /**
     * KeyEvent を CharSequence に変換して selenium に流す.
     * @param e KeyEvent
     */
    private void sendThrough(KeyEvent e) {
        //logger.info("pressed = " + e);
        // 単独 modifier は受けない
        if (KeyUtils.isModifier(e.getKeyCode())) { return; }

        // modifier を selenium Keys に変換してセット
        StringBuilder chord = new StringBuilder();
        if (shift) { chord.append(Keys.SHIFT); }
        if (ctrl) { chord.append(Keys.CONTROL); }
        if (alt) { chord.append(Keys.ALT); }
        if (meta) { chord.append(Keys.META); }
        // 特殊キーを selenium Keys に変換
        Keys specialKey = KeyUtils.toSeleniumKey(e.getKeyCode());
        if (specialKey != null) {
            logger.info("specialKey = " + specialKey);
            chord.append(specialKey);
        } else {
            // 通常キーは getChar で受ける
            logger.info("normal key = " + e.getKeyChar());
            chord.append(e.getKeyChar());
        }
        for (int i=0; i<chord.length(); i++) {
            int specialKeyCode = KeyUtils.toKeyCode(chord.charAt(i));
            String keytext = specialKeyCode < 65535?
                KeyEvent.getKeyText(specialKeyCode) : String.valueOf(chord.charAt(i));
            logger.info(String.format("chord[%d] keyCode:%d converted:%d keyChar:%s(%d) charInChord:%s(%s)",
                i, e.getKeyCode(), specialKeyCode, e.getKeyChar(), (int)e.getKeyChar(), chord.charAt(i), keytext));
        }

        orconMacro.sendThrough(chord);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (enabled) {
            setModifierState(e);
            //
            // ショートカットキーでマクロを呼ぶ
            //
            if (ctrl && e.getKeyCode() == KeyEvent.VK_ENTER) {
                // 中途終了展開 CTRL + ENTER
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (orconMacro.whereAmI().equals("K02")) {
                        orconMacro.k20ChutoTenkai();
                    }
                }

            } else if (ctrl && e.getKeyCode() == KeyEvent.VK_K) {
                // 外来管理加算削除 CTRL + K
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (orconMacro.whereAmI().equals("K02")) {
                        orconMacro.k02GairaiKanriDelete();
                    }
                }

            } else if (ctrl && e.getKeyCode() == KeyEvent.VK_B) {
                // (C02)病名登録へ移動
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (orconMacro.whereAmI().equals("K02")) {
                        orconMacro.k02ToByomeiToroku();
                    }
                }

            } else if (ctrl && (e.getKeyCode() == KeyEvent.VK_0 || e.getKeyCode() == KeyEvent.VK_1 || e.getKeyCode() == KeyEvent.VK_2)) {
                // (K03)診療行為入力ｰ請求確認で, 領収書/明細書/処方箋を打ち出すかどうか
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (orconMacro.whereAmI().equals("K03")) {
                        orconMacro.k03SelectPrintForms(e.getKeyCode() - KeyEvent.VK_0);
                    }
                }

            } else if (ctrl && e.getKeyCode() == KeyEvent.VK_V) {
                // 患者番号送信
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    switch (orconMacro.whereAmI()) {
                        case "K02" -> orconMacro.k02SendPtNum();
                        case "C02" -> orconMacro.c02SendPtNum();
                    }
                }

            } else {
                // ショートカットキー以外は, そのまま流す
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    sendThrough(e);
                }
            }
            // block
            return true;
        }

        return false;
    }
}
