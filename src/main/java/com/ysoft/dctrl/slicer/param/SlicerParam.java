package com.ysoft.dctrl.slicer.param;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;

import com.ysoft.dctrl.slicer.AbstractConfigResource;
import javafx.beans.property.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by kuhn on 4/5/2017.
 *
 * Slicer parameter object.
 * Parsed by Jackson JSON
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SlicerParam implements Cloneable {
    @Autowired EventBus eventBus;
    protected final Logger logger = LogManager.getLogger(SlicerParam.class);

    private final String id;
    private String type;
    private Object value;
    private Double min;
    private Double max;
    private Object step;
    private Property valueProperty;
    private LinkedHashMap<String, String> options;
    private Object defaultValue;

    private enum ValueType {
        FLOAT("float"),
        STRING("string"),
        ENUM("enum"),
        INT("int"),
        BOOL("bool");

        private String v;
        ValueType(String s) { v = s; }
        public String get() {return v;}
    }

    @JsonCreator
    public SlicerParam(
            @JsonProperty("id") String id,
            @JsonProperty("type") String type,
            @JsonProperty("default") Object defaultValue,
            @JsonProperty("value") Object value,
            @JsonProperty("min") Double min,
            @JsonProperty("max") Double max,
            @JsonProperty("step") Double step,
            @JsonProperty("options") LinkedHashMap<String, String> options) throws IllegalArgumentException
    {
        this.id = id;
        this.type = type;
        this.min = min;
        this.max = max;
        this.step = step;
        this.options = options;
        this.defaultValue = defaultValue;
        this.value = (value == null) ? defaultValue : value;

        if(type != null){
            switch(ValueType.valueOf(type.toUpperCase())) {
                case FLOAT: {
                    if (this.value instanceof Integer)
                        this.value = new Double(((Integer) this.value).intValue());
                    if (this.value instanceof String)
                        this.value = new Double((String)this.value);

                    this.valueProperty = new SimpleDoubleProperty((Double) this.value);
                    break;
                }

                case STRING:
                    this.valueProperty = new SimpleStringProperty((String) this.value);
                    break;

                case ENUM:
                    this.valueProperty = new SimpleStringProperty((String) this.value);
                    break;

                case INT:
                    this.valueProperty = new SimpleIntegerProperty((int) this.value);
                    break;

                case BOOL:
                    this.valueProperty = new SimpleBooleanProperty((boolean) this.value);

            }
        }
    }

    public SlicerParam(SlicerParam original){
        this.id = original.id;
        this.value = original.value;
    }

    public void setValue(Object value){
        if(this.type.equals(ValueType.ENUM.v)){
            LinkedHashMap<String, String> options = getOptions();
            if(options == null){
                logger.warn("Could not set value: {} , no enum options defined", this.id);
                return;
            }
            for(Map.Entry<String,String> option : getOptions().entrySet()){
                if(option.getValue().equals(value)){
                    value = option.getKey();
                    break;
                }
            }
        }
        this.value = value;
        this.setValueProperty(value);
        System.out.println("Setting " + this.id + " to " + value.toString());
    }

    private void publishChanged(){
        this.eventBus.publish(new Event(EventType.SLICER_PARAM_CHANGED.name() ));
    }

    public void setLimits(Double min, Double max){
        this.min = min;
        this.max = max;
    }

    public Object getValue() {
        return (value != null) ? value : defaultValue;
    }

    public String getId() {
        return id;
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

    public SimpleDoubleProperty getDoubleProperty(){
        return (SimpleDoubleProperty) this.valueProperty;
    }

    public SimpleIntegerProperty getIntegerProperty(){
        return (SimpleIntegerProperty) this.valueProperty;
    }

    public SimpleStringProperty getStringProperty(){
        return (SimpleStringProperty) this.valueProperty;
    }

    public SimpleBooleanProperty getBooleanProperty(){
        return (SimpleBooleanProperty) this.valueProperty;
    }

    public void setValueProperty(Object value) {
        this.valueProperty.setValue(value);
    }

    public void resetToDefault(){
        if (this.defaultValue != null){
            this.setValue(this.defaultValue);
//            try{
//                this.setValueProperty((double)this.defaultValue);
//            }catch(ClassCastException e){
//                System.out.println("Value cannot be cast to double.");
//            }
        }
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
