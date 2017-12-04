package com.ysoft.dctrl.ui.notification;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.utils.Worker;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Created by pilar on 30.5.2017.
 */
public class Notification extends VBox {
    private static final int DEFAULT_TIMEOUT = 6;
    private static final double DEFAULT_FADE_OUT_DURATION_MILLIS = 500;

    private Label label;
    private Button close;
    private HBox baseRow;

    private int timeout;
    private double fadeOutDuration;
    private PauseTransition timer;

    private Worker onHide;

    public Notification() {
        timeout = DEFAULT_TIMEOUT;
        fadeOutDuration = DEFAULT_FADE_OUT_DURATION_MILLIS;
        label = new Label();
        close = new Button();
        baseRow = new HBox();
        onHide = null;
        timer = new PauseTransition();

        HBox.setHgrow(this, Priority.NEVER);
        HBox.setHgrow(label, Priority.ALWAYS);
        baseRow.getChildren().addAll(label, new Separator(), close);
        getStyleClass().addAll("panel", "notification");
        baseRow.getStyleClass().add("base-row");
        close.getStyleClass().add("close");

        getChildren().add(baseRow);
        addEventHandler(MouseEvent.ANY, Event::consume);
        addOnCloseAction((e) -> hide());
    }

    protected HBox getBaseRow() {
        return baseRow;
    }

    public void setLabelText(String labelText) {
        label.setText(labelText);
    }

    public String getLabelText() {
        return label.getText();
    }

    public void addOnCloseAction(EventHandler<ActionEvent> eventHandler) {
        close.addEventHandler(ActionEvent.ACTION, eventHandler);
    }


    public void setTimeout(int seconds) {
        this.timeout = seconds;
    }

    public int getTimeout() {
        return timeout;
    }

    public void hide(){
        hide(fadeOutDuration);
    }

    public void hide(double durationMillis){
        timer.stop();

        FadeTransition ft = new FadeTransition(Duration.millis(durationMillis), this);
        ft.setInterpolator(Interpolator.EASE_OUT);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);

        ft.setOnFinished(e -> {
            if(onHide == null) return;
            onHide.work();
        });
        ft.play();
    }

    public void setOnHideHandler(Worker worker) {
        onHide = worker;
    }

    public void onShow(){
        if(timeout > 0){
            setTimer();
        }
        setOpacity(1.0);
    }

    private void setTimer() {
        timer.setDuration(Duration.seconds(timeout));
        timer.setOnFinished((e) -> hide());
        timer.play();
    }
}
