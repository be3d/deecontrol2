package com.ysoft.dctrl.ui.dialog.contract;

import com.ysoft.dctrl.ui.dialog.DialogType;

/**
 * Created by pilar on 24.5.2017.
 */
public class DialogEventData {
    private final DialogType type;
    private final Object dialogData;

    public DialogEventData(DialogType type) {
        this(type, null);
    }

    public DialogEventData(DialogType type, Object dialogData) {
        this.type = type;
        this.dialogData = dialogData;
    }

    public DialogType getType() {
        return type;
    }

    public Object getDialogData() {
        return dialogData;
    }
}
