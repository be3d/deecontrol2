package com.ysoft.dctrl.editor;

import java.util.*;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.action.AddModelAction;
import com.ysoft.dctrl.editor.action.DeleteModelAction;
import com.ysoft.dctrl.editor.action.GroupModelAction;
import com.ysoft.dctrl.editor.action.ModelScaleAction;
import com.ysoft.dctrl.editor.action.ModelTransformAction;
import com.ysoft.dctrl.editor.action.ModelTranslateAction;
import com.ysoft.dctrl.editor.action.SelectModelAction;
import com.ysoft.dctrl.editor.action.UngroupModelAction;
import com.ysoft.dctrl.editor.mesh.ExtendedMesh;
import com.ysoft.dctrl.editor.mesh.MeshGroup;
import com.ysoft.dctrl.editor.mesh.PrinterVolume;
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
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;

/**
 * Created by pilar on 20.7.2017.
 */

@Component
@SubSceneMode(SceneMode.EDIT)
public class EditSceneGraph extends SubSceneGraph {
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

    private Consumer<SceneMesh> onMeshChangeConsumer;

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
        eventBus.subscribe(EventType.EDIT_SELECT_ALL.name(), (e) -> selectAll());
        eventBus.subscribe(EventType.EDIT_DELETE_SELECTED.name(), (e) -> deleteSelected());
        eventBus.subscribe(EventType.EDIT_GROUP.name(), (e) -> groupSelected());
        eventBus.subscribe(EventType.EDIT_UNGROUP.name(), (e) -> ungroupSelected());
        eventBus.subscribe(EventType.EDIT_CLEAR_SELECTION.name(), (e) -> clearSelection());

        onMeshChangeConsumer = (mesh) -> {
            eventBus.publish(new Event(EventType.MODEL_CHANGED.name(), mesh));
        };

