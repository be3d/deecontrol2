package com.ysoft.dctrl.ui;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.factory.NotificationWrapperFactory;
import com.ysoft.dctrl.ui.notification.Notification;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Created by pilar on 30.5.2017.
 */
@Component
public class NotificationManager {
    private EventBus eventBus;
    private StackPane root;
    private FlowPane wrapper;

    private PauseTransition timer;

    @Autowired
    public NotificationManager(NotificationWrapperFactory notificationWrapperFactory, EventBus eventBus) {
        this.root = notificationWrapperFactory.buildNotificationWrapper();
        this.wrapper = (FlowPane) root.getChildren().get(0);
        this.eventBus = eventBus;
        this.timer = new PauseTransition();
    }

    @PostConstruct
    public void init() {
        eventBus.subscribe(EventType.SHOW_NOTIFICATION.name(), (e) -> {
            showNotification((Notification) e.getData());
        });
    }

    public void showNotification(Notification notification) {
        wrapper.getChildren().clear();
        int timeout = notification.getTimeout();

        notification.addOnCloseAction((e) -> {
            timer.stop();
            hideNotification(notification);
        });
        notification.setOnHideHandler(() -> hideNotification(notification));

        wrapper.getChildren().add(notification);

        if(timeout > 0) {
            setTimer(notification, timeout);
        }
    }

    public void hideNotification(Notification notification) {
        wrapper.getChildren().remove(notification);
    }

    private void setTimer(Notification notification, int timeout) {
        timer.setDuration(Duration.seconds(timeout));
        timer.setOnFinished((e) -> hideNotification(notification));
        timer.play();
    }

    public Region getNode() {
        return root;
    }
}
