package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.slicer.param.SlicerParam;
import javafx.beans.value.ChangeListener;

/**
 * Created by kuhn on 6/28/2017.
 */
public interface SlicerParamBindable<T extends SlicerParamBindable<T>> {

    void updateView();

    T load(SlicerParam param);
    T bindParamChanged();
    T bindParamChanged(ChangeListener listener);
    void bindControlChanged(ChangeListener listener);

}
