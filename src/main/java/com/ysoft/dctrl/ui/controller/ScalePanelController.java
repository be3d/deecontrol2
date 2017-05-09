package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

/**
 * Created by pilar on 10.4.2017.
 */

@Controller
public class ScalePanelController extends AbstractEditPanelController {
    @FXML TextField x;
    @FXML TextField y;
    @FXML TextField z;

    @FXML CheckBox uniform;

    public ScalePanelController(SceneGraph sceneGraph, LocalizationResource localizationResource, EventBus eventBus, DeeControlContext context) {
        super(sceneGraph, localizationResource, eventBus, context);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    public void refresh() {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }
        Point3D scale = mesh.getScale();
        x.setText(String.valueOf(scale.getX()));
        y.setText(String.valueOf(scale.getY()));
        z.setText(String.valueOf(scale.getZ()));
    }

    public void onXChange(SceneMesh mesh, String newValue) {
        double val = Double.valueOf(newValue);
        if(uniform.isSelected()) {
            scaleByRatio(mesh, val/mesh.getScaleX());
        } else {
            mesh.setScaleX(val);
        }
    }

    public void onYChange(SceneMesh mesh, String newValue) {
        double val = Double.valueOf(newValue);
        if(uniform.isSelected()) {
            scaleByRatio(mesh, val/mesh.getScaleY());
        } else {
            mesh.setScaleY(val);
        }
    }

    public void onZChange(SceneMesh mesh, String newValue) {
        double val = Double.valueOf(newValue);
        if(uniform.isSelected()) {
            scaleByRatio(mesh, val/mesh.getScaleZ());
        } else {
            mesh.setScaleZ(val);
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
