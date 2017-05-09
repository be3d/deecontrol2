package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.math.Matrix3D;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.control.TextField;

/**
 * Created by pilar on 6.4.2017.
 */

@Controller
public class RotationPanelController extends AbstractEditPanelController {
    @FXML TextField x;
    @FXML TextField y;
    @FXML TextField z;

    public RotationPanelController(SceneGraph sceneGraph, LocalizationResource localizationResource, EventBus eventBus, DeeControlContext context) {
        super(sceneGraph, localizationResource, eventBus, context);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    public void refresh() {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }
        Point3D rotation = mesh.getRotation();
        x.setText(String.valueOf(rotation.getX()));
        y.setText(String.valueOf(rotation.getY()));
        z.setText(String.valueOf(rotation.getZ()));
    }

    public void onXChange(SceneMesh mesh, String newValue) {
        mesh.setRotationX(Double.valueOf(newValue));
    }

    public void onYChange(SceneMesh mesh, String newValue) {
        mesh.setRotationY(Double.valueOf(newValue));
    }

    public void onZChange(SceneMesh mesh, String newValue) {
        mesh.setRotationZ(Double.valueOf(newValue));
    }

    public void onReset() {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }

        mesh.setRotation(new Point3D(0,0,0));
        refresh();
    }
}
