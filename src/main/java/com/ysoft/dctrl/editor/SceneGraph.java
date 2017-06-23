package com.ysoft.dctrl.editor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.control.ExtendedPerspectiveCamera;
import com.ysoft.dctrl.editor.exporter.SceneExporter;
import com.ysoft.dctrl.editor.mesh.ExtendedMesh;
import com.ysoft.dctrl.editor.mesh.MeshUtils;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.math.BoundingBox;
import com.ysoft.dctrl.slicer.SlicerController;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Created by pilar on 6.4.2017.
 */

@Component
public class SceneGraph {
    private LinkedList<SceneMesh> sceneMeshes;
    private ExtendedPerspectiveCamera camera;

    private Group sceneGroup;

    private PhongMaterial material;
    private PhongMaterial selectedMaterial;

    private SceneMesh selected;

    private final EventBus eventBus;
    private final DeeControlContext deeControlContext;

    @Autowired
    public SceneGraph(EventBus eventBus, DeeControlContext deeControlContext) {
        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
        sceneGroup = new Group();
        sceneMeshes = new LinkedList<>();
        material = new PhongMaterial(Color.LIGHTBLUE);
        selectedMaterial = new PhongMaterial(new Color(0.3f, 0.4f, 0.9019608f, 1));
        camera = createCamera();
        sceneGroup.getChildren().addAll(camera, createPrintBed());
        selected = null;

        eventBus.subscribe(EventType.MODEL_LOADED.name(), (e) -> addMesh((TriangleMesh) e.getData()));
        eventBus.subscribe(EventType.CENTER_SELECTED_MODEL.name(), (e) -> centerSelected());
        eventBus.subscribe(EventType.ALIGN_LEFT_SELECTED_MODEL.name(), (e) -> alignSelectedToLeft());
        eventBus.subscribe(EventType.ALIGN_RIGHT_SELECTED_MODEL.name(), (e) -> alignSelectedToRight());
        eventBus.subscribe(EventType.ALIGN_FRONT_SELECTED_MODEL.name(), (e) -> alignSelectedToFront());
        eventBus.subscribe(EventType.ALIGN_BACK_SELECTED_MODEL.name(), (e) -> alignSelectedToBack());
        eventBus.subscribe(EventType.EXPORT_SCENE.name(), (e) -> exportScene());
    }

    private ExtendedPerspectiveCamera createCamera() {
        ExtendedPerspectiveCamera camera = new ExtendedPerspectiveCamera(true);
        camera.setInitialTransforms(new Rotate(-90, Rotate.X_AXIS));
        camera.setFarClip(10000);
        return camera;
    }

    private Box createPrintBed() {
        Box bed = new Box(150, 150, 4);
        bed.setMaterial(new PhongMaterial(Color.GRAY));
        bed.getTransforms().addAll(new Translate(0,0,-bed.getDepth()/2));
        return bed;
    }

    public ExtendedPerspectiveCamera getCamera() {
        return camera;
    }

    public Group getSceneGroup() {
        return sceneGroup;
    }

    public LinkedList<SceneMesh> getSceneMeshses() {
        return sceneMeshes;
    }

    public LinkedList<SceneMesh> getSceneMeshes() { return sceneMeshes; }

    public void centerSelected() {
        if(selected == null) { return; }
        selected.setPosition(new Point2D(0,0));
    }

    public void alignSelectedToLeft() {
        if(selected == null) { return; }
        selected.setPosition(new Point2D(-75 + selected.getBoundingBox().getHalfSize().getX(), selected.getPositionY()));
    }

    public void alignSelectedToRight() {
        if(selected == null) { return; }
        selected.setPosition(new Point2D(75 - selected.getBoundingBox().getHalfSize().getX(), selected.getPositionY()));
    }

    public void alignSelectedToFront() {
        if(selected == null) { return; }
        selected.setPosition(new Point2D(selected.getPositionX(), -75 + selected.getBoundingBox().getHalfSize().getY()));
    }

    public void alignSelectedToBack() {
        if(selected == null) { return; }
        selected.setPosition(new Point2D(selected.getPositionX(), 75 - selected.getBoundingBox().getHalfSize().getY()));
    }

    public void addMesh(TriangleMesh mesh) {
        ExtendedMesh extendedMesh = new ExtendedMesh(mesh);
        extendedMesh.setMaterial(material);
        extendedMesh.translateToZero();
        extendedMesh.setPositionZ(extendedMesh.getBoundingBox().getHalfSize().getZ());
        sceneMeshes.add(extendedMesh);
        sceneGroup.getChildren().add(extendedMesh.getNode());
        extendedMesh.getNode().setOnMouseClicked((event -> {
            if(event.getTarget() != extendedMesh.getNode()) { return; }
            selectNew(extendedMesh);
        }));
    }

    public void deleteSelected() {
        if(selected == null) { return; }

        sceneMeshes.remove(selected);
        sceneGroup.getChildren().remove(selected.getNode());

        selected = null;
        selectNext();
    }

    public void selectNext() {
        if(selected == null) {
            if(!sceneMeshes.isEmpty()) selectNew(sceneMeshes.getFirst());
        } else {
            int next = sceneMeshes.indexOf(selected) + 1;
            if(next > sceneMeshes.size() - 1) { next = 0; }
            selectNew(sceneMeshes.get(next));
        }
    }

    public void selectPrevious() {
        if(selected == null) {
            if(!sceneMeshes.isEmpty()) selectNew(sceneMeshes.getLast());
        } else {
            int prev = sceneMeshes.indexOf(selected) - 1;
            if(prev < 0) { prev = sceneMeshes.size() -1; }
            selectNew(sceneMeshes.get(prev));
        }
    }

    private void selectNew(SceneMesh mesh) {
        if(selected != null) { selected.setMaterial(material); }
        selected = mesh;
        selected.setMaterial(selectedMaterial);
        eventBus.publish(new Event(EventType.MODEL_SELECTED.name(), selected));
    }

    public SceneMesh getSelected() {
        return selected;
    }

    public void exportScene() {
        SceneExporter sceneExporter = new SceneExporter(eventBus, deeControlContext);
        sceneExporter.exportScene(this, SlicerController.sceneSTL);
    }
}
