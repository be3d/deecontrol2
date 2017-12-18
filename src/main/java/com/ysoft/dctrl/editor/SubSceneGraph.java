package com.ysoft.dctrl.editor;

import java.util.LinkedList;
import java.util.List;

import com.ysoft.dctrl.editor.mesh.DrawableMesh;
import com.ysoft.dctrl.editor.mesh.PrinterVolume;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.math.BoundingBox;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 19.7.2017.
 */
public abstract class SubSceneGraph {
    private final LinkedList<SceneMesh> sceneMeshes;
    private final Group sceneGroup;
    private final Group helpGroup;
    protected final EventBus eventBus;
    protected PrinterVolume printerVolume;

    public SubSceneGraph(EventBus eventBus) {
        this.eventBus = eventBus;
        sceneMeshes = new LinkedList<>();
        sceneGroup = new Group();
        helpGroup = new Group();
    }

    final Group getSceneGroup() {
        return sceneGroup;
    }
    final Group getHelpGroup() { return helpGroup; }

    public final LinkedList<SceneMesh> getSceneMeshes() { return sceneMeshes; }

    protected void addMesh(SceneMesh mesh) {
        sceneMeshes.add(mesh);
        sceneGroup.getChildren().add(mesh.getNode());
    }

    protected void addMeshes(List<SceneMesh> meshes){
        meshes.forEach((m) -> addMesh(m));
    }

    protected void removeMesh(SceneMesh mesh) {
        sceneMeshes.remove(mesh);
        sceneGroup.getChildren().remove(mesh.getNode());
    }

    protected void addHelpObject(Node node) {
        helpGroup.getChildren().add(node);
    }

    protected void remvoeHelpObject(Node node) {
        helpGroup.getChildren().remove(node);
    }

    protected void setPrinterVolume(PrinterVolume printerVolume) {
        this.printerVolume = printerVolume;
    }
}
