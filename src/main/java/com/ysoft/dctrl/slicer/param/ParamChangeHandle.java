package com.ysoft.dctrl.slicer.param;

import java.util.Map;

/**
 * Created by kuhn on 5/1/2017.
 *
 * Handle used by Slicer parameters relations map.
 */

public class ParamChangeHandle implements Runnable {
    protected final String paramName;
    protected final Map<String, SlicerParam> params;
    protected final SlicerParam param;

    public ParamChangeHandle(String paramName, Map<String, SlicerParam> params){
        this.paramName = paramName;
        this.params = params;
        this.param = params.get(paramName);
    }
    @Override
    public void run() {}
}
