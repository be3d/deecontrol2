package com.ysoft.dctrl.utils.settings;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * Created by kuhn on 9/15/2017.
 */
public class ShortcutKeys {

    public static final KeyCodeCombination UNDO = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
    public static final KeyCodeCombination REDO = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
    public static final KeyCodeCombination SELECT_ALL = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);
    public static final KeyCodeCombination ZOOM_IN = new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_DOWN);
    public static final KeyCodeCombination ZOOM_OUT = new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_DOWN);

}