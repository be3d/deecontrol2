package com.ysoft.dctrl.editor.action;

import com.ysoft.dctrl.action.Action;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.math.TransformMatrix;

import javafx.geometry.Point3D;

/**
 * Created by pilar on 8.9.2017.
 */
public class ModelRotationAction implements TransformAction {
    private final SceneMesh mesh;
    private final Point3D axis;
    private final double angle;

    public ModelRotationAction(SceneMesh mesh, Point3D axis, double angle) {
        this.mesh = mesh;
        this.axis = axis;
        this.angle = angle;
    }

    @Override
    public void undo() {
        rotate(-angle);
    }

    @Override
    public void redo() {
        rotate(angle);
    }

    private void rotate(double angle) {
        Point3D rotation = TransformMatrix.getRotationAxis(axis, angle).multiply(TransformMatrix.fromEulerDeg(mesh.getRotation())).toEulerDeg();
        mesh.setRotation(rotation);
    }
}
