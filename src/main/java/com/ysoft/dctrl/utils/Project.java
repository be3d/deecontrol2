package com.ysoft.dctrl.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.ysoft.dctrl.math.TransformMatrix;

import javafx.geometry.Point3D;

/**
 * Created by pilar on 8.6.2017.
 */
public class Project {
    private static final String DEFAULT_PROJECT_NAME = "Untitled";

    private String name;
    private long printDuration;
    private Map<String, Long> materialUsage;

    private TransformMatrix printerTransformMatrix;

    public Project() {
        name = DEFAULT_PROJECT_NAME;
        materialUsage = new HashMap<>();
        printerTransformMatrix = new TransformMatrix();
        printerTransformMatrix.applyTranslate(new Point3D(-75,-75,0)).applyScale(new Point3D(-1,-1,1));
        System.err.println(printerTransformMatrix);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.isEmpty(name) ? DEFAULT_PROJECT_NAME : name;
    }

    public long getPrintDuration() { return printDuration;}

    public void setPrintDuration(long printDuration) {
        this.printDuration = printDuration;
    }

    public void addMaterial(String type, long length) {
        materialUsage.compute(type, (t, o) -> length + (o == null ? 0 : o));
    }

    public Map<String, Long> getMaterialUsage() { return materialUsage; }

    public TransformMatrix getPrinterTransformMatrix() {
        return printerTransformMatrix;
    }

    public void resetPrintInfo(){
        materialUsage.clear();
        printDuration = 0;
    }
}
