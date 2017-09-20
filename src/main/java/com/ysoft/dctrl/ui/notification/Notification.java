package com.ysoft.dctrl.ui.notification;

import com.ysoft.dctrl.utils.Worker;

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

/**
 * Created by pilar on 30.5.2017.
 */
public class Notification extends VBox {
    private static final int DEFAULT_TIMEOUT = 5;

    private Label label;
    private Button close;
    private HBox baseRow;

    private int timeout;

    private Worker onHide;

    public Notification() {
        timeout = DEFAULT_TIMEOUT;
        label = new Label();
        close = new Button();
        baseRow = new HBox();
        onHide = null;

        HBox.setHgrow(this, Priority.NEVER);
        HBox.setHgrow(label, Priority.ALWAYS);
        baseRow.getChildren().addAll(label, new Separator(), close);
        getStyleClass().addAll("panel", "notification");
        baseRow.getStyleClass().add("base-row");
        close.getStyleClass().add("close");

        getChildren().add(baseRow);
        addEventHandler(MouseEvent.ANY, Event::consume);
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

    public void hide() {
        if(onHide == null) return;
        onHide.work();
    }

    public void setOnHideHandler(Worker worker) {
        onHide = worker;
    }
}
