package com.ysoft.dctrl.safeq.job;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.control.ExtendedPerspectiveCamera;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.math.BoundingBox;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;

@Component
public class SnapShotCreator {
    private final Logger logger = LogManager.getLogger(SnapShotCreator.class);

    private static final Material MATERIAL = new PhongMaterial(Color.web("#cccccc"));

    private VBox canvas;
    private Group sceneGroup;
    private ExtendedPerspectiveCamera camera;
    private BoundingBox boundingBox;
    private SnapshotParameters snapshotParameters;
    private final EditSceneGraph editSceneGraph;
    private final EventBus eventBus;

    @Autowired
    public SnapShotCreator(EditSceneGraph editSceneGraph, EventBus eventBus) {
        this.editSceneGraph = editSceneGraph;
        this.eventBus = eventBus;
        canvas = new VBox();
        boundingBox = new BoundingBox();
        sceneGroup = new Group();
        camera = new ExtendedPerspectiveCamera(true);
        snapshotParameters = new SnapshotParameters();
    }

    @PostConstruct
    private void init() {
        canvas.setManaged(false);
        canvas.setMouseTransparent(true);

        camera.setInitialTransforms(new Rotate(-90, Rotate.X_AXIS));
        camera.setFarClip(10000);
        camera.setPosition(0,0,0);
        double theta = Math.atan(1/Math.sqrt(2));
        double alpha = 5*Math.PI/4;
        camera.setRotationX(Math.toDegrees(-theta));
        camera.setRotationY(Math.toDegrees(alpha));

        snapshotParameters.setFill(Color.TRANSPARENT);

        SubScene scene = new SubScene(sceneGroup, 500, 500, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.TRANSPARENT);
        scene.setCamera(camera);
        sceneGroup.getChildren().add(camera);
        canvas.getChildren().addAll(scene);

        eventBus.subscribe(EventType.TAKE_SCENE_SNAPSHOT.name(), (e) -> takeSnapShot((String) e.getData()));
    }

    private void createSnapShot(LinkedList<SceneMesh> meshes, String path) throws IOException {
        boundingBox.reset();
        final LinkedList<DataCarrier> dataCarriers = new LinkedList<>();
        meshes.forEach((m) -> {
            dataCarriers.add(new DataCarrier(m));
            sceneGroup.getChildren().add(m.getNode());
            m.setBoundingBoxVisible(false);
            m.setMaterial(MATERIAL);
            boundingBox.extend(m.getBoundingBox());
        });
        fitSceneToView();

        WritableImage image = canvas.snapshot(snapshotParameters, null);
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File(path));
        meshes.forEach((m) -> {
            sceneGroup.getChildren().remove(m.getNode());
        });
        dataCarriers.forEach(DataCarrier::load);
    }

    public void takeSnapShot(String path) {
        try {
            createSnapShot(editSceneGraph.getSceneMeshes(), path);
        } catch (IOException e) {
            logger.debug("Snapshot cannot be created", e);
    }
    }

    public Node getNode() {
        return canvas;
    }

    private void fitSceneToView() {
        Point3D center = boundingBox.getCenter();
        double radius = boundingBox.getSize().magnitude()/2;

        double distance = (1/Math.tan(Math.toRadians(15))) * radius * 1.1;
        Point3D vector = new Point3D(1,1,1);
        Point3D cameraPosition = center.add(vector.normalize().multiply(distance));
        camera.setPosition(cameraPosition);
    }

    private class DataCarrier {
        private SceneMesh mesh;
        private boolean boundingBoxVisible;
        private javafx.scene.paint.Material material;
        private Parent parent;

        public DataCarrier(SceneMesh mesh) {
            this.mesh = mesh;
            this.boundingBoxVisible = mesh.isBoundingBoxVisible();
            this.material = mesh.getMaterial();
            this.parent = mesh.getNode().getParent();
        }

        public void load() {
            mesh.setBoundingBoxVisible(boundingBoxVisible);
            mesh.setMaterial(material);
            ((Group) parent).getChildren().add(mesh.getNode());
        }
    }
}
