package com.ysoft.dctrl.slicer;

import com.ysoft.dctrl.slicer.param.SlicerParam;

import java.io.IOException;
import java.util.Map;

/**
 * Created by kuhn on 11/22/2017.
 */
public abstract class AbstractSlicer implements Slicer {

    protected boolean cancelled;

    public AbstractSlicer() {
        cancelled = false;
    }

    @Override
    public void cancel() { cancelled = true; }

    public boolean isCancelled(){ return cancelled; }

    @Override
    public void reset() {
        cancelled = false;
    }
}
