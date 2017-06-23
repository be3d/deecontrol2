package com.ysoft.dctrl.ui.control.complex;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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

        getStyleClass().addAll("complex-control");
        toolTip.getStyleClass().addAll("tooltip");

        getChildren().addAll(label, getControl());
        getControl().setMaxWidth(Double.MAX_VALUE);
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
        if(toolTipText == null || toolTipText.isEmpty()) {
            getChildren().remove(toolTip);
        } else {
            getChildren().add(toolTip);
        }
    }

    protected abstract Control getControl();
    protected abstract void initControl();
    protected abstract void setOnAction(EventHandler<ActionEvent> eventHandler);
}
