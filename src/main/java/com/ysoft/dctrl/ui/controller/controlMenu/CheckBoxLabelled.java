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


    public CheckBoxLabelled bindParamChanged(javafx.beans.value.ChangeListener listener){
        this.boundParam.getBooleanProperty().addListener(listener);
        return this;
    }
    public void bindControlChanged(javafx.beans.value.ChangeListener listener){
        checkBox.selectedProperty().addListener(listener);
    }

}
