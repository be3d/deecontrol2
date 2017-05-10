package com.ysoft.dctrl.slicer.param;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
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
    public String type; // data type...
    public Object value;
    public Object min;
    public Object max;
    public LinkedHashMap<String, String> options; // only for type enum

    //private Object profile_default;

    public Object defaultValue;
    public SlicerParam(@JsonProperty("default") Object defaultValue){
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public void setVal(Object value){
        this.value = value;
        System.out.println("Setting " + this.id + " to " + value.toString());
    }

    private void publishChanged(){
        this.eventBus.publish(new Event(EventType.SLICER_PARAM_CHANGED.name() ));
    }

    public void setLimits(Object min, Object max){
        this.min = min;
        this.max = max;
    }

    public Object getValue() {
        return value;
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

    public void setMin(Object min) {
        this.min = min;
    }

    public Object getMax() {
        return max;
    }

    public void setMax(Object max) {
        this.max = max;
    }

    public LinkedHashMap<String, String> getOptions() {
        return options;
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
