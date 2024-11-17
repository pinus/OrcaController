package open.dolphin.impl.orcon;

import org.openqa.selenium.Keys;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Selenium の Keys と KeyEvent を関連付ける.
 * @author pns
 */
public class KeyUtils {
    /**
     * KeyCode -> Selenium Keys のマップ
     */
    public static Map<Integer, Keys> MAP = new HashMap<>();
    static {
        MAP.put(KeyEvent.VK_SHIFT, Keys.SHIFT);
        MAP.put(KeyEvent.VK_CONTROL, Keys.CONTROL);
        MAP.put(KeyEvent.VK_ALT, Keys.ALT);
        MAP.put(KeyEvent.VK_META, Keys.META);
        MAP.put(KeyEvent.VK_ENTER, Keys.ENTER);
        MAP.put(KeyEvent.VK_TAB, Keys.TAB);
        MAP.put(KeyEvent.VK_BACK_SPACE, Keys.BACK_SPACE);
        MAP.put(KeyEvent.VK_ESCAPE, Keys.ESCAPE);
        MAP.put(KeyEvent.VK_DELETE, Keys.DELETE);
        MAP.put(KeyEvent.VK_SPACE, Keys.SPACE);
        MAP.put(KeyEvent.VK_LEFT, Keys.ARROW_LEFT);
        MAP.put(KeyEvent.VK_UP, Keys.ARROW_UP);
        MAP.put(KeyEvent.VK_RIGHT, Keys.ARROW_RIGHT);
        MAP.put(KeyEvent.VK_DOWN, Keys.ARROW_DOWN);
        MAP.put(KeyEvent.VK_CANCEL, Keys.CANCEL);
        MAP.put(KeyEvent.VK_HELP, Keys.HELP);
        MAP.put(KeyEvent.VK_CLEAR, Keys.CLEAR);
        MAP.put(KeyEvent.VK_PAUSE, Keys.PAUSE);
        MAP.put(KeyEvent.VK_PAGE_UP, Keys.PAGE_UP);
        MAP.put(KeyEvent.VK_PAGE_DOWN, Keys.PAGE_DOWN);
        MAP.put(KeyEvent.VK_END, Keys.END);
        MAP.put(KeyEvent.VK_HOME, Keys.HOME);
        MAP.put(KeyEvent.VK_INSERT, Keys.INSERT);
        MAP.put(KeyEvent.VK_SEMICOLON, Keys.SEMICOLON);
        MAP.put(KeyEvent.VK_EQUALS, Keys.EQUALS);
        MAP.put(KeyEvent.VK_NUMPAD0, Keys.NUMPAD0);
        MAP.put(KeyEvent.VK_NUMPAD1, Keys.NUMPAD1);
        MAP.put(KeyEvent.VK_NUMPAD2, Keys.NUMPAD2);
        MAP.put(KeyEvent.VK_NUMPAD3, Keys.NUMPAD3);
        MAP.put(KeyEvent.VK_NUMPAD4, Keys.NUMPAD4);
        MAP.put(KeyEvent.VK_NUMPAD5, Keys.NUMPAD5);
        MAP.put(KeyEvent.VK_NUMPAD6, Keys.NUMPAD6);
        MAP.put(KeyEvent.VK_NUMPAD7, Keys.NUMPAD7);
        MAP.put(KeyEvent.VK_NUMPAD8, Keys.NUMPAD8);
        MAP.put(KeyEvent.VK_NUMPAD9, Keys.NUMPAD9);
        MAP.put(KeyEvent.VK_MULTIPLY, Keys.MULTIPLY);
        MAP.put(KeyEvent.VK_ADD, Keys.ADD);
        MAP.put(KeyEvent.VK_SEPARATER, Keys.SEPARATOR);
        MAP.put(KeyEvent.VK_SUBTRACT, Keys.SUBTRACT);
        MAP.put(KeyEvent.VK_DECIMAL, Keys.DECIMAL);
        MAP.put(KeyEvent.VK_DIVIDE, Keys.DIVIDE);
        MAP.put(KeyEvent.VK_F1, Keys.F1);
        MAP.put(KeyEvent.VK_F2, Keys.F2);
        MAP.put(KeyEvent.VK_F3, Keys.F3);
        MAP.put(KeyEvent.VK_F4, Keys.F4);
        MAP.put(KeyEvent.VK_F5, Keys.F5);
        MAP.put(KeyEvent.VK_F6, Keys.F6);
        MAP.put(KeyEvent.VK_F7, Keys.F7);
        MAP.put(KeyEvent.VK_F8, Keys.F8);
        MAP.put(KeyEvent.VK_F9, Keys.F9);
        MAP.put(KeyEvent.VK_F10, Keys.F10);
        MAP.put(KeyEvent.VK_F11, Keys.F11);
        MAP.put(KeyEvent.VK_F12, Keys.F12);
    }

    /**
     * 特殊キーを selenium Keys に変換.
     * @param keyCode int code
     * @return special keys for selenium if present, otherwise null
     */
    public static Keys toSeleniumKey(int keyCode) {
        return MAP.get(keyCode);
    }

    /**
     * selenium 特殊キーを keycode に変換.
     * @param seleniumKey selenium keys
     * @return keycode if present, otherwise 65535
     */
    public static int toKeyCode(char seleniumKey) {
        int ret = 65535;
        for (int keyCode : MAP.keySet()) {
            if (MAP.get(keyCode).charAt(0) == seleniumKey) {
                ret = keyCode;
                break;
            }
        }
        return ret;
    }

    /**
     * KeyCode が modifier かどうかを返す.
     * @param k KeyCode
     * @return true if modifier
     */
    public static boolean isModifier(int k) {
        return k == KeyEvent.VK_SHIFT || k == KeyEvent.VK_CONTROL || k == KeyEvent.VK_ALT || k == KeyEvent.VK_META;
    }
}
