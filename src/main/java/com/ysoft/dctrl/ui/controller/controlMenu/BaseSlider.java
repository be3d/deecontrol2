package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.slicer.param.SlicerParam;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;

/**
 * Created by kuhn on 5/19/2017.
 */
public class BaseSlider extends BaseCustomControl implements SlicerParamBindable{
    @FXML
    private Slider slider;

    protected String unit = "";
    protected double value;

    public BaseSlider(String fxmlResource) {
        super();
        super.init(fxmlResource);

    }

    public void setMin(Double value){ slider.setMin(value);}
    public Double getMin(){return slider.getMin();}

    public void setMax(Double value){ slider.setMax(value);}
    public Double getMax(){return slider.getMax();}


    public void setValue(Double value){
        this.value = value;
        updateView();
    }
    public Double getValue(){ return slider.getValue();}

//    private void setTextValue(Double value){
//        valueText.setText(new Double((Math.round(value * 10D) / 10D)).toString());
//    }

    public void setStep(Double value){ slider.setMajorTickUnit(value);}
    public Double getStep(){ return slider.getMajorTickUnit();}

    public void setUnit(String value){
        unit = value;
    }

    public String getUnit(){
        return unit;
    }

    /**
     * Initialize the controller from the Slicer param object
     *
     * @param param
     * @return
     */
    public BaseSlider load(SlicerParam param){

        // todo perform the type conversion directly in param object
        this.boundParam = param;
        try{
            this.setMax(new Double(param.getMax().toString()));
            this.setMin(new Double(param.getMin().toString()));
            this.setStep(new Double(param.getStep().toString()));
            this.setValue(new Double(param.getValue().toString()));
            this.updateView();

        }catch(Exception e){
            System.out.println("Error loading " + param.getId());
            e.printStackTrace();
        }
        return this;
    }

    /**
     * Default handle for slicerParam change with no further processing
     * @return
     */
    public BaseSlider bindParamChanged(){
        boundParam.getDoubleProperty().addListener(
                (observable, oldValue, newValue) -> this.setValue((Double)newValue)
        );
        return this;
    }

    /**
     * Custom handle for slicerParam change, e.g. when the control displays different value
     * @param listener
     * @return
     */
    public BaseSlider bindParamChanged(ChangeListener listener){
        boundParam.getDoubleProperty().addListener(listener);
        return this;
    }

    /**
     * Custom handle for UI Control change -> usually just throw an event
     * @param listener
     */
    public void bindControlChanged(ChangeListener listener){
        slider.valueProperty().addListener(listener);
    }

    public void updateView(){
        slider.setValue(value);
    };
}
