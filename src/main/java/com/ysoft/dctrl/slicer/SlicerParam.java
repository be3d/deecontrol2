package com.ysoft.dctrl.slicer;

/**
 * Created by kuhn on 4/5/2017.
 */
public class SlicerParam {
    public final String id;
    public final Object data;

    private final String label = "";
    private final String type = "";
    private final String value = null;
    private final String min_value = null;
    private final String max_value = null;

    public SlicerParam(String id, Object data){
        this.id = id;
        this.data = data;
    }
    public SlicerParam(){
        this.id = null;
        this.data = null;
    }

}
