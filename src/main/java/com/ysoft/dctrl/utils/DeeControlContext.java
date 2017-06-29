package com.ysoft.dctrl.utils;

import java.io.File;
import java.util.Locale;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysoft.dctrl.utils.files.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ysoft.dctrl.Project;
import com.ysoft.dctrl.utils.settings.SafeQSettings;
import com.ysoft.dctrl.utils.settings.SettingsStore;
import com.ysoft.dctrl.utils.settings.Settings;

/**
 * Created by pilar on 21.3.2017.
 */

@Service
public class DeeControlContext {
    private final String slicerTempFolder = System.getProperty("user.home") + File.separator + ".dctrl" + File.separator + ".slicer";

    private final SpringFXMLLoader loader;
    private final Settings settings;
    private final ObjectMapper objectMapper;
    private final FileService fileService;
    private Project currentProject;

    @Autowired
    public DeeControlContext(SpringFXMLLoader loader, SettingsStore settingsStore, FileService fileService) {
        this.loader = loader;
        this.settings = settingsStore.getSettings();
        this.objectMapper = new ObjectMapper();
        this.fileService = fileService;
        this.currentProject = new Project();

    }

    public SpringFXMLLoader getFXMLLoader() {
        return loader;
    }

    public String getSlicerTempFolder() {
        return slicerTempFolder;
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

    public FileService getFileService() {
        return fileService;
    }
}
