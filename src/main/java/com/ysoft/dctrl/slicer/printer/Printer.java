package com.ysoft.dctrl.slicer.printer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ysoft.dctrl.slicer.param.SlicerParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kuhn on 4/18/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Printer {

    @JsonProperty("ID")
    public String id;

    @JsonProperty("PRINTER_FAMILY")
    public String printerFamily;

//    @JsonProperty("SLICER_PARAMS")
//    public Map<String, SlicerParam> paramsd;

    @JsonProperty("SLICER_PARAMS")
    public ArrayList<SlicerParam> params;

    public List<SlicerParam> getAllParams(){
        return params;
    }

}
