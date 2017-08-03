package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.geometry.Point3D;

/**
 * Created by pilar on 7.4.2017.
 */

@Controller
public class MovePanelController extends AbstractEditPanelController {
    public MovePanelController(EditSceneGraph sceneGraph, LocalizationService localizationService, EventBus eventBus, DeeControlContext context) {
        super(sceneGraph, localizationService, eventBus, context);
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

    public void onXChange(SceneMesh mesh, double newValue) {
        mesh.setPositionX(newValue);
    }

    public void onYChange(SceneMesh mesh, double newValue) {
        mesh.setPositionY(newValue);
    }

    public void onZChange(SceneMesh mesh, double newValue) {
        mesh.setPositionZ(newValue);
    }

    public void onReset() {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }

        mesh.setPosition(new Point3D(0,0,0));
        refresh();
    }
}
