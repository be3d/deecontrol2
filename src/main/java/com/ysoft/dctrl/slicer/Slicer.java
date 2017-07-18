package com.ysoft.dctrl.slicer;

import com.ysoft.dctrl.slicer.param.SlicerParam;
import javafx.scene.control.ProgressBar;

import java.util.Map;

/**
 * Created by kuhn on 4/4/2017.
 */
public interface Slicer {

    void run(Map<String, SlicerParam> slicerParams, String modelSTL) throws Exception;

    Map<String,SlicerParam> filterSupportedParams(Map<String, SlicerParam> allParams);

    boolean supportsParam(String paramName);

    double getProgress();

}
