package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.slicer.param.SlicerParam;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

/**
 * Created by kuhn on 6/27/2017.
 */
public abstract class BaseCheckBox extends BaseCustomControl implements SlicerParamBindable<BaseCheckBox> {
    @FXML
    private CheckBox checkBox;

    private boolean value;

    public String getText() { return checkBox.textProperty().get();}
    public void setText(String value) { checkBox.textProperty().set(value);}
    public void setOnAction(EventHandler<ActionEvent> handler){ checkBox.setOnAction(handler);}
    public void setValue(boolean value){
        this.value = value;
    }

    @Override
    public BaseCheckBox load(SlicerParam param){
        this.boundParam = param;
        try{
            value = (boolean)param.getValue();
            this.updateView();

        }catch(Exception e){
            logger.warn("Error loading {}", param.getId(), e);
        }
        return this;
    }

    @Override
    public void updateView(){
        checkBox.setSelected(value);
    }

    @Override
    public BaseCheckBox bindParamChanged() {
        boundParam.getBooleanProperty().addListener(
                (observable, oldValue, newValue) -> this.setValue(newValue)
        );
        return this;
    }

    public BaseCheckBox bindParamChanged(javafx.beans.value.ChangeListener listener){
        this.boundParam.getBooleanProperty().addListener(listener);
        return this;
    }
    public void bindControlChanged(javafx.beans.value.ChangeListener listener){
        checkBox.selectedProperty().addListener(listener);
    }

}
