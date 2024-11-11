package open.dolphin.impl.orcon;

import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * ORCA を遠隔操作するショートカットキーを listen する.
 * @author pns
 */
public class ShortcutListener implements KeyListener {
    private final Macro macro;
    private final Logger logger = LoggerFactory.getLogger(ShortcutListener.class);

    private boolean shift;
    private boolean ctrl;
    private boolean alt;
    private boolean meta;

    public ShortcutListener(Macro macro) {
        this.macro = macro;
    }

    /**
     * KeyEvent の modifier 状態をチェックする.
     * @param e KeyEvent
     */
    private void setModifierState(KeyEvent e) {
        shift = (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0;
        ctrl = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
        alt = (e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
        meta = (e.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
    }

    /**
     * sendThrough マクロを実行.
     * @param key selenium Keys (CharSequence)
     */
    private void sendThrough(CharSequence key) {
        StringBuilder chord = new StringBuilder();
        if (shift) { chord.append(Keys.SHIFT); }
        if (ctrl) { chord.append(Keys.CONTROL); }
        if (alt) { chord.append(Keys.ALT); }
        if (meta) { chord.append(Keys.META); }
        chord.append(key);
        macro.sendThrough(chord);
    }

    /**
     * 通常の文字, shift, tab, backspace, enter などの typingKey はこちらで処理.
     * @param e the event to be processed
     */
    @Override
    public void keyTyped(KeyEvent e) {
        setModifierState(e);
        if (alt || ctrl) { return; }

        // そのまま流す
        String key = String.valueOf(e.getKeyChar());
        logger.info("typed: " + key);
        sendThrough(key);
        e.consume();
    }

    /**
     * keyTyped で取れない alt, ctrl, fn キーなどははこちらで処理.
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        setModifierState(e);
        Keys key = KeyUtils.toSeleniumKey(e.getKeyCode()); // 特殊キー以外は Keys.NULL

        // 以下のキーは, typed で処理されるのでスキップ
        if (!ctrl && !alt && KeyUtils.isTypingKey(key)) {
            return;
        }

        logger.info("pressed: " + KeyEvent.getKeyText(e.getKeyCode()));

        // ショートカットキープログラム
        if (ctrl && e.getKeyCode() == KeyEvent.VK_ENTER) {
            // 中途終了展開 CTRL + ENTER
            macro.chutoTenkai();

        } else if (ctrl && e.getKeyCode() == KeyEvent.VK_K) {
            // 外来管理加算削除 CTRL + K
            macro.gairaiKanriKensaku();

        } else if (ctrl & e.getKeyCode() == KeyEvent.VK_B) {
            // (C02)病名登録へ移動
            macro.byomeiToroku();

        } else if (ctrl & (e.getKeyCode() == KeyEvent.VK_0 || e.getKeyCode() == KeyEvent.VK_1 || e.getKeyCode() == KeyEvent.VK_2)) {
            // (K03)診療行為入力ｰ請求確認で, 領収書/明細書/処方箋を打ち出すかどうか
            macro.printForms(e.getKeyCode() - KeyEvent.VK_0);

        } else {
            // ショートカットキー以外は, そのまま流す
            sendThrough(key);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}
