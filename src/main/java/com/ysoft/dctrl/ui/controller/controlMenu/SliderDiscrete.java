package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.slicer.param.SlicerParam;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;

/**
 * Created by kuhn on 5/5/2017.
 */
public class SliderDiscrete extends BaseSlider {

    @FXML    Label valueLabel;

    private static double DEFAULT_MARK_OFFSET = 7;
    private static double DEFAULT_MARK_TRACK_PADDING = 16;

    public SliderDiscrete(){
        super("/view/controlMenu/slider_discrete.fxml");

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (getStep() instanceof Double){
                value = Math.round(newValue.doubleValue() / getStep()) * getStep();
            }
            else{
                value = (double)newValue;
            }
            updateView();
        });

        slider.widthProperty().addListener((obs, o, n) -> updateView());
    }

    @Override
    public SliderDiscrete load(SlicerParam param){
        return (SliderDiscrete)super.load(param);
    }

    @Override
    public SliderDiscrete bindParamChanged(){
        return (SliderDiscrete)super.bindParamChanged();
    }

    @Override
    public SliderDiscrete bindParamChanged(ChangeListener handler){
        return (SliderDiscrete)super.bindParamChanged(handler);
    }

    @Override
    public void bindControlChanged(ChangeListener handler){
        super.bindControlChanged(handler);
    }

    @Override
    public void updateView(){
        valueLabel.setText(getDecimalFormat(decimals).format(value) + (unit != null ? " " + unit : ""));
        progress.setProgress((value - getMin()) / (getMax() - getMin()));
        slider.setValue(value);

        double defaultMarkOffset = DEFAULT_MARK_OFFSET
                + (slider.getWidth()-DEFAULT_MARK_TRACK_PADDING)*(profileDefault - getMin()) / (getMax() - getMin());

        defaultMarkBox.setPadding(new Insets(0,0,0,defaultMarkOffset));
    }
}
