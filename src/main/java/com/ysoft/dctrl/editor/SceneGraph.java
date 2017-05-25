package com.ysoft.dctrl.editor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import javafx.scene.shape.Sphere;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.control.ExtendedPerspectiveCamera;
import com.ysoft.dctrl.editor.mesh.ExtendedMesh;
import com.ysoft.dctrl.editor.mesh.MeshUtils;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Created by pilar on 6.4.2017.
 */

@Component
public class SceneGraph {
    private LinkedList<SceneMesh> sceneMeshes;
    private ExtendedPerspectiveCamera camera;

    private Group sceneGroup;

    private PhongMaterial material;
    private PhongMaterial selectedMaterial;

    private SceneMesh selected;

    private final EventBus eventBus;

    public SceneGraph(EventBus eventBus) {
        this.eventBus = eventBus;
        sceneGroup = new Group();
        sceneMeshes = new LinkedList<>();
        material = new PhongMaterial(Color.LIGHTBLUE);
        selectedMaterial = new PhongMaterial(new Color(0.3f, 0.4f, 0.9019608f, 1));
        camera = createCamera();
        Box box = new Box(0.2,0.2,0.2);
        box.setMaterial(new PhongMaterial(Color.RED));



//        Point3D pointA = new Point3D(0,0,0);
//        Point3D pointB = new Point3D(10,0,0);
//        Point3D pointC = new Point3D(15,5,0);
//        Point3D pointD = new Point3D(20,-5,0);
//        Point3D pointE = new Point3D(25,8,0);
//

        Sphere p1 = new Sphere(0.5);
        p1.setMaterial(new PhongMaterial(Color.BLUE));
        p1.setTranslateX(10);

        Sphere p2 = new Sphere(0.5);
        p2.setMaterial(new PhongMaterial(Color.BLUE));
        p2.setTranslateX(15);
        p2.setTranslateY(5);

        Sphere p3 = new Sphere(0.5);
        p3.setMaterial(new PhongMaterial(Color.BLUE));
        p3.setTranslateX(20);
        p3.setTranslateY(-5);


        Sphere p4 = new Sphere(0.5);
        p4.setMaterial(new PhongMaterial(Color.BLUE));
        p4.setTranslateX(25);
        p4.setTranslateY(8);


        Sphere a0 = new Sphere(0.5);
        a0.setMaterial(new PhongMaterial(Color.GREEN));
        a0.setTranslateX(15);
        a0.setTranslateY(5);
        a0.setTranslateZ(2);

        Sphere a1 = new Sphere(0.5);
        a1.setMaterial(new PhongMaterial(Color.GREEN));
        a1.setTranslateX(14.6);
        a1.setTranslateY(3);
        a1.setTranslateZ(0);

        Sphere a2 = new Sphere(0.5);
        a2.setMaterial(new PhongMaterial(Color.GREEN));
        a2.setTranslateX(15);
        a2.setTranslateY(5);
        a2.setTranslateZ(-2);

        Sphere a3 = new Sphere(0.5);
        a3.setMaterial(new PhongMaterial(Color.GREEN));
        a3.setTranslateX(15.3);
        a3.setTranslateY(6.97);
        a3.setTranslateZ(0);

        Sphere a4 = new Sphere(0.5);
        a4.setMaterial(new PhongMaterial(Color.GREEN));
        a4.setTranslateX(20);
        a4.setTranslateY(-5);
        a4.setTranslateZ(2);

        Sphere a5 = new Sphere(0.5);
        a5.setMaterial(new PhongMaterial(Color.GREEN));
        a5.setTranslateX(18.5);
        a5.setTranslateY(-5.4);
        a5.setTranslateZ(0);

        Sphere a6 = new Sphere(0.5);
        a6.setMaterial(new PhongMaterial(Color.GREEN));
        a6.setTranslateX(20);
        a6.setTranslateY(-5);
        a6.setTranslateZ(-2);

        Sphere a7 = new Sphere(0.5);
        a7.setMaterial(new PhongMaterial(Color.GREEN));
        a7.setTranslateX(21.9);
        a7.setTranslateY(-4.5);
        a7.setTranslateZ(0);

        //sceneGroup.getChildren().addAll(camera, createPrintBed(), box);
        sceneGroup.getChildren().addAll(camera, box,p1,p2,p3,p4,a0,a1,a2,a3,a4,a5,a6,a7);
        selected = null;

        eventBus.subscribe(EventType.MODEL_LOADED.name(), (e) -> addMesh((TriangleMesh) e.getData()));
        eventBus.subscribe(EventType.CENTER_SELECTED_MODEL.name(), (e) -> centerSelected());
    }

    private ExtendedPerspectiveCamera createCamera() {
        ExtendedPerspectiveCamera camera = new ExtendedPerspectiveCamera(true);
        camera.setInitialTransforms(new Rotate(-90, Rotate.X_AXIS));
        camera.setFarClip(10000);
        return camera;
    }

    private Box createPrintBed() {
        Box bed = new Box(150, 150, 4);
        //bed.setMaterial(new PhongMaterial(Color.GRAY));
        bed.setMaterial(new PhongMaterial(new Color(0.100f,0.100f,0.100f, 0.1)));
        bed.getTransforms().addAll(new Translate(0,0,-bed.getDepth()/2));
        return bed;
    }

    public ExtendedPerspectiveCamera getCamera() {
        return camera;
    }

    public Group getSceneGroup() {
        return sceneGroup;
    }

    public LinkedList<SceneMesh> getSceneMeshses() {
        return sceneMeshes;
    }

    public LinkedList<SceneMesh> getSceneMeshes() { return sceneMeshes; }

    public void centerSelected() {
        if(selected == null) { return; }
        selected.setPosition(new Point2D(0,0));
    }

    public void addMesh(TriangleMesh mesh) {
        ExtendedMesh extendedMesh = new ExtendedMesh(mesh);
        extendedMesh.setMaterial(material);
        extendedMesh.translateToZero();
        extendedMesh.setPositionZ(extendedMesh.getBoundingBox().getHalfSize().getZ());
        sceneMeshes.add(extendedMesh);
        sceneGroup.getChildren().add(extendedMesh.getNode());
        extendedMesh.getNode().setOnMouseClicked((event -> {
            if(event.getTarget() != extendedMesh.getNode()) { return; }
            selectNew(extendedMesh);
        }));
    }

    public void deleteSelected() {
        if(selected == null) { return; }

        sceneMeshes.remove(selected);
        sceneGroup.getChildren().remove(selected.getNode());

        selected = null;
        selectNext();
    }

    public void selectNext() {
        if(selected == null) {
            selectNew(sceneMeshes.getFirst());
        } else {
            int next = sceneMeshes.indexOf(selected) + 1;
            if(next > sceneMeshes.size() - 1) { next = 0; }
            selectNew(sceneMeshes.get(next));
        }
    }

    public void selectPrevious() {
        if(selected == null) {
            selectNew(sceneMeshes.getLast());
        } else {
            int prev = sceneMeshes.indexOf(selected) - 1;
            if(prev < 0) { prev = sceneMeshes.size() -1; }
            selectNew(sceneMeshes.get(prev));
        }
    }

    private void selectNew(SceneMesh mesh) {
        if(selected != null) { selected.setMaterial(material); }
        selected = mesh;
        selected.setMaterial(selectedMaterial);
        eventBus.publish(new Event(EventType.MODEL_SELECTED.name(), selected));
    }

    public SceneMesh getSelected() {
        return selected;
    }
}
