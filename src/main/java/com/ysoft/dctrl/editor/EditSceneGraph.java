package com.ysoft.dctrl.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.action.AddModelAction;
import com.ysoft.dctrl.editor.action.DeleteModelAction;
import com.ysoft.dctrl.editor.mesh.ExtendedMesh;
import com.ysoft.dctrl.editor.mesh.MeshGroup;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.editor.utils.ModelInsertionStack;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.event.dto.ModelLoadedDTO;
import com.ysoft.dctrl.math.BoundingBox;
import com.ysoft.dctrl.math.Point3DUtils;
import com.ysoft.dctrl.math.Utils;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

/**
 * Created by pilar on 20.7.2017.
 */

@Component
@SubSceneMode(SceneMode.EDIT)
public class EditSceneGraph extends SubSceneGraph {
    private static final Point3D PRINTER_SIZE = new Point3D(150,150,150);
    private static final Point3D PRINTER_HALF_SIZE = new Point3D(75,75,75);

    private static PhongMaterial MATERIAL = new PhongMaterial(Color.web("#cccccc"));
    private static PhongMaterial SELECTED_MATERIAL = new PhongMaterial(Color.web("#4dc824"));
    private static PhongMaterial INVALID_MATERIAL = new PhongMaterial(Color.web("#ff0000"));

    static {
        MATERIAL.setSpecularColor(Color.web("#333333"));
        MATERIAL.setSpecularPower(10);

        SELECTED_MATERIAL.setSpecularColor(Color.web("#333333"));
        SELECTED_MATERIAL.setSpecularPower(10);

        INVALID_MATERIAL.setSpecularColor(Color.web("#333333"));
        INVALID_MATERIAL.setSpecularPower(10);
    }

    private List<SceneMesh> selected;
    private SceneMesh currentlyFixing;

    private Set<SceneMesh> outOfBounds;
    private ModelInsertionStack modelInsertionStack;

    public EditSceneGraph(EventBus eventBus, ModelInsertionStack modelInsertionStack) {
        super(eventBus);
        selected = new ArrayList<>();
        outOfBounds = new HashSet<>();
        currentlyFixing = null;
        this.modelInsertionStack = modelInsertionStack;
    }

