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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Created by pilar on 7.4.2017.
 */

@Controller
public class MovePanelController extends AbstractEditPanelController {
    public MovePanelController(SceneGraph sceneGraph, LocalizationResource localizationResource, EventBus eventBus, DeeControlContext context) {
        super(sceneGraph, localizationResource, eventBus, context);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }

    public void refresh() {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }
        Point3D position = mesh.getPosition();
        x.setText(String.valueOf(position.getX()));
        y.setText(String.valueOf(position.getY()));
    }

    public void onXChange(SceneMesh mesh, String newValue) {
        mesh.setPositionX(Double.valueOf(newValue));
    }

    public void onYChange(SceneMesh mesh, String newValue) {
        mesh.setPositionY(Double.valueOf(newValue));
    }

    public void onZChange(SceneMesh mesh, String newValue) {
        mesh.setPositionZ(Double.valueOf(newValue));
    }

    public void onReset() {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }

        mesh.setPosition(new Point3D(0,0,0));
        refresh();
    }
}
