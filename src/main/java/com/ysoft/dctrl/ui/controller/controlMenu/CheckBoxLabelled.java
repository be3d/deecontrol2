package com.ysoft.dctrl.ui.controller.controlMenu;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Created by kuhn on 5/9/2017.
 */
public class CheckBoxLabelled extends BaseCustomControl {

    @FXML
    private CheckBox checkBox;

    public CheckBoxLabelled(){
        super.init("/view/controlMenu/checkbox.fxml");
    }

    public String getText() { return checkBox.textProperty().get();}
    public void setText(String value) { checkBox.textProperty().set(value);}

    @Override
    public void addChangeListener(javafx.beans.value.ChangeListener listener){
        checkBox.selectedProperty().addListener(listener);
    }

    @FXML
    protected void doSomething() {
        // this.setText(this.getBoundParamID());
        System.out.println("The button was clicked!");
    }
}
