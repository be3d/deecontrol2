package com.ysoft.dctrl.editor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.mesh.ExtendedMesh;
import com.ysoft.dctrl.editor.mesh.MeshGroup;
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

    private List<SceneMesh> selected;
    private SceneMesh currentlyFixing;

    public EditSceneGraph(EventBus eventBus) {
        super(eventBus);
        selected = new ArrayList<>();
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
        eventBus.subscribe(EventType.EDIT_GROUP.name(), (e) -> groupModels());
        eventBus.subscribe(EventType.EDIT_UNGROUP.name(), (e) -> ungroupModels());
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
            if(event.isControlDown() && !selected.isEmpty()) {
                addToSelection(extendedMesh);
            } else {
                selectSingle(extendedMesh);
            }
        }));
    }

    public void centerSelected() {
        SceneMesh s = getSelected();
        if(s == null) { return; }
        s.setPosition(getCenteredPosition(s));
    }

    public void alignSelectedToLeft() {
        SceneMesh s = getSelected();
        if(s == null) { return; }
        BoundingBox bb = s.getBoundingBox();
        Point2D c = getCenteredPosition(s);
        s.setPosition(new Point2D(-PRINTER_HALF_SIZE.getX() + bb.getHalfSize().getX() + c.getX(), s.getPositionY()));
    }

    public void alignSelectedToRight() {
        SceneMesh s = getSelected();
        if(s == null) { return; }
        BoundingBox bb = s.getBoundingBox();
        Point2D c = getCenteredPosition(s);
        s.setPosition(new Point2D(PRINTER_HALF_SIZE.getX() - bb.getHalfSize().getX() + c.getX(), s.getPositionY()));
    }

    public void alignSelectedToFront() {
        SceneMesh s = getSelected();
        if(s == null) { return; }
        BoundingBox bb = s.getBoundingBox();
        Point2D c = getCenteredPosition(s);
        s.setPosition(new Point2D(s.getPositionX(), -PRINTER_HALF_SIZE.getY() + bb.getHalfSize().getY() + c.getY()));
    }

    public void alignSelectedToBack() {
        SceneMesh s = getSelected();
        if(s == null) { return; }
        BoundingBox bb = s.getBoundingBox();
        Point2D c = getCenteredPosition(s);
        s.setPosition(new Point2D(s.getPositionX(), PRINTER_HALF_SIZE.getY() - bb.getHalfSize().getY() + c.getY()));
    }

    private Point2D getCenteredPosition(SceneMesh mesh) {
        BoundingBox bb = mesh.getBoundingBox();
        Point3D p = mesh.getPosition();
        return new Point2D(p.getX() - bb.getMin().getX() - bb.getHalfSize().getX(), p.getY() - bb.getMin().getY() - bb.getHalfSize().getY());
    }

    public void scaleSelectedToMax() {
        SceneMesh s = getSelected();
        if(s == null) return;
        Point3D size = s.getBoundingBox().getSize();
        size = Point3DUtils.divideElements(size, s.getScale());
        double scale = Utils.min(PRINTER_SIZE.getX()/size.getX(), PRINTER_SIZE.getY()/size.getY(), PRINTER_SIZE.getZ()/size.getZ());
        s.setScale(scale);
        s.setPosition(getCenteredPosition(s));
    }

    public void fixToBed(SceneMesh mesh) {
        if(currentlyFixing == mesh) { return; }
        currentlyFixing = mesh;

        BoundingBox bb = mesh.getBoundingBox();
        mesh.setPositionZ(mesh.getPositionZ() - bb.getMin().getZ());
        currentlyFixing = null;
    }

    public void deleteSelected() {
        SceneMesh s = getSelected();
        if(s == null) { return; }
        removeMesh(s);
        selected.remove(s);
        selectNext();
    }

    public void selectNext() {
        LinkedList<SceneMesh> sm = getSceneMeshes();
        SceneMesh s = getSelected();
        if(s == null) {
            if(!sm.isEmpty()) selectSingle(sm.getFirst());
        } else {
            int next = sm.indexOf(s) + 1;
            if(next > sm.size() - 1) { next = 0; }
            selectSingle(sm.get(next));
        }
    }

    public void selectPrevious() {
        LinkedList<SceneMesh> sm = getSceneMeshes();
        SceneMesh s = getSelected();
        if(s == null) {
            if(!sm.isEmpty()) selectSingle(sm.getLast());
        } else {
            int prev = sm.indexOf(s) - 1;
            if(prev < 0) { prev = sm.size() -1; }
            selectSingle(sm.get(prev));
        }
    }

    private void selectSingle(SceneMesh mesh) {
        selected.forEach(m -> m.setMaterial(material));
        selected.clear();
        mesh = mesh.getGroup() != null ? mesh.getGroup() : mesh;
        selected.add(mesh);
        mesh.setMaterial(selectedMaterial);
        eventBus.publish(new Event(EventType.MODEL_SELECTED.name(), mesh));
    }

    private void addToSelection(SceneMesh mesh) {
        mesh = mesh.getGroup() != null ? mesh.getGroup() : mesh;
        if(selected.contains(mesh)) {
            selected.remove(mesh);
            mesh.setMaterial(material);
        } else {
            selected.add(mesh);
            mesh.setMaterial(selectedMaterial);
            if(selected.size() == 2) { eventBus.publish(new Event(EventType.MODEL_MULTISELECTION.name())); }
        }
    }

    private void groupModels() {
        if(selected.size() < 2) { return; }
        MeshGroup meshGroup = new MeshGroup(selected);
        selected.forEach(this::removeMesh);
        addMesh(meshGroup);
        selected.clear();
        selectSingle(meshGroup);
    }

    private void ungroupModels() {
        SceneMesh m = getSelected();
        if(m == null || !(m instanceof MeshGroup)) { return; }
        removeMesh(m);
        List<? extends SceneMesh> l = ((MeshGroup) m).getChildren();
        l.forEach((sm) -> {
            addMesh(sm);
            sm.setMaterial(material);
        });
        SceneMesh mesh = l.get(0);
        ((MeshGroup) m).dismiss();
        selectSingle(mesh);
    }

    public SceneMesh getSelected() {
        return selected.size() == 1 ? selected.get(0) : null;
    }
}