    @PostConstruct
    public void init() {
        eventBus.subscribe(EventType.MODEL_LOADED.name(), (e) -> addMesh((ModelLoadedDTO) e.getData()));
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

    public void addMesh(ModelLoadedDTO modelLoaded) {
        ExtendedMesh extendedMesh = new ExtendedMesh(modelLoaded.getName(), modelLoaded.getMesh());
        extendedMesh.translateToZero();
        extendedMesh.setPositionZ(extendedMesh.getBoundingBox().getHalfSize().getZ());
        extendedMesh.addOnMeshChangeListener(this::fixToBed);
        addMesh(extendedMesh);
        extendedMesh.getView().setOnMousePressed((event -> {
            if(event.getTarget() != extendedMesh.getView()) { return; }
            if(event.isControlDown() && !selected.isEmpty()) {
                addToSelection(extendedMesh);
            } else {
                selectSingle(extendedMesh);
            }
        }));
        extendedMesh.getBoundingBox().setOnChange(bb -> validatePosition(extendedMesh, bb));
        validatePosition(extendedMesh);
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new AddModelAction(this, extendedMesh)));
    }

    public void addMesh(SceneMesh sceneMesh) {
        super.addMesh(sceneMesh);
        sceneMesh.setBoundingBoxVisible(false);
        sceneMesh.setMaterial(MATERIAL);
        modelInsertionStack.addSceneMesh(sceneMesh);
        selectSingle(sceneMesh);
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
        deleteModel(s);
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new DeleteModelAction(this, s)));
    }

    public void deleteModel(SceneMesh mesh) {
        removeMesh(mesh);
        selected.remove(mesh);
        handleOOB(false, mesh);
        modelInsertionStack.removeSceneMesh(mesh);
        if(outOfBounds.isEmpty()) {
            eventBus.publish(new Event(EventType.EDIT_SCENE_VALID.name()));
        }
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
        selected.forEach(m -> {
            m.setMaterial(MATERIAL);
            m.setBoundingBoxVisible(false);
        });
        selected.clear();
        mesh = mesh.getGroup() != null ? mesh.getGroup() : mesh;
        selected.add(mesh);
        mesh.setMaterial(SELECTED_MATERIAL);
        mesh.setBoundingBoxVisible(true);
        eventBus.publish(new Event(EventType.MODEL_SELECTED.name(), mesh));
    }

    private void addToSelection(SceneMesh mesh) {
        mesh = mesh.getGroup() != null ? mesh.getGroup() : mesh;
        if(selected.contains(mesh)) {
            selected.remove(mesh);
            mesh.setMaterial(MATERIAL);
            mesh.setBoundingBoxVisible(false);
            if(selected.size() == 1) { selected.get(0).setBoundingBoxVisible(true); }
        } else {
            selected.add(mesh);
            mesh.setMaterial(SELECTED_MATERIAL);
            mesh.setBoundingBoxVisible(true);
            if(selected.size() == 2) { eventBus.publish(new Event(EventType.MODEL_MULTISELECTION.name())); }
        }
        if(selected.size() > 1) { selected.forEach(m -> m.setBoundingBoxVisible(false)); }
    }

    private void groupModels() {
        if(selected.size() < 2) { return; }
        MeshGroup meshGroup = new MeshGroup(selected);
        selected.forEach(m -> {
            m.setBoundingBoxVisible(false);
            m.getBoundingBox().setOnChange(null);
            super.removeMesh(m);
        });
        super.addMesh(meshGroup);
        meshGroup.getBoundingBox().setOnChange(bb -> validatePosition(meshGroup, bb));
        validatePosition(meshGroup);
        selected.clear();
        selectSingle(meshGroup);
    }

    private void ungroupModels() {
        SceneMesh m = getSelected();
        if(m == null || !(m instanceof MeshGroup)) { return; }
        m.getBoundingBox().setOnChange(null);
        super.removeMesh(m);
        List<? extends SceneMesh> l = ((MeshGroup) m).getChildren();
        l.forEach((sm) -> {
            super.addMesh(sm);
            sm.setMaterial(MATERIAL);
            sm.setBoundingBoxVisible(false);
            sm.getBoundingBox().setOnChange(bb -> validatePosition(sm, bb));
            validatePosition(sm);
        });
        SceneMesh mesh = l.get(0);
        ((MeshGroup) m).dismiss();
        selectSingle(mesh);
    }

    private void validatePosition(SceneMesh mesh) {
        validatePosition(mesh, mesh.getBoundingBox());
    }

    private void validatePosition(SceneMesh mesh, BoundingBox bb) {
        boolean oob = !printerVolume.contains(bb);
        mesh.setOutOfBounds(oob);
        mesh.setMaterial(oob ? INVALID_MATERIAL : (selected.contains(mesh) ? SELECTED_MATERIAL : MATERIAL));

        handleOOB(oob, mesh);
    }

    private void handleOOB(boolean oob, SceneMesh mesh) {
        if(oob) {
            if(outOfBounds.isEmpty()) {
                eventBus.publish(new Event(EventType.EDIT_SCENE_INVALID.name()));
            }
            outOfBounds.add(mesh);
        } else if(outOfBounds.contains(mesh)) {
            outOfBounds.remove(mesh);
            if(outOfBounds.isEmpty()) {
                eventBus.publish(new Event(EventType.EDIT_SCENE_VALID.name()));
            }
        }
    }

    public SceneMesh getSelected() {
        return selected.size() == 1 ? selected.get(0) : null;
    }

    public void setSelected(SceneMesh mesh) {
        selectSingle(mesh);
    }

    public String getCurrentSceneName() {
        return modelInsertionStack.getFirstName();
    }
}
