package com.ysoft.dctrl.safeq.job;

/**
 * Created by pilar on 30.6.2017.
 */
public class Material {
    private String materialType;
    private long lengthInMm;

    public Material(String materialType, long lengthInMm) {
        this.materialType = materialType;
        this.lengthInMm = lengthInMm;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public long getLengthInMm() {
        return lengthInMm;
    }

    public void setLengthInMm(long lengthInMm) {
        this.lengthInMm = lengthInMm;
    }
}
