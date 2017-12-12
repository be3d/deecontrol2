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
    protected Label error;
    protected Label toolTip;
    protected Runnable onChange;

    public ComplexControl() {
        label = new Label();
        error = new Label();
        toolTip = new Label();
        onChange = () -> setErrorText(null);

        error.setManaged(false);
        error.setWrapText(true);
        toolTip.setManaged(false);
        toolTip.setWrapText(true);

        initControl();

        getStyleClass().addAll("complex-control");
        error.getStyleClass().addAll("error");
        toolTip.getStyleClass().addAll("tooltip");

        getChildren().addAll(label, getControl(), error, toolTip);
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
            toolTip.setManaged(false);
        } else {
            toolTip.setManaged(true);
        }
    }

    public String getErrorText() {
        return error.getText();
    }

    public void setErrorText(String errorText) {
        error.setText(errorText);
        if(errorText == null || errorText.isEmpty()) {
            error.setManaged(false);
            getStyleClass().remove("error");
        } else {
            error.setManaged(true);
            getStyleClass().add("error");
        }
    }

    protected abstract Control getControl();
    protected abstract void initControl();
}
