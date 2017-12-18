package com.ysoft.dctrl.ui.control;

import java.util.function.DoubleFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class NumberField extends TextField {
    private static PseudoClass INVALID_PSEUDO_CLASS = PseudoClass.getPseudoClass("invalid");

    private StringProperty unit;
    private DoubleProperty step;
    private IntegerProperty precision;

    private DoubleFunction<Boolean> validator;
    private EventHandler onChangedByMouseDragHandler;

    private Double value;
    private Double mouseDragSensitivity;
    private BooleanProperty invalid;
    private boolean associated;

    // Mouse drag along x axis increments the field value proportionally to NumberField.MouseDragSensitivity().
    // The Y position of the mouse while dragging applies a penalty to that increment. This effectively
    //  means, the further mouse is in Y from the drag start, the more delicate change is applied to the field value.
    private static double MOUSE_DRAG_Y_DEAD_AREA = 100; // area around 0 in y that is insensitive to this point
    private static double MOUSE_DRAG_Y_SENSITIVITY = 0.002; // defines rate of increasing increment penalty
    private static double MOUSE_DRAG_Y_MAX_INCREMENT_PENALTY = 0.05;

    private double mouseDragLastX;
    private double mouseDragInitialY;

    public NumberField() {
        this("");
    }

    public NumberField(String text) {
        super(text);
        unit = new SimpleStringProperty("");
        step = new SimpleDoubleProperty(1.0);
        precision = new SimpleIntegerProperty(1);
        associated = true;
        invalid = new SimpleBooleanProperty(false);
        invalid.addListener(e -> pseudoClassStateChanged(INVALID_PSEUDO_CLASS, invalid.get()));
        validator = (v) -> true;
        mouseDragSensitivity = 1.0;
        mouseDragInitialY = 0;
        mouseDragLastX = 0;
        onChangedByMouseDragHandler = event -> {};


        unit.addListener((ob, o, n) -> setText((o == null) ? getText() + n : getText().replace(o, n)));

        addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
            if(e.getCode() == KeyCode.UP) {
                if(isAssociated()) { setValue(getValue() + getStep()); }
                e.consume();
            } else if(e.getCode() == KeyCode.DOWN) {
                if(isAssociated()) { setValue(getValue() - getStep()); }
                e.consume();
            } else if(e.getCode() == KeyCode.ENTER) {
                validate();
                if(isInvalid()) { e.consume(); }
            }
        });

        textProperty().addListener((ob, o, n) -> {
            if(isInvalid()) { setInvalid(false); }
        });
        focusedProperty().addListener((ob, o, n) -> {
            if(!n) { validate(); }
        });

        setOnMousePressed(e -> {
            mouseDragLastX = e.getSceneX();
            mouseDragInitialY = e.getSceneY();
        });

        setOnMouseDragged((e) -> {
            double dx = e.getSceneX() - mouseDragLastX;
            mouseDragLastX = e.getSceneX();

            double ym = e.getScreenY() - mouseDragInitialY;
            double y0 = MOUSE_DRAG_Y_DEAD_AREA;

            double sx = mouseDragSensitivity;
            double sy = MOUSE_DRAG_Y_SENSITIVITY;

            double v0 = getValue();
            double v1;

            if (Math.abs(ym) > y0) {
                v1 = v0 + sx * dx * Math.max(1 - sy * Math.abs(ym - y0), MOUSE_DRAG_Y_MAX_INCREMENT_PENALTY);
            } else {
                v1 = v0 + sx * dx;
            }

            setValue(v1);
            onChangedByMouseDragHandler.handle(e);
            e.consume();
        });
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
        return value == null ? Double.NaN : value;
    }

    public void setValue(double value) {
        setValue(Double.valueOf(value));
    }

    public void setValue(Double value) {
        if(value == null) {
            setText("");
            return;
        }
        if(!validator.apply(value)) {
            setInvalid(true);
            return;
        }
        setInvalid(false);
        this.value = value;
        setText(String.format("%." + precision.getValue() + "f", value).replace(",", ".") + unit.getValue());
    }

    private static final Matcher DOUBLE_MATCHER = Pattern.compile("(^-?\\d+\\.?\\d*)").matcher("");
    private void validate() {
        if(!isAssociated()) { return; }
        String text = getText().trim().replaceAll("\\s\\s", " ");
        String u = unit.getValue();
        if(text.isEmpty()) {
            setValue(getValue());
            return;
        } else if(!text.matches("^-?\\d*\\.?\\d*" + ((u == null ? "" : u) + "$"))) {
            DOUBLE_MATCHER.reset(text);
            if(DOUBLE_MATCHER.find()) {
                text = DOUBLE_MATCHER.group(0);
            } else {
                setInvalid(true);
                return;
            }
            double newValue = Double.parseDouble(text);
            setValue(newValue);
            return;
        }

        setValue(Double.parseDouble(text.replace(unit.getValue(), "")));
    }

    public void setValidator(DoubleFunction<Boolean> validator) {
        this.validator = validator;
    }

    public void setInvalid(boolean invalid) {
        this.invalid.set(invalid);
    }

    public boolean isInvalid() {
        return invalid.get();
    }

    public boolean isAssociated() { return associated; }

    public void setAssociated(boolean associated) {
        this.associated = associated;
        setValue(associated ? 0d : null);
    }

    public Double getMouseDragSensitivity() { return mouseDragSensitivity; }

    public void setMouseDragSensitivity(Double mouseDragSensitivity) {
        this.mouseDragSensitivity = mouseDragSensitivity;
    }

    public void setOnChangedByMouseDrag(EventHandler<MouseEvent> h) {
        onChangedByMouseDragHandler = h;
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