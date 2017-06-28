package com.ysoft.dctrl.ui.control.complex;

import com.ysoft.dctrl.ui.control.complex.contract.PickerItem;

import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;

/**
 * Created by pilar on 25.5.2017.
 */
@DefaultProperty("items")
public class Picker extends ComplexControl {
    private ComboBox<PickerItem> comboBox;

    @Override
    protected void initControl() {
        comboBox = new ComboBox<>();
    }

    public void setPlaceholderText(String placeholderText) {
        comboBox.setPromptText(placeholderText);
    }

    public String getPlaceholderText() {
        return comboBox.getPromptText();
    }

    public void setItems(ObservableList<PickerItem> items) {
        comboBox.setItems(items);
    }

    public ObservableList<PickerItem> getItems() {
        return comboBox.getItems();
    }

    public void setSelected(int index) {
        comboBox.getSelectionModel().select(index);
    }

    public void setSelected(PickerItem item) {
        comboBox.getSelectionModel().select(item);
    }

    public void setSelected(Object value) {
        comboBox.getItems().forEach(i -> {
            if(i.getValue().equals(value)) {
                setSelected(i);
            }
        });
    }

    public Object getValue() {
        return comboBox.getValue().getValue();
    }

    @Override
    protected Control getControl() {
        return comboBox;
    }

    public void setOnAction(EventHandler<ActionEvent> eventHandler) {
        comboBox.setOnAction(eventHandler);
    }
}
