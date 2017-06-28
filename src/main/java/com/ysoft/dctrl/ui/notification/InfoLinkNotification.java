package com.ysoft.dctrl.ui.notification;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

/**
 * Created by pilar on 30.5.2017.
 */
public class InfoLinkNotification extends InfoNotification {
    private Button link;

    public InfoLinkNotification() {
        super();
        link = new Button();
        link.getStyleClass().add("link");

        getBaseRow().getChildren().add(2, link);
    }

    public void setOnLinkAction(EventHandler<ActionEvent> eventHandler) {
        link.setOnAction(eventHandler);
    }
}
