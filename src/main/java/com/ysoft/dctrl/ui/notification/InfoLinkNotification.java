package com.ysoft.dctrl.ui.notification;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;

/**
 * Created by pilar on 30.5.2017.
 */
public class InfoLinkNotification extends InfoNotification {
    private Button link;

    public InfoLinkNotification() {
        super();
        link = new Button();
        link.getStyleClass().addAll("link", "transparent");
        Region space = new Region();
        space.getStyleClass().add("space");
        getBaseRow().getChildren().add(2, link);
        getBaseRow().getChildren().add(2, space);
    }

    public void setOnLinkAction(EventHandler<ActionEvent> eventHandler) {
        link.setOnAction(eventHandler);
    }
}
