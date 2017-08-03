package com.ysoft.dctrl.slicer.cura;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pilar on 3.7.2017.
 */
public class PrinterProfile {
    private String id;
    private int version;
    private String name;
    private String inherits;

    private Map<String, Param> overrides;

    public PrinterProfile() {
        this.id = "edee";
        this.version = 1;
        this.name = "eDee";
        this.inherits = "fdmprinter";
        this.overrides = new HashMap<>();
    }

    public void addOverride(String name, Object value) {
        this.overrides.put(name, new Param(value));
    }

    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public String getInherits() {
        return inherits;
    }

    public Map<String, Param> getOverrides() {
        return overrides;
    }

    public class Param {
        private Object default_value;

        public Param(Object default_value) {
            this.default_value = default_value;
        }

        public Object getDefault_value() {
            return default_value;
        }
    }
}
