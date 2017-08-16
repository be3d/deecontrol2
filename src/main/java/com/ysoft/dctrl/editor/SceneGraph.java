package com.ysoft.dctrl.editor;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.control.ExtendedPerspectiveCamera;
import com.ysoft.dctrl.editor.mesh.PrintBed;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;

import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Created by pilar on 6.4.2017.
 */

@Component
public class SceneGraph {
    private static final Point3D PRINTER_SIZE = new Point3D(150,150,150);
    private ExtendedPerspectiveCamera camera;
    private final Group sceneGroup;
    private final Map<SceneMode, SubSceneGraph> subSceneGraphs;
    private SceneMode mode;
    private final EventBus eventBus;

    public static final AmbientLight am = new AmbientLight(getGrayColor(0.3));
    public static final PointLight b = new PointLight(getGrayColor(0.27));
    public static final PointLight f = new PointLight(getGrayColor(0.35));
    public static final PointLight c = new PointLight(getGrayColor(0.3));

    @Autowired
    public SceneGraph(EventBus eventBus, List<SubSceneGraph> subSceneGraphs) {
        this.eventBus = eventBus;
        this.subSceneGraphs = initSubSceneGraphs(subSceneGraphs);
        mode = null;
        sceneGroup = new Group();
        camera = createCamera();
    }

    private Map<SceneMode, SubSceneGraph> initSubSceneGraphs(List<SubSceneGraph> subSceneGraphs) {
        Map<SceneMode, SubSceneGraph> res = new HashMap<>();
        subSceneGraphs.forEach((g) -> {
            SubSceneMode m = g.getClass().getAnnotation(SubSceneMode.class);
            res.put(m.value(), g);
        });
        return res;
    }

    @PostConstruct
    public void init() {
        sceneGroup.getChildren().addAll(camera, createPrintBed().getNode());
        sceneGroup.getChildren().addAll(createLights());
        setMode(SceneMode.EDIT);

        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), (e) -> setMode((SceneMode) e.getData()));
    }

    private ExtendedPerspectiveCamera createCamera() {
        ExtendedPerspectiveCamera camera = new ExtendedPerspectiveCamera(true);
        camera.setInitialTransforms(new Rotate(-90, Rotate.X_AXIS));
        camera.setFarClip(10000);
        return camera;
    }

    private PrintBed createPrintBed() {
        return new PrintBed((float) PRINTER_SIZE.getX(),(float) PRINTER_SIZE.getY());
    }

    private Node[] createLights() {
        b.getTransforms().add(new Translate(-250,250,10));
        f.getTransforms().add(new Translate(10000,-10000,8000));
        c.getTransforms().add(camera.getTransforms().get(0));
        return new Node[] {am, b, f, c};
    }

    private static Color getGrayColor(double value) {
        return new Color(value, value, value, 1);
    }

    public void setMode(SceneMode mode) {
        if(this.mode != null) { sceneGroup.getChildren().remove(subSceneGraphs.get(this.mode).getSceneGroup()); }
        this.mode = mode;
        sceneGroup.getChildren().add(0, subSceneGraphs.get(this.mode).getSceneGroup());
    }

    public ExtendedPerspectiveCamera getCamera() {
        return camera;
    }

    public Parent getSceneGroup() {
        return sceneGroup;
    }
}
