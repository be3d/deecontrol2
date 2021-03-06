package com.ysoft.dctrl.ui.controller.controlMenu;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.geometry.Bounds;
import javafx.scene.control.ScrollPane;

import java.util.LinkedHashMap;

/**
 * Created by kuhn on 9/18/2017.
 */
public class ScrollBox extends ScrollPane {

    private boolean scrollable;
    private ScrollState currentScrollState;
    private enum ScrollState {NOT_SCROLLABLE, AT_TOP, AT_BOTTOM, AT_MIDDLE}

    private LinkedHashMap<ScrollState, PseudoClass> pseudoClasses = new LinkedHashMap<ScrollState, PseudoClass>(){{
        put(ScrollState.AT_TOP, PseudoClass.getPseudoClass("atTop"));
        put(ScrollState.AT_BOTTOM, PseudoClass.getPseudoClass("atBottom"));
        put(ScrollState.AT_MIDDLE, PseudoClass.getPseudoClass("atMiddle"));
    }};

    public ScrollBox() {
        scrollable = false;
        currentScrollState = null;

        vvalueProperty().addListener(
            (ObservableValue<? extends Number> obs, Number oldValue, Number newValue) -> {
                decideScrollState((double)newValue);
            });
        ChangeListener<Bounds> l = (ob, o, n) -> {
            scrollable = getViewportBounds().getHeight() < getContent().getLayoutBounds().getHeight();
            decideScrollState(getVvalue());
        };
        contentProperty().addListener((ob, o, n) -> {
            if(o != null) { o.layoutBoundsProperty().removeListener(l); }
            if(n != null) { n.layoutBoundsProperty().addListener(l); }
        });
    }

    private void decideScrollState(double scrollV) {
        if (scrollable) {
            if (scrollV == 0) {
                updateScrollState(ScrollState.AT_TOP);
            } else if (scrollV == this.getVmax()) {
                updateScrollState(ScrollState.AT_BOTTOM);
            } else {
                updateScrollState(ScrollState.AT_MIDDLE);
            }
        } else {
            updateScrollState(ScrollState.NOT_SCROLLABLE);
        }
    }

    private void updateScrollState(ScrollState scrollState){

        if(currentScrollState == scrollState){ return; }
        currentScrollState = scrollState;

        pseudoClasses.forEach((s,c) -> this.pseudoClassStateChanged(c, s==currentScrollState));
    }
}


