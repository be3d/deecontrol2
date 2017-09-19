package com.ysoft.dctrl.utils;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.EventBus;

import javafx.scene.input.DataFormat;

/**
 * Created by pilar on 18.9.2017.
 */

@Component
public class Clipboard {
    private final static DataFormat MESH_FORMAT = new DataFormat("deecontrol/mesh");

    private final javafx.scene.input.Clipboard systemClipborad;

    private List<SceneMesh> models;

    public Clipboard(EventBus eventBus) {
        this.systemClipborad = javafx.scene.input.Clipboard.getSystemClipboard();
        this.models = null;
    }

    public void addModels(List<SceneMesh> models) {
        this.models = models;
        systemClipborad.setContent(new HashMap<DataFormat, Object>(){{put(MESH_FORMAT, "DeeControlMeshes#" + models.size());}});
    }

    public boolean hasModels() {
        return systemClipborad.getContentTypes().contains(MESH_FORMAT);
    }

    public List<SceneMesh> getModels() {
        return models;
    }

    public boolean hasModelFiles() {
        if(!systemClipborad.hasFiles()) { return false; }
        List<File> files = systemClipborad.getFiles();
        for(File f : files) {
            if(f.getName().toLowerCase().endsWith(".stl")) {
                return true;
            }
        }

        return false;
    }

    public List<File> getModelFiles() {
        List<File> files = systemClipborad.getFiles();
        files.removeIf((f) -> !f.getName().toLowerCase().endsWith(".stl"));
        return files;
    }
}
