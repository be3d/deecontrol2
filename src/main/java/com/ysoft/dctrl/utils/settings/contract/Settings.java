package com.ysoft.dctrl.utils.settings.contract;

import java.util.Locale;
import java.util.function.Consumer;

/**
 * Created by pilar on 30.3.2017.
 */
public class Settings {
    private Locale startUpLocale;
    private Consumer<Settings> onChangeHandler;
    private String lastOpenPwd;

    public Settings() {
        onChangeHandler = null;
        startUpLocale = Locale.US;
    }

    public Locale getStartUpLocale() {
        return startUpLocale;
    }

    public void setStartUpLocale(Locale startUpLocale) {
        this.startUpLocale = startUpLocale;
        handleChange();
    }

    public String getLastOpenPwd() {
        return lastOpenPwd;
    }

    public void setLastOpenPwd(String lastOpenPwd) {
        this.lastOpenPwd = lastOpenPwd;
        handleChange();
    }

    private void handleChange() {
        if(onChangeHandler == null) { return; }
        onChangeHandler.accept(this);
    }

    public void onChange(Consumer<Settings> onChangeHandler) {
        this.onChangeHandler = onChangeHandler;
    }
}
