package com.ysoft.dctrl.slicer.param;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;

/**
 * Created by kuhn on 4/5/2017.
 *
 * Slicer parameter object.
 * Parsed by Jackson JSON
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SlicerParam {

    @Autowired EventBus eventBus;

    public String id;   // id of SlicerParamType
    private String type; // data type...
    private Object value;
    private Double min;
    private Double max;
    private Object step;

    private DoubleProperty valueProperty = new SimpleDoubleProperty();

    private LinkedHashMap<String, String> options; // only for type enum
    //private Object profile_default;

    private Object defaultValue;

    public SlicerParam(@JsonProperty("default") Object defaultValue){
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public void setVal(Object value){
        this.value = value;
        System.out.println("Setting " + this.id + " to " + value.toString());

//        if (this.type.equals("float"))
        try{
            this.setValueProperty((double) value);
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    private void publishChanged(){
        this.eventBus.publish(new Event(EventType.SLICER_PARAM_CHANGED.name() ));
    }

    public void setLimits(Double min, Double max){
        this.min = min;
        this.max = max;
    }

    public Object getValue() {
        if (value != null)
            return value;
        else
            return defaultValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

    public LinkedHashMap<String, String> getOptions() {
        return options;
    }

    public Object getStep() {
        return step;
    }

    public void setStep(Object step) {
        this.step = step;
    }


    public double getValueProperty() {
        return valueProperty.get();
    }

    public DoubleProperty valuePropertyProperty() {
        return valueProperty;
    }

    public void setValueProperty(double valueProperty) {
        this.valueProperty.set(valueProperty);
    }


    @Override
    public String toString() {
        // todo get from language
        return id;
    }


//   // public Object getProfile_default() {
//        return profile_default;
//    }
//
//    public void setProfile_default(Object profile_default) {
//        this.profile_default = profile_default;
//    }
}
