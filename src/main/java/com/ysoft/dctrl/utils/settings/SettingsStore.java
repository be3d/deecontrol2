package com.ysoft.dctrl.utils.settings;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by pilar on 30.3.2017.
 */

@Component
public class SettingsStore {
    private final String settingsFilePath;

    private final Settings settings;
    private final ObjectMapper objectMapper;

    public SettingsStore() {
        settingsFilePath = System.getProperty("user.home") + File.separator + ".dctrl" + File.separator + "settings.json";
        settings = new Settings();
        settings.onSave(this::saveSettings);
        objectMapper = new ObjectMapper();
    }

    public void saveSettings() {
        saveSettings(settings);
    }

    private void saveSettings(Settings settings) {
        try {
            objectMapper.writeValue(new File(settingsFilePath), settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public Settings getSettings(boolean reload) {
        if(reload) { loadSettings(); }
        return settings;
    }

    @PostConstruct
    private void initialize() {
        loadSettings();
    }

    private void loadSettings() {
        try {
            objectMapper.readerForUpdating(settings).readValue(new File(settingsFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
