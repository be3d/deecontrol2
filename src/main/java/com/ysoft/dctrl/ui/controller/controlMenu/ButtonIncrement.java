package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.event.EventHandler;
import com.ysoft.dctrl.slicer.param.SlicerParam;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.function.DoubleFunction;

/**
 * Created by kuhn on 6/26/2017.
 */
public class ButtonIncrement extends BaseCustomControl implements SlicerParamBindable<ButtonIncrement> {

    @FXML Label label;
    @FXML Button minusBtn;
    @FXML Button plusBtn;
    @FXML Label textValue;

    private double increment;
    private String unit;
    private double value;
    private double max;
    private double min;
    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    private DoubleFunction<Double> recalculation;

    public ButtonIncrement(){
        super.init("/view/controlMenu/increment.fxml");

        minusBtn.setOnAction((e) -> {
            double newValue = this.getValue() - increment;
            if (newValue >= min && newValue <= max){
                setValue(newValue);
            }
        } );
        plusBtn.setOnAction((e) -> {
            double newValue = this.getValue() + increment;
            if (newValue >= min && newValue <= max){
                setValue(newValue);
            }
        });

    }

    public void setIncrement(double value){
        increment = value;
    }

    public double getIncrement(){
        return increment;
    }

    public void setLabel(String text){
        label.setText(text);
    }

    public String getLabel(){
        return label.getText();
    }

    public void setUnit(String unit){
        this.unit = unit;
    }

    public String getUnit(){
        return unit;
    }

    public void setMax(double value){
        this.max = value;
    }

    public double getMax(){
        return max;
    }

    public void setMin(double value){
        this.min = value;
    }

    public double getMin(){
        return min;
    }

    private void setValue(Double value){
        this.value = value;
        //this.boundParam.setVal(value);
        updateParam();
        updateView();
    }


    private double getValue(){
        return value;
    }

    public void updateView(){
        if (recalculation != null){
            Double showValue = recalculation.apply(this.value);
            textValue.setText(decimalFormat.format(showValue) + " " + unit);
//            textValue.setText(new Double((Math.round(value * 10D) / 10D)).toString() + unit);
        } else {
            textValue.setText(decimalFormat.format(this.value) + (unit != null ? " " + unit : ""));
        }
    }

    /**
     * If the shown value is different than the actual value of slicer parameter,
     * bind the recalculation here.
     *
     * @param f
     */
    public SlicerParamBindable bindRecalculation(DoubleFunction f){
        recalculation = f;
        return this;
    }

    @Override
    public ButtonIncrement load(SlicerParam param) {
        boundParam = param;
        try{
            value = (Double)param.getValue();
            updateView();
        } catch(Exception e){
            System.out.println("Error loading " + param.getId());
            e.printStackTrace();
        }
        return this;
    }

    @Override
    public ButtonIncrement bindParamChanged() {
        boundParam.getDoubleProperty().addListener(
                (observable, oldValue, newValue) -> this.setValue((Double)newValue)
        );
        return this;
    }

    @Override
    public ButtonIncrement bindParamChanged(ChangeListener listener) {
        return this;
    }

    @Override
    public void bindControlChanged(ChangeListener listener) {
        minusBtn.onActionProperty().addListener(listener);
        plusBtn.onActionProperty().addListener(listener);
        System.out.println("out");
    }

    public void bindOnAction(javafx.event.EventHandler<javafx.event.ActionEvent> handler){
        minusBtn.addEventHandler(javafx.event.ActionEvent.ACTION, handler);
        plusBtn.addEventHandler(javafx.event.ActionEvent.ACTION, handler);
    }

    public void updateParam(){
        boundParam.setVal(getValue());
    }
}
