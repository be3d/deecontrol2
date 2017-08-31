package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.slicer.param.SlicerParam;
import com.ysoft.dctrl.utils.DeeControlContext;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by kuhn on 5/5/2017.
 */
@Controller
public class Picker extends BaseTooltipControl {

    @FXML
    private Label label;
    @FXML
    private ComboBox comboBox;

    private HashMap<String,String> items;

    public Picker(){
        super.init("/view/controlMenu/picker.fxml");
    }

    public String getLabel() {
        return label.textProperty().get();
    }

    public void setLabel(String value) {
        label.textProperty().set(value);
    }

    public void addItem(Object item){comboBox.getItems().add(item);}
    public void addItems(ObservableList list) {getItems().addAll(list);}
    public void setItems(ObservableList list) {comboBox.setItems(list);}
    public ObservableList getItems() {return comboBox.getItems();}
    public void addSeparator(){getItems().add(new Separator());}

    public void selectItem(Object item){
        comboBox.getSelectionModel().select(item);
    }

    public Picker bindParamChanged(){
        boundParam.getStringProperty().addListener(
                (observable, oldValue, newValue) -> this.selectItem(newValue)
        );
        return this;
    }

    public Picker bindParamChanged(javafx.beans.value.ChangeListener listener){
        boundParam.getStringProperty().addListener(listener);
        return this;
    }

    public void bindControlChanged(javafx.beans.value.ChangeListener listener){
        comboBox.getSelectionModel().selectedItemProperty().addListener(listener);
    }

    public Picker load(SlicerParam param){
        this.boundParam = param;
        if (param != null){
            items = param.getOptions();
            setItems(FXCollections.observableList(new ArrayList<>(items.values())));
            selectItem(items.get(param.getDefaultValue()));
        }
        return this;
    }

}




