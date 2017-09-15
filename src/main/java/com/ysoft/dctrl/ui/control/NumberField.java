package com.ysoft.dctrl.ui.control;

import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
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

    private Consumer<Double> limit;

    private BooleanProperty invalid;

    private double value;

    public NumberField() {
        this("");
    }

    public NumberField(String text) {
        super(text);
        unit = new SimpleStringProperty("");
        step = new SimpleDoubleProperty(1.0);
        precision = new SimpleIntegerProperty(1);
        limit = (v) -> {};

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
            } else if(e.getCode() == KeyCode.ENTER) {
                validate();
            }
        });

        focusedProperty().addListener((ob, o, n) -> validate());
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
        return value;
    }

    public void setValue(double value) {
        this.value = value;
        setText(String.format("%." + precision.getValue() + "f", value).replace(",", ".") + unit.getValue());
    }

    private void validate() {
        String text = getText().trim().replaceAll("\\s\\s", " ");
        String u = unit.getValue();
        if(!text.matches("^-?\\d*\\.?\\d*" + ((u == null ? "" : u) + "$"))) {
            double newValue = Double.parseDouble(text);
            setValue(newValue);
            return;
        }

        setText(text);
        setValue(Double.parseDouble(text.replace(unit.getValue(), "")));
    }

    public void setLimit(Consumer<Double> limit) {
        this.limit = limit;
    }

    @Override
    public void nextWord() {
        super.endOfNextWord();
    }

    @Override
    public void selectNextWord() {
        super.selectEndOfNextWord();
    }
}