        eventBus.subscribe(EventType.PRINT_VOLUME_OFFSET_CHANGED.name(), (e) -> {
            printerVolume.setPlatformOffset((double) e.getData());
            getSceneMeshes().forEach(m -> validatePosition(m));
        });
    }

    @Override
    protected void setPrinterVolume(PrinterVolume printerVolume) {
        super.setPrinterVolume(printerVolume);
        addHelpObject(printerVolume.getOffsetNode());
    }

    public void addMesh(ModelLoadedDTO modelLoaded) {
        ExtendedMesh extendedMesh = new ExtendedMesh(modelLoaded.getName(), modelLoaded.getMesh());
        initMesh(extendedMesh);
        extendedMesh.translateToZero();
        extendedMesh.setPositionZ(extendedMesh.getBoundingBox().getHalfSize().getZ());
        addMesh(extendedMesh);
        extendedMesh.getBoundingBox().setOnChange(bb -> validatePosition(extendedMesh, bb));
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new AddModelAction(this::addMesh, this::deleteModel, extendedMesh)));
    }

    public void cloneMesh(SceneMesh original) {
        SceneMesh mesh = original.clone(new Point3D(5,0,0));
        if(mesh instanceof MeshGroup) {
            ((MeshGroup) mesh).getChildren().forEach(this::initMesh);
        } else {
            initMesh((ExtendedMesh) mesh);
        }
        addMesh(mesh);
        mesh.getBoundingBox().setOnChange(bb -> validatePosition(mesh, bb));
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new AddModelAction(this::addMesh, this::deleteModel, mesh)));
    }

    private void initMesh(ExtendedMesh extendedMesh) {
        extendedMesh.addOnMeshChangeListener(this::fixToBed);
        extendedMesh.getView().setOnMousePressed((event -> {
            if(event.getTarget() != extendedMesh.getView()) { return; }
            Consumer<SceneMesh> selectionConsumer;
            List<SceneMesh> oldSelection = new LinkedList<>(selected);
            if(event.isControlDown() && !selected.isEmpty()) {
                addToSelection(extendedMesh);
                selectionConsumer = this::addToSelection;
            } else if(getSelected() == (extendedMesh.getGroup() != null? extendedMesh.getGroup() : extendedMesh)) {
                return;
            } else {
                selectSingle(extendedMesh);
                selectionConsumer = this::selectSingle;
            }
            eventBus.publish(new Event(EventType.ADD_ACTION.name(), new SelectModelAction(
                    selectionConsumer, this::setSelected, extendedMesh, oldSelection
            )));
        }));
        extendedMesh.getBoundingBox().setOnChange(bb -> validatePosition(extendedMesh, bb));
        extendedMesh.addOnMeshChangeListener(onMeshChangeConsumer);

    }

    @Override
    public void addMesh(SceneMesh sceneMesh) {
        super.addMesh(sceneMesh);
        sceneMesh.setBoundingBoxVisible(false);
        sceneMesh.setMaterial(MATERIAL);
        modelInsertionStack.addSceneMesh(sceneMesh);

        selectSingle(sceneMesh);
        validatePosition(sceneMesh);
    }

    public void centerSelected() {
        SceneMesh s = getSelected();
        if(s == null) { return; }
        Point3D oldPosition = s.getPosition();
        s.setPosition(getCenteredPosition(s));
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new ModelTranslateAction(s, oldPosition, s.getPosition())));
    }

    public void alignSelectedToLeft() {
        SceneMesh s = getSelected();
        if(s == null) { return; }
        BoundingBox bb = s.getBoundingBox();
        Point2D c = getCenteredPosition(s);
        Point3D oldPosition = s.getPosition();
        s.setPosition(new Point2D(-printerVolume.getHalfSize().getX() + bb.getHalfSize().getX() + c.getX(), s.getPositionY()));
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new ModelTranslateAction(s, oldPosition, s.getPosition())));
    }

    public void alignSelectedToRight() {
        SceneMesh s = getSelected();
        if(s == null) { return; }
        BoundingBox bb = s.getBoundingBox();
        Point2D c = getCenteredPosition(s);
        Point3D oldPosition = s.getPosition();
        s.setPosition(new Point2D(printerVolume.getHalfSize().getX() - bb.getHalfSize().getX() + c.getX(), s.getPositionY()));
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new ModelTranslateAction(s, oldPosition, s.getPosition())));
    }

    public void alignSelectedToFront() {
        SceneMesh s = getSelected();
        if(s == null) { return; }
        BoundingBox bb = s.getBoundingBox();
        Point2D c = getCenteredPosition(s);
        Point3D oldPosition = s.getPosition();
        s.setPosition(new Point2D(s.getPositionX(), -printerVolume.getHalfSize().getY() + bb.getHalfSize().getY() + c.getY()));
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new ModelTranslateAction(s, oldPosition, s.getPosition())));
    }

    public void alignSelectedToBack() {
        SceneMesh s = getSelected();
        if(s == null) { return; }
        BoundingBox bb = s.getBoundingBox();
        Point2D c = getCenteredPosition(s);
        Point3D oldPosition = s.getPosition();
        s.setPosition(new Point2D(s.getPositionX(), printerVolume.getHalfSize().getY() - bb.getHalfSize().getY() + c.getY()));
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new ModelTranslateAction(s, oldPosition, s.getPosition())));
    }

    private Point2D getCenteredPosition(SceneMesh mesh) {
        BoundingBox bb = mesh.getBoundingBox();
        Point3D p = mesh.getPosition();
        return new Point2D(p.getX() - bb.getMin().getX() - bb.getHalfSize().getX(), p.getY() - bb.getMin().getY() - bb.getHalfSize().getY());
    }

    public void scaleSelectedToMax() {
        SceneMesh s = getSelected();
        if(s == null) { return; }
        Point3D size = s.getBoundingBox().getSize();
        size = Point3DUtils.divideElements(size, s.getScale());
        Point3D oldScale = s.getScale();
        Point3D oldPosition = s.getPosition();
        Point3D volumeSize = printerVolume.getSize();
        double scale = Utils.min(volumeSize.getX()/size.getX(), volumeSize.getY()/size.getY(), volumeSize.getZ()/size.getZ());
        s.setScale(scale);
        s.setPosition(getCenteredPosition(s));
        ModelTranslateAction translateAction = new ModelTranslateAction(s, oldPosition, s.getPosition());
        ModelScaleAction scaleAction = new ModelScaleAction(s, oldScale, s.getScale());
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new ModelTransformAction(translateAction, scaleAction)));
    }

    public void fixToBed(SceneMesh mesh) {
        if(currentlyFixing == mesh) { return; }
        currentlyFixing = mesh;

        BoundingBox bb = mesh.getBoundingBox();
        mesh.setPositionZ(mesh.getPositionZ() - bb.getMin().getZ());
        currentlyFixing = null;
    }

    public void clearSelection() {
        selected.forEach(m -> {
            m.setMaterial(MATERIAL);
            m.setBoundingBoxVisible(false);
        });
        selected.clear();
        eventBus.publish(new Event(EventType.MODEL_SELECTED.name(), null));
    }

    public void deleteSelected() {
        List<SceneMesh> selected = new ArrayList<>(getSelectedAll());
        selected.forEach(mesh -> deleteModel(mesh));
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new DeleteModelAction(this::deleteModels, this::addMeshes, selected)));
    }

    private void deleteModels(List<SceneMesh> meshes){
        meshes.forEach( (m) -> deleteModel(m));
    }

    private void deleteModel(SceneMesh mesh) {
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
            if(!sm.isEmpty()) { selectSingle(sm.getFirst()); }
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
            if(!sm.isEmpty()) { selectSingle(sm.getLast()); }
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
        mesh.setBoundingBoxVisible(true);
        validatePosition(mesh);
        eventBus.publish(new Event(EventType.MODEL_SELECTED.name(), mesh));
    }

    private void selectAll(){
        LinkedList<SceneMesh> sm = getSceneMeshes();
        selected.clear();
        sm.forEach(m -> {
            selected.add(m);
            validatePosition(m);
            m.setBoundingBoxVisible(true);
        });
    }

    private void addToSelection(SceneMesh mesh) {
        mesh = mesh.getGroup() != null ? mesh.getGroup() : mesh;
        if(selected.contains(mesh)) {
            selected.remove(mesh);
            mesh.setMaterial(MATERIAL);
            mesh.setBoundingBoxVisible(false);
            if(selected.size() == 1) {
                selected.get(0).setBoundingBoxVisible(true);
                eventBus.publish(new Event(EventType.MODEL_SELECTED.name(), selected.get(0)));}
        } else {
            selected.add(mesh);
            validatePosition(mesh);
            mesh.setBoundingBoxVisible(true);
            if(selected.size() == 2) { eventBus.publish(new Event(EventType.MODEL_MULTISELECTION.name())); }
        }
        if(selected.size() > 1) { selected.forEach(m -> m.setBoundingBoxVisible(false)); }
    }

    private void setSelected(List<SceneMesh> selection) {
        int size = selection.size();
        int oldSize = selected.size();
        if(size == 0) {
            clearSelection();
        } else if(size == 1) {
            selectSingle(selection.get(0));
        } else {
            selected.forEach((m) -> {
                m.setBoundingBoxVisible(false);
                m.setMaterial(MATERIAL);
            });
            selected.clear();
            selection.forEach((m) -> {
                m.setBoundingBoxVisible(false);
                validatePosition(m);
                selected.add(m);
            });
            if(oldSize < 2) { eventBus.publish(new Event(EventType.MODEL_MULTISELECTION.name())); }
        }
    }

    private void groupSelected() {
        if(selected.size() < 2) { return; }
        List<SceneMesh> meshes = new LinkedList<>(selected);
        groupModels(selected);
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new GroupModelAction(
                this::groupModels, this::ungroupModels, meshes
        )));
    }

    private void groupModels(List<SceneMesh> models) {
        MeshGroup meshGroup = new MeshGroup(models);
        models.forEach(m -> {
            m.setBoundingBoxVisible(false);
            m.getBoundingBox().setOnChange(null);
            m.removeOnMeshChangeListener(onMeshChangeConsumer);
            super.removeMesh(m);
        });
        super.addMesh(meshGroup);
        meshGroup.addOnMeshChangeListener(onMeshChangeConsumer);
        meshGroup.getBoundingBox().setOnChange(bb -> validatePosition(meshGroup, bb));
        validatePosition(meshGroup);
        selected.clear();
        selectSingle(meshGroup);
    }

    private void ungroupSelected() {
        SceneMesh m = getSelected();
        if(m == null || !(m instanceof MeshGroup)) { return; }
        List<SceneMesh> meshes = new LinkedList<>(((MeshGroup) m).getChildren());
        ungroupModels((MeshGroup) m, false);
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new UngroupModelAction(
                this::ungroupModels, this::groupModels, meshes
        )));
    }

    private void ungroupModels(MeshGroup meshGroup) {
        ungroupModels(meshGroup, true);
    }

    private void ungroupModels(MeshGroup meshGroup, boolean selectAll) {
        meshGroup.getBoundingBox().setOnChange(null);
        meshGroup.removeOnMeshChangeListener(onMeshChangeConsumer);
        super.removeMesh(meshGroup);
        List<SceneMesh> l = new LinkedList<>(meshGroup.getChildren());
        l.forEach((sm) -> {
            super.addMesh(sm);
            sm.setMaterial(MATERIAL);
            sm.setBoundingBoxVisible(false);
            sm.getBoundingBox().setOnChange(bb -> validatePosition(sm, bb));
            sm.addOnMeshChangeListener(onMeshChangeConsumer);
            validatePosition(sm);
        });
        meshGroup.dismiss();
        if(selectAll) {
            setSelected(l);
        } else {
            selectSingle(l.get(0));
        }
    }

    private void validatePosition(SceneMesh mesh) {
        validatePosition(mesh, mesh.getBoundingBox());
    }

    private void validatePosition(SceneMesh mesh, BoundingBox bb) {
        boolean oob = !printerVolume.contains(bb);
        mesh.setOutOfBounds(oob);
        Material validMaterial = selected.contains(mesh) ? SELECTED_MATERIAL : MATERIAL;
        mesh.setMaterial(oob ? INVALID_MATERIAL : validMaterial);

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

    public List<SceneMesh> getSelectedAll(){
        return selected.size() > 0 ? selected : new ArrayList<>();
    }

    public void setSelected(SceneMesh mesh) {
        selectSingle(mesh);
    }

    public String getCurrentSceneName() {
        return modelInsertionStack.getFirstName();
    }

    public List<SceneMesh> cloneSelection() {
        List<SceneMesh> cloned = new LinkedList<>();
        cloned.addAll(selected);
        return cloned;
    }
}

