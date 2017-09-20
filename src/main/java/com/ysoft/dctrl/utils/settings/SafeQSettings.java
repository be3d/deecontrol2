package com.ysoft.dctrl.utils.settings;

import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by pilar on 15.5.2017.
 */
public class SafeQSettings {
    private String spoolerAddress;
    private String spoolerPort;

    @JsonIgnore private Consumer<SafeQSettings> onSaveHandler = null;

    public SafeQSettings() {
        spoolerAddress = null;
        spoolerPort = null;
    }

    public String getSpoolerAddress() {
        return spoolerAddress;
    }

    public SafeQSettings setSpoolerAddress(String spoolerAddress) {
        this.spoolerAddress = spoolerAddress;
        return this;
    }

    public String getSpoolerPort() {
        return spoolerPort;
    }

    public SafeQSettings setSpoolerPort(String spoolerPort) {
        this.spoolerPort = spoolerPort;
        return this;
    }

    @JsonIgnore
    public boolean isSet() {
        return isSet(spoolerAddress) && isSet(spoolerPort);
    }

    @JsonIgnore
    private boolean isSet(String s) {
        return s != null && !s.equals("");
    }

    public void save() {
        if(onSaveHandler != null) { onSaveHandler.accept(this); }
    }

    void onSave(Consumer<SafeQSettings> onSaveHandler) {
        this.onSaveHandler = onSaveHandler;
    }
}
