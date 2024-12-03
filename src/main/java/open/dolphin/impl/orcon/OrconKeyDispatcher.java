package open.dolphin.impl.orcon;

import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.stream.Stream;

/**
 * キーを横取りして orca に送るための key dispatcher.
 * @author pns
 */
public class OrconKeyDispatcher implements KeyEventDispatcher {
    private final OrcaController context;
    private final OrconMacro orconMacro;
    private final Logger logger = LoggerFactory.getLogger(OrconKeyDispatcher.class);

    private boolean shift;
    private boolean ctrl;
    private boolean alt;
    private boolean meta;

    public OrconKeyDispatcher(OrcaController context) {
        this.context = context;
        this.orconMacro = context.getOrconMacro();
    }

    /**
     * KeyEvent の modifier 状態をチェックする.
     * @param e KeyEvent
     */
    private void setModifierState(KeyEvent e) {
        shift = e.isShiftDown();
        ctrl = e.isControlDown();
        alt = e.isAltDown();
        meta = e.isMetaDown();
    }

    /**
     * key code が chord 文字列と一致しているかどうか.
     * @param code key code
     * @param chord string like "shift ctrl ENTER"
     * @return whether key code equals chord string
     */
    private boolean is(int code, String chord) {
        String[] token = chord.split("\\s");
        int keyValue = KeyUtils.getVKValue(token[token.length-1]);
        // modifier が指定されていない場合は, modifier は考慮しない
        if (token.length == 1) { return code == keyValue; }
        // modifier が指定されている場合
        boolean s = chord.contains("shift");
        boolean c = chord.contains("ctrl");
        boolean a = chord.contains("alt");
        boolean m = chord.contains("meta");
        return (code == keyValue) && (!s ^ shift) && (!c ^ ctrl) && (!a ^ alt) && (!m ^ meta);
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
            //logger.info("specialKey = " + specialKey);
            chord.append(specialKey);
        } else {
            // 通常キーは getChar で受ける
            //logger.info("normal key = " + e.getKeyChar());
            chord.append(e.getKeyChar());
        }
//        for (int i=0; i<chord.length(); i++) {
//            int specialKeyCode = KeyUtils.toKeyCode(chord.charAt(i));
//            String keytext = specialKeyCode < 65535?
//                KeyEvent.getKeyText(specialKeyCode) : String.valueOf(chord.charAt(i));
//            logger.info(String.format("chord[%d] keyCode:%d converted:%d keyChar:%s(%d) charInChord:%s(%s)",
//                i, e.getKeyCode(), specialKeyCode, e.getKeyChar(), (int)e.getKeyChar(), chord.charAt(i), keytext));
//        }

        orconMacro.sendThrough(chord);
    }

    /**
     * DISABLE: 全て dolphin へ送る, FULL: コマンドキーだけ dolphin へ送る
     * STEALTH: 指定したものだけ dolphin へ送る
     * @param e the KeyEvent to dispatch
     * @return true to block
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        setModifierState(e);
        int keyCode = e.getKeyCode();

        // 横取りしないキーを false で返す
        if (context.getMode() == OrcaController.Mode.DISABLE) {
            // 横取りしない
            return false;

        } else if (context.getMode() == OrcaController.Mode.FULL) {
            // コマンドキーを横取りしない
            if (meta) { return false; }

        } else { // Mode.STEALTH
            // ショートカット、ファンクションキーだけ通す
            if (Stream.of("ctrl shift ENTER", "ctrl K", "ctrl B", "ctrl 0", "ctrl 1", "ctrl 2", "ctrl V", "alt ENTER",
                    "alt meta A", "alt BACK_SPACE", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10", "F11", "F12")
                .noneMatch(chord -> is(keyCode, chord))) {
                return false;
            }
        }

        //
        // ショートカットキーでマクロを呼ぶ
        //
        if (is(keyCode, "shift ctrl ENTER")) {
            // 中途終了展開 SHIFT + CTRL + ENTER
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                if (orconMacro.whereAmI().equals("K02")) {
                    orconMacro.k20ChutoTenkai();
                }
            }

        } else if (is(keyCode, "ctrl K")) {
            // 外来管理加算削除 CTRL + K
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                if (orconMacro.whereAmI().equals("K02")) {
                    orconMacro.k02GairaiKanriDelete();
                }
            }

        } else if (is(keyCode, "ctrl B")) {
            // (C02)病名登録へ移動
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                if (orconMacro.whereAmI().equals("K02")) {
                    orconMacro.k02ToByomeiToroku();
                }
            }

        } else if (Stream.of("ctrl 0", "ctrl 1", "ctrl 2").anyMatch(chord -> is(keyCode, chord))) {
            // (K03)診療行為入力ｰ請求確認で, 領収書/明細書/処方箋を打ち出すかどうか
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                if (orconMacro.whereAmI().equals("K03")) {
                    orconMacro.k03SelectPrintForms(e.getKeyCode() - KeyEvent.VK_0);
                }
            }

//        } else if (is(keyCode, "ctrl V")) {
//            // 患者番号送信
//            if (e.getID() == KeyEvent.KEY_RELEASED) {
//                switch (orconMacro.whereAmI()) {
//                    case "K02" -> orconMacro.k02SendPtNum();
//                    case "C02" -> orconMacro.c02SendPtNum();
//                }
//            }

        } else if (is(keyCode, "alt ENTER")) {
            // alt + ENTER で orca enter 入力
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                orconMacro.sendThrough(Keys.ENTER);
            }

        } else if (is(keyCode, "alt meta A")) {
            // alt + command + A で 全選択
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                orconMacro.sendThrough(Keys.chord(Keys.META, "A"));
            }

        } else if (is(keyCode, "alt BACK_SPACE")) {
            // alt + backspace だと KEY_RELEASED しか発生しない?
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                orconMacro.sendThrough(Keys.BACK_SPACE);
            }

        } else {
            // ショートカットキー以外は, そのまま流す
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                sendThrough(e);
            }
        }
        // block
        return true;
    }
}
