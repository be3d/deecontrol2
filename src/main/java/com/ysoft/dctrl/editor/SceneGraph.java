package com.ysoft.dctrl.editor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.ysoft.dctrl.editor.control.CameraGroup;
import com.ysoft.dctrl.editor.control.CameraType;
import javafx.scene.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.mesh.PrintBed;
import com.ysoft.dctrl.editor.mesh.PrinterVolume;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.math.BoundingBox;
import com.ysoft.dctrl.math.Point3DUtils;
import com.ysoft.dctrl.utils.ColorUtils;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Created by pilar on 6.4.2017.
 */

@Component
public class SceneGraph {
    private static final Point3D PRINTER_SIZE = new Point3D(150,150,150);
    private static final Point3D PRINTER_HALF_SIZE = new Point3D(75,75,75);
    private CameraGroup cameraGroup;

    private final Group sceneGroup;
    private final Group helpGroup;
    private final Group subSceneGroup;
    private final Map<SceneMode, SubSceneGraph> subSceneGraphs;
    private SceneMode mode;
    private final EventBus eventBus;
    private final PrinterVolume printVolume;

    public static final AmbientLight am = new AmbientLight(getGrayColor(0.3));
    public static final PointLight b = new PointLight(getGrayColor(0.27));
    public static final PointLight f = new PointLight(getGrayColor(0.35));
    public static final PointLight c = new PointLight(getGrayColor(0.3));

    @Autowired
    public SceneGraph(EventBus eventBus, List<SubSceneGraph> subSceneGraphs) {
        this.eventBus = eventBus;
        printVolume = new PrinterVolume();
        printVolume.set(Point3DUtils.setZ(Point3DUtils.copy(PRINTER_HALF_SIZE).multiply(-1),0), Point3DUtils.setZ(Point3DUtils.copy(PRINTER_HALF_SIZE), PRINTER_SIZE.getZ()));
        mode = null;
        sceneGroup = new Group();
        helpGroup = new Group();
        subSceneGroup = new Group();
        sceneGroup.getChildren().addAll(subSceneGroup, helpGroup);
        cameraGroup = createCameraGroup();

        this.subSceneGraphs = initSubSceneGraphs(subSceneGraphs);
    }

    private Map<SceneMode, SubSceneGraph> initSubSceneGraphs(List<SubSceneGraph> subSceneGraphs) {
        Map<SceneMode, SubSceneGraph> res = new HashMap<>();
        subSceneGraphs.forEach((g) -> {
            SubSceneMode m = g.getClass().getAnnotation(SubSceneMode.class);
            res.put(m.value(), g);
            g.setPrinterVolume(printVolume);
        });
        return res;
    }

    @PostConstruct
    public void init() {
        helpGroup.getChildren().addAll(cameraGroup.getSelected(), printVolume.getNode(), createPrintBed().getNode());
        helpGroup.getChildren().addAll(createLights());
        helpGroup.setVisible(false);
        setMode(SceneMode.EDIT);

        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), (e) -> setMode((SceneMode) e.getData()));
        eventBus.subscribe(EventType.EDIT_SCENE_VALID.name(), (e) -> printVolume.setDefaultColor());
        eventBus.subscribe(EventType.EDIT_SCENE_INVALID.name(), (e) -> printVolume.setInvalidColor());
        eventBus.subscribeOnce(EventType.MODEL_LOADED.name(), (e) -> helpGroup.setVisible(true));
    }

    private CameraGroup createCameraGroup() {
        CameraGroup cameraGroup = new CameraGroup();
        cameraGroup.setInitialTransforms(new Rotate(-90, Rotate.X_AXIS));
        return cameraGroup;
    }

    private PrintBed createPrintBed() {
        return new PrintBed((float) PRINTER_SIZE.getX(),(float) PRINTER_SIZE.getY());
    }

    private Node[] createLights() {
        b.getTransforms().add(new Translate(-250,250,10));
        f.getTransforms().add(new Translate(10000,-10000,8000));
        c.getTransforms().add(cameraGroup.getSelected().getTransforms().get(0));
        return new Node[] {am, b, f, c};
    }

    private static Color getGrayColor(double value) {
        return new Color(value, value, value, 1);
    }

    public void setMode(SceneMode mode) {
        if(this.mode != null) {
            subSceneGroup.getChildren().remove(subSceneGraphs.get(this.mode).getSceneGroup());
            helpGroup.getChildren().remove(subSceneGraphs.get(this.mode).getHelpGroup());
        }
        this.mode = mode;
        subSceneGroup.getChildren().add(0, subSceneGraphs.get(this.mode).getSceneGroup());
        helpGroup.getChildren().add(subSceneGraphs.get(this.mode).getHelpGroup());
    }

    public CameraGroup getCameraGroup(){
        return cameraGroup;
    }

    public Parent getSceneGroup() {
        return sceneGroup;
    }

    public void addHelpObject(Shape3D object) {
        helpGroup.getChildren().add(object);
    }

    public void setSubSceneMouseTransparent(boolean transparent) {
        subSceneGroup.setMouseTransparent(transparent);
    }
}