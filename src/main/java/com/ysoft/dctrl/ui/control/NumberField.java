package com.ysoft.dctrl.ui.control;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class NumberField extends TextField {
    private StringProperty unit;
    private DoubleProperty step;
    private IntegerProperty precision;

    public NumberField() {
        this("");
    }

    public NumberField(String text) {
        super(text);
        unit = new SimpleStringProperty("");
        step = new SimpleDoubleProperty(1.0);
        precision = new SimpleIntegerProperty(1);

        unit.addListener((ob, o, n) -> {
            setText((o == null) ? getText() + n : getText().replace(o, n));
        });

        addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            if(e.getCode() == KeyCode.UP) {
                setValue(getValue() + getStep());
                e.consume();
            } else if(e.getCode() == KeyCode.DOWN) {
                setValue(getValue() - getStep());
                e.consume();
            }
        });

        setTextFormatter(getNumberFormatter());
    }

    public String getUnit() {
        return unit.getValue();
    }

    public void setUnit(String unit) {
        this.unit.setValue(unit == null ? "" : unit);
    }

    public Double getStep() { return step.getValue(); }

    public void setStep(Double step) { this.step.setValue(step);}

    public double getValue() {
        String val = textProperty().getValue().replace(unit.getValue(), "");
        if(val.startsWith(".")) { val = "0" + val; }
        return Double.parseDouble(val);
    }

    public void setValue(double val) {
        setText(String.format("%." + precision.getValue() + "f", val).replace(",", ".") + unit.getValue());
    }

    private TextFormatter<String> getNumberFormatter() {
        return new TextFormatter<String>((change) -> {
            String u = unit.getValue();
            if(change.getControlNewText().matches("^-?\\d*\\.?\\d*" + (u == null ? "" : u))) {
                return change;
            }
            return null;
        });
    }
}