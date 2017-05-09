package com.ysoft.dctrl.ui.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * Created by pilar on 21.4.2017.
 */
public class Tool extends Button {
    private static PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    BooleanProperty selected;

    public Tool(String text) {
        super(text);
        initaialize();
    }

    public Tool(String text, Node graphic) {
        super(text, graphic);
        initaialize();
    }

    public Tool() {
        super();
        initaialize();
    }

    private void initaialize() {
        selected = new SimpleBooleanProperty(false);
        selected.addListener(e -> pseudoClassStateChanged(SELECTED, selected.get()));
    }

    public boolean isSelected() {
        return selected.get();
    }


    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }
}
