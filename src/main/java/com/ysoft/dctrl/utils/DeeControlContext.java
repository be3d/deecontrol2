package com.ysoft.dctrl.utils;

import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ysoft.dctrl.utils.settings.SafeQSettings;
import com.ysoft.dctrl.utils.settings.SettingsStore;
import com.ysoft.dctrl.utils.settings.Settings;

/**
 * Created by pilar on 21.3.2017.
 */

@Service
public class DeeControlContext {
    private final SpringFXMLLoader loader;
    private final Settings settings;
    private final ObjectMapper objectMapper;

    @Autowired
    public DeeControlContext(SpringFXMLLoader loader, SettingsStore settingsStore) {
        this.loader = loader;
        this.settings = settingsStore.getSettings();
        this.objectMapper = new ObjectMapper();
    }

    public SpringFXMLLoader getFXMLLoader() {
        return loader;
    }

    public Settings getSettings() { return settings; }
}
