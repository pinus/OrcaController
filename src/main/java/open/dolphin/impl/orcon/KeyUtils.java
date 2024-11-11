package open.dolphin.impl.orcon;

import org.openqa.selenium.Keys;

import java.awt.event.KeyEvent;
import java.util.Arrays;

/**
 * Selenium の Keys と KeyEvent を関連付ける.
 * @author pns
 */
public class KeyUtils {

    public static Keys[] TYPING_KEYS = new Keys[] {
        Keys.ENTER, Keys.TAB, Keys.BACK_SPACE, Keys.DELETE, Keys.SEMICOLON,
        Keys.MULTIPLY, Keys.SEPARATOR, Keys.SUBTRACT, Keys.DECIMAL, Keys.DIVIDE, Keys.EQUALS, Keys.ADD,
        Keys.NUMPAD0, Keys.NUMPAD1,Keys.NUMPAD2,Keys.NUMPAD3,Keys.NUMPAD4,
        Keys.NUMPAD5,Keys.NUMPAD6,Keys.NUMPAD7,Keys.NUMPAD8,Keys.NUMPAD9,
    };

    public static Keys toSeleniumKey(int keyCode) {
        return switch (keyCode) {
            case KeyEvent.VK_ENTER -> Keys.ENTER;
            case KeyEvent.VK_TAB -> Keys.TAB;
            case KeyEvent.VK_BACK_SPACE -> Keys.BACK_SPACE;
            case KeyEvent.VK_ESCAPE -> Keys.ESCAPE;
            case KeyEvent.VK_DELETE -> Keys.DELETE;
            case KeyEvent.VK_SHIFT -> Keys.SHIFT;
            case KeyEvent.VK_CONTROL -> Keys.CONTROL;
            case KeyEvent.VK_ALT -> Keys.ALT;
            case KeyEvent.VK_SPACE -> Keys.SPACE;
            case KeyEvent.VK_LEFT -> Keys.ARROW_LEFT;
            case KeyEvent.VK_UP -> Keys.ARROW_UP;
            case KeyEvent.VK_RIGHT -> Keys.ARROW_RIGHT;
            case KeyEvent.VK_DOWN -> Keys.ARROW_DOWN;
            case KeyEvent.VK_CANCEL -> Keys.CANCEL;
            case KeyEvent.VK_HELP -> Keys.HELP;
            case KeyEvent.VK_CLEAR -> Keys.CLEAR;
            case KeyEvent.VK_PAUSE -> Keys.PAUSE;
            case KeyEvent.VK_PAGE_UP -> Keys.PAGE_UP;
            case KeyEvent.VK_PAGE_DOWN -> Keys.PAGE_DOWN;
            case KeyEvent.VK_END -> Keys.END;
            case KeyEvent.VK_HOME -> Keys.HOME;
            case KeyEvent.VK_INSERT -> Keys.INSERT;
            case KeyEvent.VK_SEMICOLON -> Keys.SEMICOLON;
            case KeyEvent.VK_EQUALS -> Keys.EQUALS;
            case KeyEvent.VK_NUMPAD0 -> Keys.NUMPAD0;
            case KeyEvent.VK_NUMPAD1 -> Keys.NUMPAD1;
            case KeyEvent.VK_NUMPAD2 -> Keys.NUMPAD2;
            case KeyEvent.VK_NUMPAD3 -> Keys.NUMPAD3;
            case KeyEvent.VK_NUMPAD4 -> Keys.NUMPAD4;
            case KeyEvent.VK_NUMPAD5 -> Keys.NUMPAD5;
            case KeyEvent.VK_NUMPAD6 -> Keys.NUMPAD6;
            case KeyEvent.VK_NUMPAD7 -> Keys.NUMPAD7;
            case KeyEvent.VK_NUMPAD8 -> Keys.NUMPAD8;
            case KeyEvent.VK_NUMPAD9 -> Keys.NUMPAD9;
            case KeyEvent.VK_MULTIPLY -> Keys.MULTIPLY;
            case KeyEvent.VK_ADD -> Keys.ADD;
            case KeyEvent.VK_SEPARATER -> Keys.SEPARATOR;
            case KeyEvent.VK_SUBTRACT -> Keys.SUBTRACT;
            case KeyEvent.VK_DECIMAL -> Keys.DECIMAL;
            case KeyEvent.VK_DIVIDE -> Keys.DIVIDE;
            case KeyEvent.VK_F1 -> Keys.F1;
            case KeyEvent.VK_F2 -> Keys.F2;
            case KeyEvent.VK_F3 -> Keys.F3;
            case KeyEvent.VK_F4 -> Keys.F4;
            case KeyEvent.VK_F5 -> Keys.F5;
            case KeyEvent.VK_F6 -> Keys.F6;
            case KeyEvent.VK_F7 -> Keys.F7;
            case KeyEvent.VK_F8 -> Keys.F8;
            case KeyEvent.VK_F9 -> Keys.F9;
            case KeyEvent.VK_F10 -> Keys.F10;
            case KeyEvent.VK_F11 -> Keys.F11;
            case KeyEvent.VK_F12 -> Keys.F12;
            case KeyEvent.VK_META -> Keys.META;
            default -> Keys.NULL;
        };
    }

    /**
     * KeyTyped で処理されるキーかどうかを返す.
     * @param key target key
     * @return true if the key should be processed through KeyTyped
     */
    public static boolean isTypingKey(Keys key) {
        return Arrays.asList(TYPING_KEYS).contains(key);
    }
}
