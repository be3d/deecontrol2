package com.ysoft.dctrl.editor.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.TransformChangedEvent;
import javafx.scene.transform.Translate;

/**
 * Created by pilar on 25.3.2017.
 */
public class ExtendedPerspectiveCamera extends PerspectiveCamera {
    private Rotate rotationX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotationY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotationZ = new Rotate(0, Rotate.Z_AXIS);
    private Translate position = new Translate(0,0,0);

    public ExtendedPerspectiveCamera() {
        this(false);
    }

    public ExtendedPerspectiveCamera(boolean fixedEyeAtCameraZero) {
        super(fixedEyeAtCameraZero);
        getTransforms().addAll(position, rotationY, rotationX, rotationZ);
    }

    private Collection<Transform> getExtendedTransforms() {
        Collection<Transform> t = new ArrayList<>();
        t.add(position);
        t.add(rotationX);
        t.add(rotationY);
        t.add(rotationZ);
        return t;
    }

    public void setInitialTransforms(Transform... transforms) {
        setInitialTransforms(Arrays.asList(transforms));
    }

    public void setInitialTransforms(Collection<Transform> transforms) {
        ObservableList<Transform> t = getTransforms();
        t.clear();
        t.addAll(position);
        t.addAll(transforms);
        t.addAll(rotationY, rotationX, rotationZ);
    }

    public void setPosition(double x, double y, double z) {
        setPosition(new Point3D(x,y,z));
    }

    public void setPosition(Point3D point) {
        position.setX(point.getX());
        position.setY(point.getY());
        position.setZ(point.getZ());
    }

    public void setPositionX(double x) {
        position.setX(x);
    }

    public void setPositionY(double y) {
        position.setY(y);
    }

    public void setPositionZ(double z) {
        position.setZ(z);
    }

    public Point3D getPosition() {
        return new Point3D(position.getX(), position.getY(), position.getZ());
    }

    public void setRotation(double x, double y, double z) {
        setRotationX(x);
        setRotationY(y);
        setRotationZ(z);
    }

    public void setRotation(Point3D rotation) {
        setRotation(rotation.getX(), rotation.getY(), rotation.getZ());
    }

    public void setRotationX(double rotationX) {
        this.rotationX.setAngle(rotationX);
    }

    public void setRotationY(double rotationY) {
        this.rotationY.setAngle(rotationY);
    }

    public void setRotationZ(double rotationZ) {
        this.rotationZ.setAngle(rotationZ);
    }

    public Point3D getRotation() {
        return new Point3D(rotationX.getAngle(), rotationY.getAngle(), rotationZ.getAngle());
    }

    public void setRotationChangeListener(EventHandler<TransformChangedEvent> handler) {
        rotationX.setOnTransformChanged(handler);
        rotationY.setOnTransformChanged(handler);
        rotationZ.setOnTransformChanged(handler);
    }
}
