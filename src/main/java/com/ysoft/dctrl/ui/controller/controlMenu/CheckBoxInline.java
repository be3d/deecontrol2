package com.ysoft.dctrl.ui.controller.controlMenu;

import com.fasterxml.jackson.databind.deser.Deserializers;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

/**
 * Created by kuhn on 5/9/2017.
 */
public class CheckBoxInline extends BaseCheckBox{

    public CheckBoxInline(){
        super.init("/view/controlMenu/checkbox_inline.fxml");
    }

}
