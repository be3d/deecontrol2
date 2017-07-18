package com.ysoft.dctrl.ui.control;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.sun.javafx.collections.TrackableObservableList;

import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Created by pilar on 15.5.2017.
 */

public class DialogPane extends VBox {

    protected Label headerLabel;
    protected Button close;
    private HeaderBox header;

    private EventHandler<ActionEvent> onCloseHandler;

    public DialogPane() {
        headerLabel = new Label();
        close = new Button();
        header = new HeaderBox();
        header.getChildren().addAll(headerLabel, close);
        StackPane.setAlignment(headerLabel, Pos.CENTER_LEFT);
        StackPane.setAlignment(close, Pos.CENTER_RIGHT);
        ObservableList<Node> children = getChildren();
        children.addAll(header);
        children.addListener((ListChangeListener<? super Node>) (e) -> {
            if(children.indexOf(header) != 0) {
                children.remove(header);
                children.add(0, header);
            }
        });

        close.setOnAction((e) -> {
            if(onCloseHandler != null) onCloseHandler.handle(e);
            if(!e.isConsumed()) setVisible(false);
        });
    }
    public StringProperty textProperty() {
        return headerLabel.textProperty();
    }

    public final void setText(String value) { headerLabel.textProperty().setValue(value); }

    public String getText() { return headerLabel.textProperty().getValue(); }

    public void setOnCloseAction(EventHandler<ActionEvent> value) {
        onCloseHandler = value;
    }

    public void close() {
        close.fire();
    }
}
