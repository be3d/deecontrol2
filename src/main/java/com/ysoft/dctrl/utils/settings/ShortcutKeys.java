package com.ysoft.dctrl.utils.settings;

import com.ysoft.dctrl.utils.OSVersion;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * Created by kuhn on 9/15/2017.
 */
public class ShortcutKeys {

    public static final KeyCodeCombination UNDO = new KeyCodeCombination(KeyCode.Y, KeyCombination.SHORTCUT_DOWN);
    public static final KeyCodeCombination REDO = new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN);
    public static final KeyCodeCombination SELECT_ALL = new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN);
    public static final KeyCodeCombination ZOOM_IN = new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_DOWN);
    public static final KeyCodeCombination ZOOM_OUT = new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_DOWN);
    public static final KeyCodeCombination COPY = new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN);
    public static final KeyCodeCombination PASTE = new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN);
    public static final KeyCodeCombination DUPLICATE = new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN);
    public static final KeyCodeCombination DELETE;

    static {
        KeyCodeCombination macDelete = new KeyCodeCombination(KeyCode.BACK_SPACE, KeyCombination.SHORTCUT_DOWN);
        KeyCodeCombination winDelete = new KeyCodeCombination(KeyCode.DELETE);
        DELETE = OSVersion.is(OSVersion.MAC) ? macDelete : winDelete;
    }

}