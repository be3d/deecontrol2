package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.slicer.param.SlicerParamType;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

/**
 * Created by kuhn on 5/5/2017.
 */
public class SliderDiscrete extends BaseCustomControl{

    @FXML
    Slider slider;

    @FXML
    Label valueLabel;

    public SliderDiscrete(){
        super.init("/view/controlMenu/slider_discrete.fxml");



        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            valueLabel.setText(newValue.toString());
        });
    }

    public void setMin(Double value){ slider.setMin(value);}
    public Double getMin(){return slider.getMin();}

    public void setMax(Double value){ slider.setMax(value);}
    public Double getMax(){return slider.getMax();}

    public void setStep(Double value){
        // todo add make tick invisible over certain threshold
        slider.setMajorTickUnit(value);
    }

    public Double getStep(){ return slider.getMajorTickUnit();}

    public void setValue(Double value){ slider.setValue(value);}
    public Double getValue(){ return slider.getValue();}

    @Override
    public void addChangeListener(javafx.beans.value.ChangeListener listener){
        slider.valueProperty().addListener(listener);
    }

}
