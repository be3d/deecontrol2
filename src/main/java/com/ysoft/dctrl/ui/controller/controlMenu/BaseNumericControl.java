package com.ysoft.dctrl.ui.controller.controlMenu;

import java.text.DecimalFormat;

/**
 * Created by kuhn on 8/3/2017.
 */
public abstract class BaseNumericControl extends BaseCustomControl {

    protected double value = 0;
    protected int decimals = 0;
    protected DecimalFormat df;

    public BaseNumericControl(String fxmlResource){
        super.init(fxmlResource);
    }


}
