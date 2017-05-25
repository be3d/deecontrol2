package com.ysoft.dctrl.ui.control.complex;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Created by pilar on 25.5.2017.
 */
public abstract class ComplexControl extends VBox {
    protected Label label;
    protected Label toolTip;

    public ComplexControl() {
        label = new Label();
        toolTip = new Label();

        initControl();

        toolTip.textProperty().addListener((o, oldValue, newValue) -> {
            if(newValue == null || newValue.isEmpty()) {
                getChildren().add(toolTip);
            } else {
                getChildren().remove(toolTip);
            }
        });

        getChildren().addAll(label, getControl());
    }

    public String getLabelText() {
        return label.getText();
    }

    public void setLabelText(String labelText) {
        label.setText(labelText);
    }

    public String getToolTipText() {
        return toolTip.getText();
    }

    public void setToolTipText(String toolTipText) {
        toolTip.setText(toolTipText);
    }

    protected abstract Control getControl();
    protected abstract void initControl();
    protected abstract void setOnAction(EventHandler<ActionEvent> eventHandler);
}
