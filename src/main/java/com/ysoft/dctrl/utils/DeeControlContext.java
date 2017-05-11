package com.ysoft.dctrl.utils;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ysoft.dctrl.utils.settings.SettingsStore;
import com.ysoft.dctrl.utils.settings.contract.Settings;

/**
 * Created by pilar on 21.3.2017.
 */

@Service
public class DeeControlContext {
    private final SpringFXMLLoader loader;
    private final Settings settings;

    @Autowired
    public DeeControlContext(SpringFXMLLoader loader, SettingsStore settingsStore) {
        this.loader = loader;
        this.settings = settingsStore.getSettings();
    }

    public SpringFXMLLoader getFXMLLoader() {
        return loader;
    }

    public Locale getStartUpLocale() {
        return settings.getStartUpLocale();
    }

    public String getLastOpenPwd() { return settings.getLastOpenPwd(); }
}
