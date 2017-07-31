package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.math.BoundingBox;
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
        xSize.textProperty().addListener((observable, oldValue, newValue) -> onSizeChange(this::onXSizeChange, xSize.getValue()));
        ySize.textProperty().addListener((observable, oldValue, newValue) -> onSizeChange(this::onYSizeChange, ySize.getValue()));
        zSize.textProperty().addListener((observable, oldValue, newValue) -> onSizeChange(this::onZSizeChange, zSize.getValue()));

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

    public void onSizeChange(BiConsumer<SceneMesh, Double> consumer, double newValue) {
        if(refreshInProgress) { return; }
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }
        consumer.accept(mesh, newValue);

        refresh();
    }

    public void onXChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        newValue /= 100;
        if(uniform.isSelected()) {
            scaleByRatio(mesh, newValue/mesh.getScaleX());
        } else {
            mesh.setScaleX(newValue);
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
            mesh.setScaleX(ratio);
        }
    }

    public void onYChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        newValue /= 100;
        if(uniform.isSelected()) {
            scaleByRatio(mesh, newValue/mesh.getScaleY());
        } else {
            mesh.setScaleY(newValue);
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
            mesh.setScaleY(ratio);
        }
    }

    public void onZChange(SceneMesh mesh, double newValue) {
        if(refreshInProgress) { return; }
        newValue /= 100;
        if(uniform.isSelected()) {
            scaleByRatio(mesh, newValue/mesh.getScaleZ());
        } else {
            mesh.setScaleZ(newValue);
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
            mesh.setScaleZ(ratio);
        }
    }

    public void scaleByRatio(SceneMesh mesh, double ratio) {
        mesh.setScaleX(ratio*mesh.getScaleX());
        mesh.setScaleY(ratio*mesh.getScaleY());
        mesh.setScaleZ(ratio*mesh.getScaleZ());
        refresh();
    }

    public void onReset() {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }

        mesh.setScale(new Point3D(1,1,1));
        refresh();
    }
}
