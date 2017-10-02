package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.action.ModelTranslateAction;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.math.Point3DUtils;
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

    @Override
    public void refresh(SceneMesh mesh) {
        associate(mesh != null);
        if(mesh == null) { return; }
        Point3D position = mesh.getPosition();
        x.setValue(position.getX());
        y.setValue(position.getY());
    }

    private void associate(boolean associated) {
        x.setAssociated(associated);
        y.setAssociated(associated);
        z.setAssociated(associated);
    }

    public void onXChange(SceneMesh mesh, double newValue) {
        setPosition(mesh, Point3DUtils.setX(mesh.getPosition(), newValue));
    }

    public void onYChange(SceneMesh mesh, double newValue) {
        setPosition(mesh, Point3DUtils.setY(mesh.getPosition(), newValue));
    }

    public void onZChange(SceneMesh mesh, double newValue) {
        setPosition(mesh, Point3DUtils.setZ(mesh.getPosition(), newValue));
    }

    private void setPosition(SceneMesh mesh, Point3D position) {
        Point3D oldPosition = mesh.getPosition();
        if(oldPosition.equals(position)) { return; }
        mesh.setPosition(position);
        eventBus.publish(new Event(EventType.ADD_ACTION.name(), new ModelTranslateAction(mesh,oldPosition, position)));
    }

    public void onReset() {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }

        mesh.setPosition(new Point3D(0,0,0));
        refresh(mesh);
    }
}
