package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.slicer.param.SlicerParam;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

/**
 * Created by kuhn on 5/5/2017.
 */
public class SliderContinuous extends com.ysoft.dctrl.ui.controller.controlMenu.BaseSlider {
    @FXML
    Slider slider;

    @FXML
    TextField valueText;

    public SliderContinuous(){
        super("/view/controlMenu/slider_continuous.fxml");

        PseudoClass errorClass = PseudoClass.getPseudoClass("error");
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            setTextValue((Double)newValue);
            valueText.pseudoClassStateChanged(errorClass, false);
        });

        valueText.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // perform check when loosing focus
            if( newValue == false){
                try{
                    Double newSliderValue = Double.valueOf(valueText.getText());
                    setValue(newSliderValue);
                    valueText.pseudoClassStateChanged(errorClass, false);

                }catch(NumberFormatException e ){
                    valueText.pseudoClassStateChanged(errorClass, true);
                }
            }
        });
    }

    private void setTextValue(Double value){
        valueText.setText(new Double((Math.round(value * 10D) / 10D)).toString());
    }

    public SliderContinuous load(SlicerParam param) {
        return (SliderContinuous) super.load(param);
    }

    public SliderContinuous bindParamChanged(){
        return (SliderContinuous)super.bindParamChanged();
    }

    public SliderContinuous bindParamChanged(javafx.beans.value.ChangeListener listener){
        return (SliderContinuous)super.bindParamChanged(listener);
    }

    public void bindControlChanged(javafx.beans.value.ChangeListener listener){
        super.bindControlChanged(listener);
    }
}
