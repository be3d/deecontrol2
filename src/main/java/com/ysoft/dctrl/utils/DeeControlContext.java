package com.ysoft.dctrl.utils;

import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysoft.dctrl.utils.files.FileService;
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
    private final ObjectMapper objectMapper;
    private final FileService fileService;

    @Autowired
    public DeeControlContext(SpringFXMLLoader loader, SettingsStore settingsStore, FileService fileService) {
        this.loader = loader;
        this.settings = settingsStore.getSettings();
        this.objectMapper = new ObjectMapper();
        this.fileService = fileService;

    }

    public SpringFXMLLoader getFXMLLoader() {
        return loader;
    }

    public Locale getStartUpLocale() {
        return settings.getStartUpLocale();
    }

    public String getLastOpenPwd() { return settings.getLastOpenPwd(); }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public FileService getFileService() {
        return fileService;
    }
}
