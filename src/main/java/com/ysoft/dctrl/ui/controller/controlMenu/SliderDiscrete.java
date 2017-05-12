package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.slicer.param.SlicerParam;
import com.ysoft.dctrl.slicer.param.SlicerParamType;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.text.DecimalFormat;

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
        DecimalFormat df = new DecimalFormat("#.##");

        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (this.getStep() instanceof Double)
                valueLabel.setText(df.format(new Double((Math.round(newValue.doubleValue() / this.getStep()) * this.getStep()))));
            else
                valueLabel.setText((newValue).toString());
        });
    }

    public void setMin(Double value){ slider.setMin(value);}
    public Double getMin(){return slider.getMin();}

    public void setMax(Double value){ slider.setMax(value);}
    public Double getMax(){return slider.getMax();}

    public void setStep(Double value){ slider.setMajorTickUnit(value);}
    public Double getStep(){ return slider.getMajorTickUnit();}

    public void setValue(Double value){ slider.setValue(value);}
    public Double getValue(){ return slider.getValue();}


    public SliderDiscrete load(SlicerParam param){
        // todo perform the type conversion directly in param object
        this.boundParam = param;
        try{
            this.setMax(new Double(param.getMax().toString()));
            this.setMin(new Double(param.getMin().toString()));
            this.setStep(new Double(param.getStep().toString()));
            this.setValue(new Double(param.getValue().toString()));
        }catch(Exception e){
            System.out.println("Error loading " + param.id);
            e.printStackTrace();
        }
        return this;
    }

    public SliderDiscrete bindParamChanged(javafx.beans.value.ChangeListener listener){
        boundParam.valuePropertyProperty().addListener(listener);
        //slider.valueProperty().addListener(listener);
        return this;
    }

    public void bindControlChanged(javafx.beans.value.ChangeListener listener){
        slider.valueProperty().addListener(listener);
    }

}
