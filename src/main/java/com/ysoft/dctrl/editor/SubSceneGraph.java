package com.ysoft.dctrl.editor;

import java.util.LinkedList;
import java.util.List;

import com.ysoft.dctrl.editor.mesh.DrawableMesh;
import com.ysoft.dctrl.editor.mesh.PrinterVolume;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.math.BoundingBox;

import javafx.scene.Group;
import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 19.7.2017.
 */
public abstract class SubSceneGraph {
    private final LinkedList<SceneMesh> sceneMeshes;
    private final Group sceneGroup;
    protected final EventBus eventBus;
    protected PrinterVolume printerVolume;

    public SubSceneGraph(EventBus eventBus) {
        this.eventBus = eventBus;
        sceneMeshes = new LinkedList<>();
        sceneGroup = new Group();
    }

    public final Group getSceneGroup() {
        return sceneGroup;
    }

    public final LinkedList<SceneMesh> getSceneMeshes() { return sceneMeshes; }

    protected void addMesh(SceneMesh mesh) {
        sceneMeshes.add(mesh);
        sceneGroup.getChildren().add(mesh.getNode());
    }

    protected void removeMesh(SceneMesh mesh) {
        sceneMeshes.remove(mesh);
        sceneGroup.getChildren().remove(mesh.getNode());
    }

    protected final void setPrinterVolume(PrinterVolume printerVolume) {
        this.printerVolume = printerVolume;
    }
}
