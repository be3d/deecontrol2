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

    private String id;
    private String name;
    private String description;

    // Compatibility
    private String slicerID;
    private String printerID;
    private String printerGroup;

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

    /**
     * Creates default profile with no parameters (the params go from the printer.def)
     */
    public Profile(){
        this.setId("default");
        this.setName("Default");
        this.setDescription("Default printer profile");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSlicerID() {
        return slicerID;
    }

    public void setSlicerID(String slicerID) {
        this.slicerID = slicerID;
    }

    public String getPrinterID() {
        return printerID;
    }

    public void setPrinterID(String printerID) {
        this.printerID = printerID;
    }

    public String getPrinterGroup() {
        return printerGroup;
    }

    public void setPrinterGroup(String printerGroup) {
        this.printerGroup = printerGroup;
    }

    @Override
    public String toString(){
        return name;
    }
}
