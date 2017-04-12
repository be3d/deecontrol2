package com.ysoft.dctrl.slicer;

import java.io.BufferedReader;
import java.io.File;
import java.util.Map;

/**
 * Created by kuhn on 4/4/2017.
 */
public interface Slicer {

    void run(Map<String,Object> slicerParams) throws Exception;

    void progressHandler(BufferedReader stdInput) throws Exception;

}
