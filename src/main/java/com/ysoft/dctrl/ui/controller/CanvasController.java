package com.ysoft.dctrl.ui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

//import com.interactivemesh.jfx.importer.stl.StlMeshImporter;
import com.ysoft.dctrl.editor.control.ExtendedPerspectiveCamera;
import com.ysoft.dctrl.editor.control.TrackBallControls;
import com.ysoft.dctrl.editor.importer.StlImporter;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by pilar on 22.3.2017.
 */

@Controller
public class CanvasController extends AbstractController implements Initializable {
    @FXML VBox canvas;

    private Group sceneGroup;
    private PhongMaterial phongMaterial;

    @Autowired
    public CanvasController(EventBus eventBus, DeeControlContext deeControlContext) {
        super(eventBus, deeControlContext);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        phongMaterial = new PhongMaterial(Color.LIGHTBLUE);
        //phongMaterial.setSpecularColor(Color.BLUE);
        //phongMaterial.setDiffuseColor(Color.LIGHTBLUE);

        //Box box = new Box(20, 10, 5);
        //box.setMaterial(material);

        ExtendedPerspectiveCamera camera = new ExtendedPerspectiveCamera(true);
        //camera.getTransforms().addAll(new Translate(0,-60,0), new Rotate(-90, Rotate.X_AXIS), new Translate(0,-10,0), new Rotate(-5, Rotate.X_AXIS));
        camera.setInitialTransforms(new Rotate(-90, Rotate.X_AXIS));
        camera.setFarClip(10000);

        sceneGroup = new Group();

        sceneGroup.getChildren().addAll(camera/*, box*/);

        SubScene subScene = new SubScene(sceneGroup, 100, 100, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.WHITESMOKE);
        subScene.setCamera(camera);
        /*subScene.heightProperty().bind(canvas.heightProperty());*/
        //subScene.widthProperty().bind(canvas.widthProperty());

        /*Group root = new Group();
        root.getChildren().add(subScene);*/

        //canvas.getChildren().addAll(root);
        canvas.getChildren().addAll(subScene);

        canvas.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
            System.err.println("w: " + oldValue + " " + newValue);
            subScene.setWidth(newValue.doubleValue());
        });
        canvas.prefHeightProperty().addListener((observable, oldValue, newValue) -> {
            System.err.println("h: " + oldValue + " " + newValue);
            subScene.setHeight(newValue.doubleValue());
        });

        TrackBallControls controls = new TrackBallControls(camera);

        canvas.setOnMousePressed(controls::onMousePressed);
        canvas.setOnMouseDragged(controls::onMouseDragged);
        canvas.setOnMouseReleased(controls::onMouseReleased);
        canvas.setOnScroll(controls::onScroll);

        eventBus.subscribe(EventType.ADD_MODEL.name(), this::addModel);


    }

    public void addModel(Event event) {
        String modelPath = (String) event.getData();
        TriangleMesh mesh;

        //StlMeshImporter importer = new StlMeshImporter();
        //importer.read(modelPath);
        //mesh = importer.getImport();

        StlImporter stlImporter = new StlImporter();
        try {
            mesh = stlImporter.load(modelPath);
            mesh.getTexCoords().addAll(0.0f,0.0f);
        } catch (IOException e) {
            System.err.println("fuck");
            return;
        }
        MeshView view = new MeshView();
        view.setMaterial(phongMaterial);
        view.setMesh(mesh);
        view.setTranslateX(0);
        view.setTranslateY(0);
        view.setTranslateZ(0);
        view.setScaleX(1);
        view.setScaleY(1);
        view.setScaleZ(1);

        sceneGroup.getChildren().addAll(view);
    }
}
