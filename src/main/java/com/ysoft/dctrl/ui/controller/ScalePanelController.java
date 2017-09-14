package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

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
        initNumberFieldListener(xSize, Item.X, this::onSizeChange);
        initNumberFieldListener(ySize, Item.Y, this::onSizeChange);
        initNumberFieldListener(zSize, Item.Z, this::onSizeChange);

        toMax.setOnAction((e) -> {
            sceneGraph.scaleSelectedToMax();
            refresh();
        });
    }

    public void refresh() {
        refreshInProgress = true;
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }
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

        refresh();
    }

    public void onXChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        newValue /= 100;
        if(uniform.isSelected()) {
            scaleByRatio(mesh, newValue/mesh.getScaleX());
        } else {
            setScale(mesh, Point3DUtils.setX(mesh.getScale(), newValue));
        }
        refresh();
    }

    public void onXSizeChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        BoundingBox bb = mesh.getBoundingBox();
        double scale = mesh.getScaleX();
        double def = bb.getSize().getX() / scale;
        double ratio = newValue / def;
        if(uniform.isSelected()) {
            scaleByRatio(mesh, ratio/mesh.getScaleX());
        } else {
            setScale(mesh, Point3DUtils.setX(mesh.getScale(), ratio));
        }
    }

    public void onYChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        newValue /= 100;
        if(uniform.isSelected()) {
            scaleByRatio(mesh, newValue/mesh.getScaleY());
        } else {
            setScale(mesh, Point3DUtils.setY(mesh.getScale(), newValue));
        }
        refresh();
    }

    public void onYSizeChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        BoundingBox bb = mesh.getBoundingBox();
        double scale = mesh.getScaleY();
        double def = bb.getSize().getY() / scale;
        double ratio = newValue / def;
        if(uniform.isSelected()) {
            scaleByRatio(mesh, ratio/mesh.getScaleY());
        } else {
            setScale(mesh, Point3DUtils.setY(mesh.getScale(), ratio));
        }
    }

    public void onZChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        newValue /= 100;
        if(uniform.isSelected()) {
            scaleByRatio(mesh, newValue/mesh.getScaleZ());
        } else {
            setScale(mesh, Point3DUtils.setZ(mesh.getScale(), newValue));
        }
        refresh();
    }

    public void onZSizeChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        BoundingBox bb = mesh.getBoundingBox();
        double scale = mesh.getScaleZ();
        double def = bb.getSize().getZ() / scale;
        double ratio = newValue / def;
        if(uniform.isSelected()) {
            scaleByRatio(mesh, ratio/mesh.getScaleZ());
        } else {
            setScale(mesh, Point3DUtils.setZ(mesh.getScale(), ratio));
        }
    }

    public void scaleByRatio(SceneMesh mesh, double ratio) {
        Point3D s = new Point3D(ratio*mesh.getScaleX(), ratio*mesh.getScaleY(), ratio*mesh.getScaleZ());
        setScale(mesh, s);
    }

    public void setScale(SceneMesh mesh, Point3D scale) {
        Point3D oldScale = mesh.getScale();
        if(oldScale.equals(scale)) { return; }
        mesh.setScale(scale);
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new ModelScaleAction(mesh, oldScale, scale)));
    }

    public void onReset() {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }

        mesh.setScale(new Point3D(1,1,1));
        refresh();
    }
}
