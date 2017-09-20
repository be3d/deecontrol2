package com.ysoft.dctrl.ui.notification;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;

public class AlertLinkNotification extends AlertNotification {
    private Button link;

    public AlertLinkNotification() {
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

    public void setLinkText(String linkText) {
        link.setText(linkText);
    }
}
