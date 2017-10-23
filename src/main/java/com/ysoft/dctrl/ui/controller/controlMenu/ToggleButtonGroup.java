package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.slicer.param.SlicerParam;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;

/**
 * Created by kuhn on 8/2/2017.
 */
public class ToggleButtonGroup extends BaseTooltipControl implements SlicerParamBindable {
    @FXML ToggleGroup toggleGroup;
    @FXML HBox buttonsContainer;

    private LinkedHashMap<String, ToggleButton> valuesMap = new LinkedHashMap<>();
    private double value;
    private String unit;
    private int decimals;
    private DecimalFormat decimalFormat;

    public ToggleButtonGroup(){
        super.init("/view/controlMenu/toggle_button_group.fxml");

        toggleGroup.selectedToggleProperty().addListener((ov, oldVal, newVal) -> {
            // Prevent empty selection
            if(newVal == null){
                oldVal.setSelected(true);
            }
        });
    }

    @Override
    public void updateView() {
        valuesMap.get(decimalFormat.format(this.value)).setSelected(true);
    }

    @Override
    public SlicerParamBindable load(SlicerParam param) {

        this.boundParam = param;
        decimalFormat = super.getDecimalFormat(decimals);

        /**
         * The array of values is derived from max min and all steps in between
         */
        double min = (double)param.getMin();
        double max = (double)param.getMax();
        double step = (double)param.getStep();
        this.value = (double)param.getValue();

        double n = min;
        while (n <= max){
            ToggleButton button = new ToggleButton(super.getDecimalFormat(decimals).format(n) + " " + unit);
            button.setUserData(n);
            button.setToggleGroup(toggleGroup);
            valuesMap.put( decimalFormat.format(n), button);
            buttonsContainer.getChildren().add(button);
            button.setSelected( decimalFormat.format(value).equals(decimalFormat.format(n)));
            n = n + step;
        }

        // Apply rounded corners to first and last button
        ObservableList<Node> buttons = buttonsContainer.getChildren();
        buttons.get(0).getStyleClass().addAll("control-toggle-group-button-first");
        buttons.get(buttons.size()-1).getStyleClass().addAll("control-toggle-group-button-last");

        return this;
    }

    @Override
    public SlicerParamBindable bindParamChanged() {
        boundParam.getDoubleProperty().addListener(
            (observable, oldValue, newValue) -> this.setValue((Double)newValue)
        );
        return this;
    }

    @Override
    public SlicerParamBindable bindParamChanged(ChangeListener listener) {
        bindParamChanged();
        boundParam.getDoubleProperty().addListener(listener);
        return this;
    }

    @Override
    public void bindControlChanged(ChangeListener listener) {
        toggleGroup.selectedToggleProperty().addListener(listener);
    }

    public void setValue(double value){

        this.value = value;
        this.updateView();
    }

    public void setUnit(String value){
        unit = value;
    }
    public String getUnit(){
        return unit;
    }

    public void setDecimals(int value){ decimals = value; }
    public int getDecimals(){ return decimals; }

}
