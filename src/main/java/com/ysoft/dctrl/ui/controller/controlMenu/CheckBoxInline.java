package com.ysoft.dctrl.ui.controller.controlMenu;

import com.fasterxml.jackson.databind.deser.Deserializers;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

/**
 * Created by kuhn on 5/9/2017.
 */
public class CheckBoxInline extends BaseCustomControl{
    @FXML
    private CheckBox checkBox;

    public CheckBoxInline(){
        super.init("/view/controlMenu/checkbox_inline.fxml");
    }

    public String getText() { return checkBox.textProperty().get();}
    public void setText(String value) { checkBox.textProperty().set(value);}


    public CheckBoxInline bindParamChanged(javafx.beans.value.ChangeListener listener){
        this.boundParam.getBooleanProperty().addListener(listener);
        return this;
    }
    public void bindControlChanged(javafx.beans.value.ChangeListener listener){
        checkBox.selectedProperty().addListener(listener);
    }

}
