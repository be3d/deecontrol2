package com.ysoft.dctrl.ui.controller.controlMenu;

import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

/**
 * Created by kuhn on 5/5/2017.
 */
public class SliderContinuous extends BaseCustomControl{
    @FXML
    Slider slider;

    @FXML
    TextField valueText;

    public SliderContinuous(){
        super.init("/view/controlMenu/slider_continuous.fxml");

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

    public void setMin(Double value){ slider.setMin(value);}
    public Double getMin(){return slider.getMin();}

    public void setMax(Double value){ slider.setMax(value);}
    public Double getMax(){return slider.getMax();}


    public void setValue(Double value){ slider.setValue(value);}
    public Double getValue(){ return slider.getValue();}

    private void setTextValue(Double value){
        valueText.setText(new Double((Math.round(value * 10D) / 10D)).toString());
    }

    @Override
    public void addChangeListener(javafx.beans.value.ChangeListener listener){
        slider.valueProperty().addListener(listener);
    }
}
