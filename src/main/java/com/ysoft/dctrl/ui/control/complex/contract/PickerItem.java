package com.ysoft.dctrl.ui.control.complex.contract;

/**
 * Created by pilar on 25.5.2017.
 */
public class PickerItem {
    private String label;
    private Object value;

    public PickerItem() {
        this(null, null);
    }

    public PickerItem(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
