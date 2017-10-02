package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.DoubleFunction;

import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.action.ModelScaleAction;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.math.BoundingBox;
import com.ysoft.dctrl.math.Point3DUtils;
import com.ysoft.dctrl.ui.control.NumberField;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

/**
 * Created by pilar on 10.4.2017.
 */

@Controller
public class ScalePanelController extends AbstractEditPanelController {
    @FXML NumberField x;
    @FXML NumberField y;
    @FXML NumberField z;

    @FXML NumberField xSize;
    @FXML NumberField ySize;
    @FXML NumberField zSize;

    @FXML CheckBox uniform;

    @FXML Button toMax;

    private volatile boolean refreshInProgress;

    public ScalePanelController(EditSceneGraph sceneGraph, LocalizationService localizationService, EventBus eventBus, DeeControlContext context) {
        super(sceneGraph, localizationService, eventBus, context);
        refreshInProgress = false;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        DoubleFunction<Boolean> validator = ((v) -> v > 0);

        x.setValidator(validator);
        y.setValidator(validator);
        z.setValidator(validator);
        xSize.setValidator(validator);
        ySize.setValidator(validator);
        zSize.setValidator(validator);

        initNumberFieldListener(xSize, Item.X, this::onSizeChange);
        initNumberFieldListener(ySize, Item.Y, this::onSizeChange);
        initNumberFieldListener(zSize, Item.Z, this::onSizeChange);

        toMax.setOnAction((e) -> {
            sceneGraph.scaleSelectedToMax();
            refresh(sceneGraph.getSelected());
        });
    }

    @Override
    public void refresh(SceneMesh mesh) {
        associate(mesh != null);
        if(mesh == null) { return; }
        refreshInProgress = true;
        Point3D scale = mesh.getScale();
        x.setValue(scale.getX() * 100);
        y.setValue(scale.getY() * 100);
        z.setValue(scale.getZ() * 100);
        BoundingBox bb = mesh.getBoundingBox();
        xSize.setValue(bb.getSize().getX());
        ySize.setValue(bb.getSize().getY());
        zSize.setValue(bb.getSize().getZ());
        refreshInProgress = false;
    }

    private void associate(boolean associated) {
        x.setAssociated(associated);
        y.setAssociated(associated);
        z.setAssociated(associated);
        xSize.setAssociated(associated);
        ySize.setAssociated(associated);
        zSize.setAssociated(associated);
    }

    public void onSizeChange(double newValue, Item item) {
        if(refreshInProgress) { return; }
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }
        switch (item) {
            case X:
                onXSizeChange(mesh, newValue);
                break;
            case Y:
                onYSizeChange(mesh, newValue);
                break;
            case Z:
                onZSizeChange(mesh, newValue);
                break;
        }
    }

    public void onXChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        newValue /= 100;
        if((uniform.isSelected() ? scaleByRatio(mesh, newValue/mesh.getScaleX()) : setScale(mesh, Point3DUtils.setX(mesh.getScale(), newValue)))) {
            refresh(mesh);
        }
    }

    public void onXSizeChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        BoundingBox bb = mesh.getBoundingBox();
        double scale = mesh.getScaleX();
        double def = bb.getSize().getX() / scale;
        double ratio = newValue / def;
        if((uniform.isSelected() ? scaleByRatio(mesh, ratio/mesh.getScaleX()) : setScale(mesh, Point3DUtils.setX(mesh.getScale(), ratio)))) {
            refresh(mesh);
        }
    }

    public void onYChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        newValue /= 100;
        if((uniform.isSelected() ? scaleByRatio(mesh, newValue/mesh.getScaleY()) : setScale(mesh, Point3DUtils.setY(mesh.getScale(), newValue)))) {
            refresh(mesh);
        }
    }

    public void onYSizeChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        BoundingBox bb = mesh.getBoundingBox();
        double scale = mesh.getScaleY();
        double def = bb.getSize().getY() / scale;
        double ratio = newValue / def;
        if((uniform.isSelected() ? scaleByRatio(mesh, ratio/mesh.getScaleY()) : setScale(mesh, Point3DUtils.setY(mesh.getScale(), ratio)))) {
            refresh(mesh);
        }
    }

    public void onZChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        newValue /= 100;
        if((uniform.isSelected() ? scaleByRatio(mesh, newValue/mesh.getScaleZ()) : setScale(mesh, Point3DUtils.setZ(mesh.getScale(), newValue)))) {
            refresh(mesh);
        }
    }

    public void onZSizeChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        BoundingBox bb = mesh.getBoundingBox();
        double scale = mesh.getScaleZ();
        double def = bb.getSize().getZ() / scale;
        double ratio = newValue / def;
        if((uniform.isSelected() ? scaleByRatio(mesh, ratio/mesh.getScaleZ()) : setScale(mesh, Point3DUtils.setZ(mesh.getScale(), ratio)))) {
            refresh(mesh);
        }
    }

    public boolean scaleByRatio(SceneMesh mesh, double ratio) {
        Point3D s = new Point3D(ratio*mesh.getScaleX(), ratio*mesh.getScaleY(), ratio*mesh.getScaleZ());
        return setScale(mesh, s);
    }

    public boolean setScale(SceneMesh mesh, Point3D scale) {
        Point3D oldScale = mesh.getScale();
        if(oldScale.equals(scale)) { return false; }
        mesh.setScale(scale);
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new ModelScaleAction(mesh, oldScale, scale)));
        return true;
    }

    public void onReset() {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }
        mesh.setScale(new Point3D(1,1,1));
        refresh(mesh);
    }
}
