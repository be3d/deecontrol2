package com.ysoft.dctrl.ui.notification;

import javafx.scene.layout.Region;

/**
 * Created by pilar on 30.5.2017.
 */
public abstract class IconNotification extends Notification {
    private Region icon;

    public IconNotification() {
        super();
        icon = new Region();
        getBaseRow().getChildren().add(0, icon);
        icon.getStyleClass().addAll("notification-icon", "notification-icon-" + getIconType().name().toLowerCase());
    }

    protected abstract IconType getIconType();
}
