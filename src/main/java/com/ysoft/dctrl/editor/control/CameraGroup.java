package com.ysoft.dctrl.editor.control;

import com.ysoft.dctrl.math.Point3DUtils;
import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kuhn on 12/19/2017.
 */
public class CameraGroup extends Group{

    private static final CameraType DEFAULT_CAMERA_TYPE = CameraType.PERSPECTIVE;

    private static final double FOV_PERSPECTIVE = 30;
    private static final double FOV_PARALLEL = 0.1;
    private static final double FOV_PARALLEL_COEF =
            (Math.tan(0.5*Math.toRadians(FOV_PERSPECTIVE))/Math.tan(0.5*Math.toRadians(FOV_PARALLEL)));

    private Rotate rotationX;
    private Rotate rotationY;
    private Rotate rotationZ;
    private Translate positionTransform;
    private Translate parallelPositionTransform;

    private Point3D position;
    private Point3D target;

    HashMap<CameraType, Camera> cameras;
    Camera selected;

    public CameraGroup(){
        cameras = new HashMap<>();

        PerspectiveCamera perspectiveCamera = new PerspectiveCamera(true);
        perspectiveCamera.setFieldOfView(30);
        perspectiveCamera.setNearClip(5);
        perspectiveCamera.setFarClip(10000);

        PerspectiveCamera parallelCamera = new PerspectiveCamera(true);
        parallelCamera.setFieldOfView(0.1);
        parallelCamera.setNearClip(15000);
        parallelCamera.setFarClip(1000000);

        positionTransform = new Translate(0,0,0);
        parallelPositionTransform = new Translate(0,0,0);
        rotationX = new Rotate(0, Rotate.X_AXIS);
        rotationY = new Rotate(0, Rotate.Y_AXIS);
        rotationZ = new Rotate(0, Rotate.Z_AXIS);


        target = new Point3D(0,0,0);
        position = new Point3D(0,0,0);

        cameras.put(CameraType.PERSPECTIVE, perspectiveCamera);
        cameras.put(CameraType.PARALLEL, parallelCamera);

        selected = cameras.get(DEFAULT_CAMERA_TYPE);
    }

    public Camera select(CameraType cameraType){
        selected = cameras.get(cameraType);
        return selected;
    }

    public Camera getSelected(){
        return selected;
    }

    public void setInitialTransforms(Transform... transforms) {
        setInitialTransforms(Arrays.asList(transforms));
    }

    public void setInitialTransforms(Collection<Transform> transforms) {
        ObservableList<Transform> t = cameras.get(CameraType.PERSPECTIVE).getTransforms();
        t.clear();
        t.addAll(positionTransform);
        t.addAll(transforms);
        t.addAll(rotationY, rotationX, rotationZ);

        ObservableList<Transform> tp = cameras.get(CameraType.PARALLEL).getTransforms();
        tp.clear();
        tp.addAll(parallelPositionTransform);
        tp.addAll(transforms);
        tp.addAll(rotationY, rotationX, rotationZ);
    }

    public void setPosition(double x, double y, double z) {
        setPosition(new Point3D(x,y,z));
    }

    public void setPosition(Point3D point) {
        position = point;

        positionTransform.setX(point.getX());
        positionTransform.setY(point.getY());
        positionTransform.setZ(point.getZ());

        // Parallel camera is moved away so the perspective flattens
        double d = position.distance(target);
        double diff = d*FOV_PARALLEL_COEF - d;
        Point3D newPos = position.add(getLookAtVector().multiply(diff));

        parallelPositionTransform.setX(newPos.getX());
        parallelPositionTransform.setY(newPos.getY());
        parallelPositionTransform.setZ(newPos.getZ());
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

    public Point3D getTarget() {
        return this.target;
    }

    public void setTarget(Point3D target) {
        this.target = target;
    }

    public Point3D getLookAtVector() {
        Point3D normal = Point3DUtils.copy(position);
        normal = normal.subtract(target);
        normal = normal.normalize();
        return normal;
    }

    public Camera getActiveCamera(){
        return selected;
    }
}
