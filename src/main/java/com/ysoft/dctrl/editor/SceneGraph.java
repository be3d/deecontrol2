package com.ysoft.dctrl.editor;

import java.util.LinkedList;

import com.ysoft.dctrl.editor.mesh.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.control.ExtendedPerspectiveCamera;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.math.BoundingBox;
import com.ysoft.dctrl.math.Point3DUtils;
import com.ysoft.dctrl.math.Utils;

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
    private static final Point3D PRINTER_SIZE= new Point3D(150,150,150);
    private static final Point3D PRINTER_HALF_SIZE= new Point3D(75,75,75);

    private LinkedList<SceneMesh> sceneMeshes;
    private ExtendedPerspectiveCamera camera;

    private Group sceneGroup;

    private PhongMaterial material;
    private PhongMaterial selectedMaterial;

    private SceneMesh selected;

    private final EventBus eventBus;

    private SceneMesh currentlyFixing;

    @Autowired
    public SceneGraph(EventBus eventBus) {
        this.eventBus = eventBus;
        sceneGroup = new Group();
        sceneMeshes = new LinkedList<>();

        material = new PhongMaterial(Color.LIGHTBLUE);
        selectedMaterial = new PhongMaterial(new Color(0.3f, 0.4f, 0.9019608f, 1));
        camera = createCamera();
        sceneGroup.getChildren().addAll(camera, createPrintBed());
        selected = null;
        currentlyFixing = null;

        eventBus.subscribe(EventType.MODEL_LOADED.name(), (e) -> addMesh((TriangleMesh) e.getData()));
        eventBus.subscribe(EventType.CENTER_SELECTED_MODEL.name(), (e) -> centerSelected());
        eventBus.subscribe(EventType.ALIGN_LEFT_SELECTED_MODEL.name(), (e) -> alignSelectedToLeft());
        eventBus.subscribe(EventType.ALIGN_RIGHT_SELECTED_MODEL.name(), (e) -> alignSelectedToRight());
        eventBus.subscribe(EventType.ALIGN_FRONT_SELECTED_MODEL.name(), (e) -> alignSelectedToFront());
        eventBus.subscribe(EventType.ALIGN_BACK_SELECTED_MODEL.name(), (e) -> alignSelectedToBack());
        eventBus.subscribe(EventType.SCALE_MAX_SELECTED_MODEL.name(), (e) -> scaleSelectedToMax());
    }

    private ExtendedPerspectiveCamera createCamera() {
        ExtendedPerspectiveCamera camera = new ExtendedPerspectiveCamera(true);
        camera.setInitialTransforms(new Rotate(-90, Rotate.X_AXIS));
        camera.setFarClip(10000);
        return camera;
    }

    private Box createPrintBed() {
        Box bed = new Box(PRINTER_SIZE.getX(), PRINTER_SIZE.getY(), 4);
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

    public LinkedList<SceneMesh> getSceneMeshes() { return sceneMeshes; }

    public void centerSelected() {
        if(selected == null) { return; }
        selected.setPosition(getCenteredPosition(selected));
    }

    public void alignSelectedToLeft() {
        if(selected == null) { return; }
        BoundingBox bb = selected.getBoundingBox();
        Point2D c = getCenteredPosition(selected);
        selected.setPosition(new Point2D(-PRINTER_HALF_SIZE.getX() + bb.getHalfSize().getX() + c.getX(), selected.getPositionY()));
    }

    public void alignSelectedToRight() {
        if(selected == null) { return; }
        BoundingBox bb = selected.getBoundingBox();
        Point2D c = getCenteredPosition(selected);
        selected.setPosition(new Point2D(PRINTER_HALF_SIZE.getX() - bb.getHalfSize().getX() + c.getX(), selected.getPositionY()));
    }

    public void alignSelectedToFront() {
        if(selected == null) { return; }
        BoundingBox bb = selected.getBoundingBox();
        Point2D c = getCenteredPosition(selected);
        selected.setPosition(new Point2D(selected.getPositionX(), -PRINTER_HALF_SIZE.getY() + bb.getHalfSize().getY() + c.getY()));
    }

    public void alignSelectedToBack() {
        if(selected == null) { return; }
        BoundingBox bb = selected.getBoundingBox();
        Point2D c = getCenteredPosition(selected);
        selected.setPosition(new Point2D(selected.getPositionX(), PRINTER_HALF_SIZE.getY() - bb.getHalfSize().getY() + c.getY()));
    }

    private Point2D getCenteredPosition(SceneMesh mesh) {
        BoundingBox bb = mesh.getBoundingBox();
        Point3D p = mesh.getPosition();
        return new Point2D(p.getX() - bb.getMin().getX() - bb.getHalfSize().getX(), p.getY() - bb.getMin().getY() - bb.getHalfSize().getY());
    }

    public void scaleSelectedToMax() {
        if(selected == null) return;
        Point3D size = selected.getBoundingBox().getSize();
        size = Point3DUtils.divideElements(size, selected.getScale());
        double scale = Utils.min(PRINTER_SIZE.getX()/size.getX(), PRINTER_SIZE.getY()/size.getY(), PRINTER_SIZE.getZ()/size.getZ());
        selected.setScale(scale);
    }

    public void fixToBed(SceneMesh mesh) {
        if(currentlyFixing == mesh) { return; }
        currentlyFixing = mesh;

        BoundingBox bb = mesh.getBoundingBox();
        if(bb.getMin().getZ() != 0) {
            mesh.setPositionZ(bb.getHalfSize().getZ());
        }
        currentlyFixing = null;
    }

    public void addMesh(TriangleMesh mesh) {
        ExtendedMesh extendedMesh = new ExtendedMesh(mesh);
        extendedMesh.setMaterial(material);
        extendedMesh.translateToZero();
        extendedMesh.setPositionZ(extendedMesh.getBoundingBox().getHalfSize().getZ());
        extendedMesh.addOnMeshChangeListener(this::fixToBed);
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

    public void hideAllMeshes(){
        for (SceneMesh mesh : sceneMeshes){
            mesh.getNode().setVisible(false);
        }
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
}
