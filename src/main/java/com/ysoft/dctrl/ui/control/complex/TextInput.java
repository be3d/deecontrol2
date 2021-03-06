package com.ysoft.dctrl.ui.control.complex;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

/**
 * Created by pilar on 25.5.2017.
 */
public class TextInput extends ComplexControl {
    private TextField textField;

    @Override
    protected void initControl() {
        textField = new TextField();
        textField.textProperty().addListener((ob, o, n) ->{
            onChange.run();
        });
    }

    @Override
    protected Control getControl() {
        return textField;
    }

    public void setPlaceholderText(String placeholderText) {
        textField.setPromptText(placeholderText);
    }

    public String getPlaceholderText() {
        return textField.getPromptText();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public String getText() {
        return textField.getText();
    }

    @Override
    public void requestFocus() {
        textField.requestFocus();
    }
}
