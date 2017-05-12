package com.ysoft.dctrl.slicer.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ysoft.dctrl.slicer.param.SlicerParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by kuhn on 4/20/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {

    public String id;
    public String name;
    public String description;

    // Compatibility
    public String slicerID;
    public String printerID;
    public String printerGroup;

    //public Map<String, SlicerParam> params;
    public  ArrayList<SlicerParam > params;

    public Profile(String id, String name, String description,
                   String slicerID, String printerGroup, String printerID,
                   //Map<String, SlicerParam> params){
                   ArrayList<SlicerParam > params){
        this.id = "id123456";
        this.name = name;
        this.description = description;
        this.slicerID = slicerID;
        this.printerGroup = printerGroup;
        this.printerID = printerID;
        this.params = params;

    }

    public Profile(){
        this.id = "default";
    }

    @Override
    public String toString(){
        return name;
    }
}
