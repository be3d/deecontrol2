package com.ysoft.dctrl.ui;

import javax.annotation.PostConstruct;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.factory.NotificationWrapperFactory;
import com.ysoft.dctrl.ui.notification.Notification;

import javafx.animation.PauseTransition;
import javafx.scene.layout.FlowPane;
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

        root.setPickOnBounds(false);
        wrapper.setPickOnBounds(false);
    }

    public void showNotification(Notification notification) {
        int timeout = notification.getTimeout();

        notification.onShow();
        notification.addOnCloseAction((e) -> {
            timer.stop();
            notification.hide();
        });
        notification.setOnHideHandler(() -> hideNotification(notification));

        if(!wrapper.getChildren().contains(notification)){
            wrapper.getChildren().add(0, notification);
        }

        if(timeout > 0) {
            setTimer(notification, timeout);
        }
    }

    public void hideNotification(Notification notification) {
        wrapper.getChildren().remove(notification);
    }

    private void setTimer(Notification notification, int timeout) {
        timer.setDuration(Duration.seconds(timeout));
        timer.setOnFinished((e) -> notification.hide());
        timer.play();
    }

    public Region getNode() {
        return root;
    }
}
