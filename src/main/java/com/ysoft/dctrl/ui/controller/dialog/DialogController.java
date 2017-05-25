package com.ysoft.dctrl.ui.controller.dialog;

import java.net.URL;
import java.util.ResourceBundle;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.controller.LocalizableController;
import com.ysoft.dctrl.ui.dialog.contract.DialogEventData;
import com.ysoft.dctrl.ui.factory.dialog.DialogType;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

/**
 * Created by pilar on 24.5.2017.
 */
public abstract class DialogController extends LocalizableController {
    public DialogController(LocalizationService localizationService, EventBus eventBus, DeeControlContext context) {
        super(localizationService, eventBus, context);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventBus.subscribe(EventType.SHOW_DIALOG.name(), e-> {
            DialogEventData eventData = (DialogEventData) e.getData();
            if(getDialogType() == eventData.getType()) { onShow(eventData.getDialogData()); }
        });
        super.initialize(location, resources);
    }

    protected abstract DialogType getDialogType();
    protected abstract void onShow(Object data);
}
