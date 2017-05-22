package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.slicer.param.SlicerParam;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import java.text.DecimalFormat;

/**
 * Created by kuhn on 5/5/2017.
 */
public class SliderDiscrete extends com.ysoft.dctrl.ui.controller.controlMenu.Slider{

    @FXML
    Slider slider;

    @FXML
    Label valueLabel;

    public SliderDiscrete(){
        super("/view/controlMenu/slider_discrete.fxml");
//        super.init("/view/controlMenu/slider_discrete.fxml");"/view/controlMenu/slider_discrete.fxml"

        DecimalFormat df = new DecimalFormat("#.##");
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (this.getStep() instanceof Double)
                valueLabel.setText(df.format(new Double((Math.round(newValue.doubleValue() / this.getStep()) * this.getStep()))));
            else
                valueLabel.setText((newValue).toString());
        });
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

}
