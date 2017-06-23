package com.ysoft.dctrl.ui.controller.controlMenu;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Created by kuhn on 6/23/2017.
 */
public class SubHeader extends BaseCustomControl {

    @FXML    Label label;
    public SubHeader(){
        super.init("/view/controlMenu/header_subsection.fxml");
    }


    public String getText() {
        return label.textProperty().get();
    }

    public void setText(String value) {
        label.textProperty().set(value);
    }

}
