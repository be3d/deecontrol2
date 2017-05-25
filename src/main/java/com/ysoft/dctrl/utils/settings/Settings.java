package com.ysoft.dctrl.utils.settings;

import java.util.Locale;
import java.util.function.Consumer;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by pilar on 30.3.2017.
 */
public class Settings {
    private Locale startUpLocale;
    private String lastOpenPwd;
    private SafeQSettings safeQSettings;

    @JsonIgnore private Consumer<Settings> onSaveHandler;

    protected Settings() {
        onSaveHandler = null;
        startUpLocale = Locale.US;
        safeQSettings = new SafeQSettings();
    }

    public Locale getStartUpLocale() {
        return startUpLocale;
    }

    public Settings setStartUpLocale(Locale startUpLocale) {
        this.startUpLocale = startUpLocale;
        return this;
    }

    public String getLastOpenPwd() {
        return lastOpenPwd;
    }

    public Settings setLastOpenPwd(String lastOpenPwd) {
        this.lastOpenPwd = lastOpenPwd;
        return this;
    }

    public SafeQSettings getSafeQSettings() {
        return safeQSettings;
    }

    public Settings setSafeQSettings(SafeQSettings safeQSettings) {
        this.safeQSettings = safeQSettings;
        safeQSettings.onSave((s) -> save());
        return this;
    }

    public void save() {
        if(onSaveHandler == null) { return; }
        onSaveHandler.accept(this);
    }

    void onSave(Consumer<Settings> onSaveHandler) {
        this.onSaveHandler = onSaveHandler;
    }
}
