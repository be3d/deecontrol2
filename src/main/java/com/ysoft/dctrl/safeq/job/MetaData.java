package com.ysoft.dctrl.safeq.job;

import java.util.ArrayList;

/**
 * Created by pilar on 30.6.2017.
 */
public class MetaData {
    private String printerType;
    private long printDurationInMins;
    private ArrayList<Material> material;

    public String getPrinterType() {
        return printerType;
    }

    public void setPrinterType(String printerType) {
        this.printerType = printerType;
    }

    public long getPrintDurationInMins() {
        return printDurationInMins;
    }

    public void setPrintDurationInMins(long printDurationInMins) {
        this.printDurationInMins = printDurationInMins;
    }

    public ArrayList<Material> getMaterial() {
        return material;
    }

    public void setMaterial(ArrayList<Material> material) {
        this.material = material;
    }
}
