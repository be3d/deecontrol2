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
import javafx.scene.control.TextField;

/**
 * Created by pilar on 10.4.2017.
 */

@Controller
public class ScalePanelController extends AbstractEditPanelController {
    @FXML TextField x;
    @FXML TextField y;
    @FXML TextField z;

    public ScalePanelController(SceneGraph sceneGraph, LocalizationResource localizationResource, EventBus eventBus, DeeControlContext context) {
        super(sceneGraph, localizationResource, eventBus, context);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.visibleProperty().addListener(this::onVisibleChange);

        x.textProperty().addListener((observable, oldValue, newValue) -> onXChange(newValue));
        y.textProperty().addListener((observable, oldValue, newValue) -> onYChange(newValue));
        z.textProperty().addListener((observable, oldValue, newValue) -> onZChange(newValue));

        super.initialize(location, resources);
    }

    public void onVisibleChange(ObservableValue<? extends Boolean> observable, final boolean oldValue, final boolean newValue) {
        if(!newValue) { return; }
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }
        Point3D scale = mesh.getScale();
        x.setText(String.valueOf(scale.getX()));
        y.setText(String.valueOf(scale.getY()));
        z.setText(String.valueOf(scale.getZ()));
    }

    public void onXChange(String newValue) {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }
        mesh.setScaleX(Double.valueOf(newValue));
    }

    public void onYChange(String newValue) {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }
        mesh.setScaleY(Double.valueOf(newValue));
    }

    public void onZChange(String newValue) {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }
        mesh.setScaleZ(Double.valueOf(newValue));
    }
}
