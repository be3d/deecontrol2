package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.slicer.param.SlicerParam;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;

/**
 * Created by kuhn on 5/19/2017.
 */
public class BaseSlider extends BaseTooltipControl implements SlicerParamBindable {

    @FXML   protected Slider slider;
    @FXML   protected ProgressBar progress;
    @FXML   protected VBox defaultMarkBox;

    protected String unit;
    protected double value;
    protected double profileDefault;
    protected int decimals;

    public BaseSlider(String fxmlResource) {
        init(fxmlResource);

        unit = "";
        decimals = 2;
        value = 0;
        profileDefault = 0;
    }

    public BaseSlider load(SlicerParam param){
        boundParam = param;
        try{
            setMax(new Double(param.getMax().toString()));
            setMin(new Double(param.getMin().toString()));
            setStep(new Double(param.getStep().toString()));
            setValue(new Double(param.getValue().toString()));
            setProfileDefault(new Double(param.getProfileDefault().toString()));

            updateView();

        } catch(Exception e){
            logger.warn("Error loading {}", param.getId(), e);
        }
        return this;
    }

    public BaseSlider bindParamChanged(){
        boundParam.getDoubleProperty().addListener(
                (obs, o, n) -> setValue((Double)n)
        );
        boundParam.getProfileDefaultProperty().addListener(
                (obs, o, n) -> setProfileDefault((Double)n)
        );
        return this;
    }

    public BaseSlider bindParamChanged(ChangeListener listener){
        bindParamChanged();
        boundParam.getDoubleProperty().addListener(listener);
        return this;
    }

    public void bindControlChanged(ChangeListener listener){
        slider.valueProperty().addListener(listener);
    }

    public void updateView(){
        slider.setValue(value);
    }

    public void setMin(Double value){
        slider.setMin(value);
    }
    public Double getMin(){return slider.getMin();}

    public void setMax(Double value){ slider.setMax(value);}
    public Double getMax(){return slider.getMax();}

    public void setValue(Double value){
        this.value = value;
        updateView();
    }
    public Double getValue(){ return slider.getValue();}

    public void setStep(Double value){ slider.setMajorTickUnit(value);}
    public Double getStep(){ return slider.getMajorTickUnit();}

    public void setUnit(String value){
        unit = value;
    }
    public String getUnit(){
        return unit;
    }

    public void setDecimals(int value){ decimals = value; }
    public int getDecimals(){ return decimals; }

    public double getProfileDefault() { return this.profileDefault; }

    public void setProfileDefault(double profileDefault) {
        this.profileDefault = profileDefault;
        updateView();
    }
}
