package com.ysoft.dctrl.ui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.dialog.Dialog;
import com.ysoft.dctrl.ui.dialog.contract.DialogEventData;
import com.ysoft.dctrl.ui.factory.dialog.DialogType;
import com.ysoft.dctrl.ui.factory.dialog.PreferencesFactory;
import com.ysoft.dctrl.ui.factory.dialog.WrapperFactory;

import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

/**
 * Created by pilar on 23.5.2017.
 */

@Component
public class DialogManager {
    private EventBus eventBus;
    private Pane wrapper;

    private Map<DialogType, Dialog> dialogs;

    @Autowired
    public DialogManager(EventBus eventBus,
                         WrapperFactory wrapperFactory,
                         PreferencesFactory preferencesFactory
    ) {
        this.eventBus = eventBus;
        wrapper = wrapperFactory.buildWrapper();
        dialogs = new ConcurrentHashMap<>();
        dialogs.put(DialogType.PREFERENCES, preferencesFactory.buildPreferences());
    }

    @PostConstruct
    public void init() {
        wrapper.setPickOnBounds(false);

        dialogs.forEach((k, d) -> d.getNode().visibleProperty().addListener((o, oldValue, newValue) -> {
            if(newValue) {
                wrapper.getChildren().add(d.getNode());
            } else {
                wrapper.getChildren().remove(d.getNode());
            }
        }));

        eventBus.subscribe(EventType.SHOW_DIALOG.name(), (e) ->{
            showDialog((DialogEventData) e.getData());
        });
    }

    public void showDialog(DialogEventData data) {
        if(dialogs.containsKey(data.getType())) {
            Dialog d = dialogs.get(data.getType());
            d.show();
        } else {
            System.err.println("fuck dialog not found");
        }

    }

    public Region getNode() { return wrapper; }
}
