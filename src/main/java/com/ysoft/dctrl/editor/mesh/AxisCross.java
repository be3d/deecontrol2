package com.ysoft.dctrl.editor.mesh;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.control.ExtendedPerspectiveCamera;
import com.ysoft.dctrl.editor.mesh.shape.Arrow;
import com.ysoft.dctrl.utils.ColorUtils;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;

@Component
public class AxisCross {
    private static final double CAMERA_DISTANCE = 300;
    private static final int ARROW_DIVISIONS = 32;
    private static final double ARROW_LEG_LENGTH = 50;
    private static final double ARROW_LEG_RADIUS = 1.5;
    private static final double ARROW_HEAD_LENGTH = 20;
    private static final double ARROW_HEAD_RADIUS = 10;

    private enum Axis {X, Y, Z}

    private Group group;
    private ExtendedPerspectiveCamera camera;
    private EnumMap<Axis, Consumer<Point2D>> arrowPositionHandlers;

    public AxisCross() {
        this.group = new Group();
        camera = new ExtendedPerspectiveCamera(true);
        arrowPositionHandlers = new EnumMap<>(Axis.class);
    }

    public void init(Pane node, Consumer<Point2D> xHandler, Consumer<Point2D> yHandler, Consumer<Point2D> zHandler) {
        camera.setInitialTransforms(new Rotate(-90, Rotate.X_AXIS));
        camera.setFarClip(10000);

        SubScene subScene = new SubScene(group, 10, 10, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.TRANSPARENT);
        subScene.setCamera(camera);
        group.getChildren().add(camera);

        node.getChildren().add(0, subScene);
        node.heightProperty().addListener((ob, o, n) -> {
            subScene.setHeight(n.doubleValue());
            handleArrowPositions();
        });
        node.widthProperty().addListener((ob, o, n) -> {
            subScene.setWidth(n.doubleValue());
            handleArrowPositions();
        });

        group.getChildren().addAll(
                createAxisArrow(new Rotate(90, new Point3D(0,1,0)), "#f9464b"),
                createAxisArrow(new Rotate(90, new Point3D(-1,0,0)), "#53c833"),
                createAxisArrow(new Rotate(0, Point3D.ZERO), "#1fc0ee")
        );

        arrowPositionHandlers.put(Axis.X, xHandler);
        arrowPositionHandlers.put(Axis.Y, yHandler);
        arrowPositionHandlers.put(Axis.Z, zHandler);
        handleArrowPositions();
    }

    public void setRefCamera(ExtendedPerspectiveCamera refCamera) {
        refCamera.setRotationChangeListener((e) -> updateCamera(refCamera));
        updateCamera(refCamera);
    }

    private void updateCamera(ExtendedPerspectiveCamera refCamera) {
        camera.setRotation(refCamera.getRotation());
        setCameraPosition();
        handleArrowPositions();
    }

    private void setCameraPosition() {
        Point3D cameraRotation = camera.getRotation();
        double theta = -Math.toRadians(cameraRotation.getX());
        double alpha = Math.toRadians(cameraRotation.getY() - 180);

        double z = Math.sin(theta) * CAMERA_DISTANCE;
        double xyDist = Math.cos(theta) * CAMERA_DISTANCE;
        double x = Math.sin(alpha) * xyDist;
        double y = Math.cos(alpha) * xyDist;

        camera.setPosition(x, y, z);
    }

    private Group createAxisArrow(Rotate rotation, String webColor) {
        Group arrowGroup = new Group();
        Arrow arrow = new Arrow(ARROW_DIVISIONS, ARROW_LEG_LENGTH, ARROW_LEG_RADIUS, ARROW_HEAD_LENGTH, ARROW_HEAD_RADIUS);
        arrow.setMaterial(getArrowMaterial(webColor));
        arrow.getTransforms().addAll(rotation);

        arrowGroup.getChildren().addAll(arrow.getView());

        return arrowGroup;
    }

    private Material getArrowMaterial(String webColor) {
        PhongMaterial material = new PhongMaterial();

        material.setSpecularColor(Color.BLACK);
        material.setDiffuseMap(ColorUtils.getColorImage("#000000"));
        material.setSelfIlluminationMap(ColorUtils.getColorImage(webColor));

        return material;
    }

    private void handleArrowPositions() {
        if(arrowPositionHandlers.get(Axis.X) != null) { arrowPositionHandlers.get(Axis.X).accept(computeArrowPositon(Axis.X)); }
        if(arrowPositionHandlers.get(Axis.Y) != null) { arrowPositionHandlers.get(Axis.Y).accept(computeArrowPositon(Axis.Y)); }
        if(arrowPositionHandlers.get(Axis.Z) != null) { arrowPositionHandlers.get(Axis.Z).accept(computeArrowPositon(Axis.Z)); }
    }

    private Point2D computeArrowPositon(Axis axis) {
        Point3D cameraRotation = camera.getRotation();
        double theta = -Math.toRadians(cameraRotation.getX());
        double alpha = Math.toRadians(cameraRotation.getY() - 180);

        double a = 0.5;
        double x;
        double y;

        if(axis == null) { return Point2D.ZERO; }
        switch (axis) {
            case X:
                x = a - Math.cos(alpha) * a;
                y = a + Math.sin(theta) * a * Math.sin(alpha);
                break;
            case Y:
                x = a + Math.sin(alpha) * a;
                y = a + Math.sin(theta) * a * Math.cos(alpha);
                break;
            case Z:
                x = a;
                y = a - Math.abs(Math.cos(theta)) * a;
                break;
            default:
                x = 0;
                y = 0;
                break;
        }

        return new Point2D(x,y);
    }
}
