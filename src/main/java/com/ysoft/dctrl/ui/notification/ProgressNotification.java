package com.ysoft.dctrl.ui.notification;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

/**
 * Created by pilar on 30.5.2017.
 */
public class ProgressNotification extends Notification {
    private static final int DEFAULT_HIDE_TIMEOUT = 5;

    private ProgressBar progressBar;
    private Label percents;
    private int collapseTimeout;
    private Timeline collapseTimer;

    public ProgressNotification() {
        super();
        super.setTimeout(0);
        setTimeout(DEFAULT_HIDE_TIMEOUT);

        progressBar = new ProgressBar();
        progressBar.getStyleClass().add("progress");
        getChildren().add(0, progressBar);
        getStyleClass().add("progress");

        percents = new Label();
        getBaseRow().getChildren().add(1, percents);

        setProgress(0);

        collapseTimer = new Timeline();
        parentProperty().addListener((o, oldValue, newValue) -> {
            if(newValue != null) { onShow(); }
        });
    }

    public void setProgress(double value) {
        percents.setText(Math.round(value*100) + "%");
        progressBar.setProgress(value);
    }

    @Override
    public void setTimeout(int time) {
        collapseTimeout = time;
    }

    @Override
    public void onShow() {
        super.onShow();
        setProgress(0);
        collapse();
    }

    private void collapse() {
        collapseTimer.getKeyFrames().addAll(getDelaKeyFrame()/*, getCollapseKeyFrame()/*, getExpandKeyFrame()*/);
        collapseTimer.setCycleCount(1);
        collapseTimer.playFromStart();
    }

    private KeyFrame getDelaKeyFrame() {
        return new KeyFrame(Duration.seconds(collapseTimeout), (e) -> System.err.println(getPrefHeight()));
    }

    private KeyFrame getCollapseKeyFrame() {
        KeyValue k1 = new KeyValue(getBaseRow().scaleYProperty(), 0);
        KeyValue k2 = new KeyValue(prefHeightProperty(), 15);

        return new KeyFrame(Duration.millis(1000), k1, k2);
    }

    private KeyFrame getExpandKeyFrame() {
        KeyValue k1 = new KeyValue(getBaseRow().scaleYProperty(), 1);
        KeyValue k2 = new KeyValue(prefHeightProperty(), 60);

        return new KeyFrame(Duration.millis(1000), k1, k2);
    }
}
