package com.ysoft.dctrl.editor;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.mesh.ExtendedMesh;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.math.BoundingBox;
import com.ysoft.dctrl.math.Point3DUtils;
import com.ysoft.dctrl.math.Utils;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 20.7.2017.
 */

@Component
@SubSceneMode(SceneMode.EDIT)
public class EditSceneGraph extends SubSceneGraph {
    private static final Point3D PRINTER_SIZE = new Point3D(150,150,150);
    private static final Point3D PRINTER_HALF_SIZE = new Point3D(75,75,75);

    private static final PhongMaterial material = new PhongMaterial(Color.LIGHTBLUE);
    private static final PhongMaterial selectedMaterial = new PhongMaterial(new Color(0.3f, 0.4f, 0.9019608f, 1));

    static {
        material.setSpecularColor(new Color(0.2,0.2,0.2,1));
        material.setSpecularPower(10);

        selectedMaterial.setSpecularColor(new Color(0.2,0.2,0.2,1));
        selectedMaterial.setSpecularPower(10);
    }

    private SceneMesh selected;
    private SceneMesh currentlyFixing;

    public EditSceneGraph(EventBus eventBus) {
        super(eventBus);
        selected = null;
        currentlyFixing = null;
    }

    @PostConstruct
    public void init() {
        eventBus.subscribe(EventType.MODEL_LOADED.name(), (e) -> addMesh((TriangleMesh) e.getData()));
        eventBus.subscribe(EventType.CENTER_SELECTED_MODEL.name(), (e) -> centerSelected());
        eventBus.subscribe(EventType.ALIGN_LEFT_SELECTED_MODEL.name(), (e) -> alignSelectedToLeft());
        eventBus.subscribe(EventType.ALIGN_RIGHT_SELECTED_MODEL.name(), (e) -> alignSelectedToRight());
        eventBus.subscribe(EventType.ALIGN_FRONT_SELECTED_MODEL.name(), (e) -> alignSelectedToFront());
        eventBus.subscribe(EventType.ALIGN_BACK_SELECTED_MODEL.name(), (e) -> alignSelectedToBack());
        eventBus.subscribe(EventType.SCALE_MAX_SELECTED_MODEL.name(), (e) -> scaleSelectedToMax());
        eventBus.subscribe(EventType.EDIT_SELECT_PREV.name(), (e) -> selectPrevious());
        eventBus.subscribe(EventType.EDIT_SELECT_NEXT.name(), (e) -> selectNext());
        eventBus.subscribe(EventType.EDIT_DELETE_SELECTED.name(), (e) -> deleteSelected());
    }

    public void addMesh(TriangleMesh mesh) {
        ExtendedMesh extendedMesh = new ExtendedMesh(mesh);
        extendedMesh.setMaterial(material);
        extendedMesh.translateToZero();
        extendedMesh.setPositionZ(extendedMesh.getBoundingBox().getHalfSize().getZ());
        extendedMesh.addOnMeshChangeListener(this::fixToBed);
        addMesh(extendedMesh);
        extendedMesh.getNode().setOnMouseClicked((event -> {
            if(event.getTarget() != extendedMesh.getNode()) { return; }
            selectNew(extendedMesh);
        }));
    }

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
        mesh.setPositionZ(mesh.getPositionZ() - bb.getMin().getZ());
        currentlyFixing = null;
    }

    public void deleteSelected() {
        if(selected == null) { return; }
        removeMesh(selected);
        selected = null;
        selectNext();
    }

    public void selectNext() {
        LinkedList<SceneMesh> sm = getSceneMeshes();
        if(selected == null) {
            if(!sm.isEmpty()) selectNew(sm.getFirst());
        } else {
            int next = sm.indexOf(selected) + 1;
            if(next > sm.size() - 1) { next = 0; }
            selectNew(sm.get(next));
        }
    }

    public void selectPrevious() {
        LinkedList<SceneMesh> sm = getSceneMeshes();
        if(selected == null) {
            if(!sm.isEmpty()) selectNew(sm.getLast());
        } else {
            int prev = sm.indexOf(selected) - 1;
            if(prev < 0) { prev = sm.size() -1; }
            selectNew(sm.get(prev));
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
