package com.ysoft.dctrl.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysoft.dctrl.utils.files.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private Project currentProject;
    private String version;

    @Autowired
    public DeeControlContext(SpringFXMLLoader loader, SettingsStore settingsStore) {
        this.loader = loader;
        this.settings = settingsStore.getSettings();
        this.objectMapper = new ObjectMapper();
        this.currentProject = new Project();
        String v = getClass().getPackage().getImplementationVersion();
        this.version = v != null ? v : "DEV";
    }

    public SpringFXMLLoader getFXMLLoader() {
        return loader;
    }

    public Settings getSettings() { return settings; }

    public Project getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(Project currentProject) {
        this.currentProject = currentProject;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public String getVersion() {
        return version;
    }
}
