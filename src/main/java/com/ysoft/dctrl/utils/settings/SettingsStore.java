package com.ysoft.dctrl.utils.settings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.ysoft.dctrl.utils.settings.contract.Settings;

/**
 * Created by pilar on 30.3.2017.
 */

@Component
public class SettingsStore {
    private static final String LOCALE = "locale";
    private static final String LAST_OPEN_PWD = "lastOpenPwd";

    private final String settingsFilePath;

    private final Settings settings;

    public SettingsStore() {
        settingsFilePath = System.getProperty("user.home") + File.separator + ".dctrl" + File.separator + "settings.properties";
        settings = new Settings();
        settings.onChange(this::saveSettings);
    }

    public void saveSettings() {
        saveSettings(settings);
    }

    private void saveSettings(Settings settings) {
        Properties properties = new Properties();
        properties.put(LOCALE, getLocaleString(settings.getStartUpLocale()));
        properties.put(LAST_OPEN_PWD, settings.getLastOpenPwd());

        try {
            properties.store(new FileWriter(settingsFilePath), "DeeControl settings");
        } catch (IOException e) {
            System.err.println("settings not saved :(");
        }
    }

    public Settings getSettings() {
        return settings;
    }

    public Settings getSettings(boolean reload) {
        loadSettings();
        return settings;
    }

    @PostConstruct
    private void initialize() {
        settings.onChange(this::saveSettings);
    }

    private void loadSettings() {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(settingsFilePath));

            settings.setStartUpLocale(parseLocale(properties.getProperty(LOCALE)));
            settings.setLastOpenPwd(properties.getProperty(LAST_OPEN_PWD));
        } catch (IOException e) {
            System.err.println("hups :D");
        }
    }

    private String getLocaleString(Locale locale) {
        return locale.getLanguage() + "_" + locale.getCountry();
    }

    private Locale parseLocale(String localeString) {
        String[] s = localeString.split("_");
        return new Locale(s[0], s[1]);
    }

}
