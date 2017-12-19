package com.ysoft.dctrl.editor.control;

import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kuhn on 12/19/2017.
 */
public class CameraGroup extends Group{

    HashMap<CameraType, Camera> cameras;
    Camera selected;

    private Rotate rotationX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotationY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotationZ = new Rotate(0, Rotate.Z_AXIS);
    private Translate position = new Translate(0,0,0);

    public CameraGroup(PerspectiveCamera perspectiveCamera, ParallelCamera parallelCamera){
        cameras = new HashMap<>();

        if(perspectiveCamera != null){
            cameras.put(CameraType.PERSPECTIVE, perspectiveCamera);
            perspectiveCamera.getTransforms().addAll(position, rotationY, rotationX, rotationZ);
        }
        if(parallelCamera != null){
            cameras.put(CameraType.PARALLEL, parallelCamera);
            parallelCamera.getTransforms().addAll(position, rotationY, rotationX, rotationZ);
        }
    }

    public CameraGroup(PerspectiveCamera camera){ this(camera, null); }
    public CameraGroup(ParallelCamera camera){ this(null, camera); }

    public void select(CameraType cameraType){
        selected = cameras.get(cameraType);
    }
    public Camera getSelected(){
        return selected;
    }

    public void setInitialTransforms(Transform... transforms) {
        setInitialTransforms(Arrays.asList(transforms));
    }

    public void setInitialTransforms(Collection<Transform> transforms) {
        ObservableList<Transform> t = cameras.get(CameraType.PARALLEL).getTransforms();
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
}
