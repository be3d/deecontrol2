package com.ysoft.dctrl.ui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.control.TrackBallControls;
import com.ysoft.dctrl.editor.importer.ImportRunner;
import com.ysoft.dctrl.editor.importer.StlImporter;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.notification.ProgressNotification;
import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.KeyEventPropagator;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 22.3.2017.
 */

@Controller
public class CanvasController extends AbstractController implements Initializable {
    @FXML AnchorPane canvas;

    private SceneGraph sceneGraph;
    private KeyEventPropagator keyEventPropagator;

    @Autowired
    public CanvasController(EventBus eventBus, DeeControlContext deeControlContext, SceneGraph sceneGraph, KeyEventPropagator keyEventPropagator) {
        super(eventBus, deeControlContext);
        this.sceneGraph = sceneGraph;
        this.keyEventPropagator = keyEventPropagator;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SubScene subScene = new SubScene(sceneGraph.getSceneGroup(), 10, 10, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.WHITESMOKE);
        subScene.setCamera(sceneGraph.getCamera());

        canvas.getChildren().addAll(subScene);

        canvas.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
            subScene.setWidth(newValue.doubleValue());
        });
        canvas.prefHeightProperty().addListener((observable, oldValue, newValue) -> {
            subScene.setHeight(newValue.doubleValue());
        });

        TrackBallControls controls = new TrackBallControls(sceneGraph.getCamera(), new Point3D(0,-400,400));

        canvas.setOnMousePressed(controls::onMousePressed);
        canvas.setOnMouseDragged(controls::onMouseDragged);
        canvas.setOnMouseReleased(controls::onMouseReleased);
        canvas.setOnScroll(controls::onScroll);
        canvas.setOnDragDropped(this::onDragDrop);
        canvas.setOnDragOver(this::onDragOver);

        keyEventPropagator.onKeyPressed(this::keyDown);
        eventBus.subscribe(EventType.ADD_MODEL.name(), this::addModel);
        eventBus.subscribe(EventType.RESET_VIEW.name(), (e) -> controls.resetCamera());
        eventBus.subscribe(EventType.TAKE_SCENE_SNAPSHOT.name(), (e) -> takeSnapShot((String) e.getData()));
    }

    private void onDragOver(DragEvent dragEvent) {
        if(dragEvent.getGestureSource() != canvas && dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        dragEvent.consume();
    }

    private void onDragDrop(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        if(db.hasFiles()) {
            db.getFiles().forEach((f) -> addModel(f.getAbsolutePath()));
        }
    }

    public void addModel(Event event) {
        String modelPath = (String) event.getData();
        addModel(modelPath);
    }

    public void addModel(String modelPath) {
        ProgressNotification progressNotification = new ProgressNotification();
        progressNotification.setLabelText("Inserting objects...");
        String hd = eventBus.subscribe(EventType.MODEL_LOAD_PROGRESS.name(), e -> {
            progressNotification.setProgress(((double) e.getData())/100);
        });

        eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), progressNotification));

        StlImporter stlImporter = new StlImporter();
        ImportRunner importRunner = new ImportRunner(eventBus, stlImporter, modelPath);
        importRunner.setOnSucceeded(e -> {
            progressNotification.hide();
            eventBus.publish(new Event(EventType.MODEL_LOADED.name(), importRunner.getValue()));
            eventBus.unsubscribe(hd);
        });

        new Thread(importRunner).start();
    }

    public void keyDown(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case TAB:
                if(keyEvent.isControlDown()) {
                    if(keyEvent.isShiftDown()) {
                        sceneGraph.selectPrevious();
                    } else {
                        sceneGraph.selectNext();
                    }
                }
                break;
            case DELETE:
                sceneGraph.deleteSelected();
                break;
        }
    }

    public void takeSnapShot(String path) {
        WritableImage image = canvas.snapshot(new SnapshotParameters(), null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
