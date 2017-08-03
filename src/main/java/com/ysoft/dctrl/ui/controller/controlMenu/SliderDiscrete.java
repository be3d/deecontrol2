package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.slicer.param.SlicerParam;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;

import java.text.DecimalFormat;
import java.util.Collections;

/**
 * Created by kuhn on 5/5/2017.
 */
public class SliderDiscrete extends BaseSlider {

    @FXML    Slider slider;
    @FXML    Label valueLabel;
    @FXML    ProgressBar progress;

    public SliderDiscrete(){
        super("/view/controlMenu/slider_discrete.fxml");
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (this.getStep() instanceof Double){
                value = new Double((Math.round(newValue.doubleValue() / this.getStep()) * this.getStep()));
            }
            else{
                value = (double)newValue;
            }
            updateView();
        });
        updateView();
    }

    public SliderDiscrete load(SlicerParam param){
        return (SliderDiscrete)super.load(param);
    }

    public SliderDiscrete bindParamChanged(){
        return (SliderDiscrete)super.bindParamChanged();
    }

    public SliderDiscrete bindParamChanged(javafx.beans.value.ChangeListener handler){
        return (SliderDiscrete)super.bindParamChanged(handler);
    }

    public void bindControlChanged(javafx.beans.value.ChangeListener handler){
        super.bindControlChanged(handler);
    }

    public void updateView(){
        valueLabel.setText(super.getDecimalFormat(decimals).format(this.value) + (unit != null ? " " + unit : ""));
        progress.setProgress((this.value - this.getMin()) / (this.getMax() - this.getMin()));
        slider.setValue(this.value);
    }
}
