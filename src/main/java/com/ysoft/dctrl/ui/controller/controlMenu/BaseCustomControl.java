package com.ysoft.dctrl.ui.controller.controlMenu;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Collections;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.slicer.param.SlicerParam;
import com.ysoft.dctrl.utils.DeeControlContext;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Sample custom control hosting a text field and a button.
 */
@Controller
public abstract class BaseCustomControl extends VBox {
    protected final Logger logger = LogManager.getLogger(BaseCustomControl.class);

    @FXML
    Label label;

    protected SlicerParam boundParam;
    protected String boundParamID;

    public BaseCustomControl(){}

    public void init(String fxmlResource){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlResource));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String getBoundParamID(){
        return boundParamID;
    }
    public void setBoundParamID(String boundParamID) {
        this.boundParamID = boundParamID;
    }


    public String getLabel() {
        if (label != null){
            return label.textProperty().get();
        } else {
            return null;
        }
    }
    public void setLabel(String value) {
        if (label != null){
            label.textProperty().set(value);
        }
    }

    protected DecimalFormat getDecimalFormat(int decimals){
        DecimalFormat df;
        if (decimals > 0){
            df = new DecimalFormat("#."+String.join("", Collections.nCopies(decimals, "#")));
        }
        else {
            df = new DecimalFormat("#");
        }
        return df;
    }
}
