package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.math.TransformMatrix;
import com.ysoft.dctrl.ui.control.NumberField;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.geometry.Point3D;
import javafx.scene.control.Button;

/**
 * Created by pilar on 6.4.2017.
 */

@Controller
public class RotationPanelController extends AbstractEditPanelController {
    private static final Point3D X_AXIS = new Point3D(1,0,0);
    private static final Point3D Y_AXIS = new Point3D(0,1,0);
    private static final Point3D Z_AXIS = new Point3D(0,0,1);

    @FXML NumberField x;
    @FXML NumberField y;
    @FXML NumberField z;

    @FXML Button counterX;
    @FXML Button clockX;
    @FXML Button counterY;
    @FXML Button clockY;
    @FXML Button counterZ;
    @FXML Button clockZ;

    public RotationPanelController(EditSceneGraph sceneGraph, LocalizationService localizationService, EventBus eventBus, DeeControlContext context) {
        super(sceneGraph, localizationService, eventBus, context);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        counterX.setOnAction((e) -> rotate(X_AXIS, x.getValue()));
        clockX.setOnAction((e) -> rotate(X_AXIS, -x.getValue()));
        counterY.setOnAction((e) -> rotate(Y_AXIS, y.getValue()));
        clockY.setOnAction((e) -> rotate(Y_AXIS, -y.getValue()));
        counterZ.setOnAction((e) -> rotate(Z_AXIS, z.getValue()));
        clockZ.setOnAction((e) -> rotate(Z_AXIS, -z.getValue()));
    }

    public void refresh() {
        x.setValue(90);
        y.setValue(90);
        z.setValue(90);
    }

    public void onXChange(SceneMesh mesh, double newValue) {}

    public void onYChange(SceneMesh mesh, double newValue) {}

    public void onZChange(SceneMesh mesh, double newValue) {}

    public void rotate(Point3D axis, double angle) {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }
        angle = Math.toRadians(angle);
        Point3D rotation = TransformMatrix.getRotationAxis(axis, angle).multiply(TransformMatrix.fromEuler(getRadRotation(mesh.getRotation()))).toEuler();

        mesh.setRotation(getDegRotation(rotation));
    }

    public Point3D getDegRotation(Point3D rotation) {
        return new Point3D(Math.toDegrees(rotation.getX()), Math.toDegrees(rotation.getY()), Math.toDegrees(rotation.getZ()));
    }

    public Point3D getRadRotation(Point3D rotation) {
        return new Point3D(Math.toRadians(rotation.getX()), Math.toRadians(rotation.getY()), Math.toRadians(rotation.getZ()));
    }

    public void onReset() {
        SceneMesh mesh = sceneGraph.getSelected();
        if(mesh == null) { return; }

        mesh.setRotation(new Point3D(0,0,0));
        refresh();
    }
}
