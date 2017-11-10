package com.ysoft.dctrl.ui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.ui.notification.ErrorNotification;
import com.ysoft.dctrl.utils.exceptions.RunningOutOfMemoryException;
import javafx.scene.shape.TriangleMesh;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.control.MeshTransformControls;
import com.ysoft.dctrl.editor.control.TrackBallCameraControls;
import com.ysoft.dctrl.editor.importer.ImportRunner;
import com.ysoft.dctrl.editor.importer.StlImporter;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.event.dto.ModelLoadedDTO;
import com.ysoft.dctrl.ui.notification.ProgressNotification;
import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.KeyEventPropagator;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

/**
 * Created by pilar on 22.3.2017.
 */

@Controller
public class CanvasController extends LocalizableController implements Initializable {
    @FXML AnchorPane canvas;

    private final Logger logger = LogManager.getLogger(CanvasController.class);
    private final SceneGraph sceneGraph;
    private final KeyEventPropagator keyEventPropagator;
    private final MeshTransformControls meshTransformControls;

    private Thread importRunnerThread;

    @Autowired
    public CanvasController(EventBus eventBus,
                            DeeControlContext deeControlContext,
                            LocalizationService localizationService,
                            SceneGraph sceneGraph,
                            KeyEventPropagator keyEventPropagator,
                            MeshTransformControls meshTransformControls
    ) {
        super(localizationService, eventBus, deeControlContext);
        this.sceneGraph = sceneGraph;
        this.keyEventPropagator = keyEventPropagator;
        this.meshTransformControls = meshTransformControls;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SubScene subScene = new SubScene(sceneGraph.getSceneGroup(), 10, 10, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.WHITESMOKE);
        subScene.setCamera(sceneGraph.getCamera());

        sceneGraph.addHelpObject(meshTransformControls.getPlane());

        canvas.getChildren().addAll(subScene);

        canvas.prefWidthProperty().addListener((observable, oldValue, newValue) -> {
            subScene.setWidth(newValue.doubleValue());
        });
        canvas.prefHeightProperty().addListener((observable, oldValue, newValue) -> {
            subScene.setHeight(newValue.doubleValue());
        });

        TrackBallCameraControls controls = new TrackBallCameraControls(sceneGraph.getCamera(), new Point3D(0,-400,400));
        
        canvas.setOnMousePressed((e) -> {
            canvas.requestFocus();
            meshTransformControls.onMousePressed(e);
            if(e.isConsumed()) { return; }
            controls.onMousePressed(e);
        });
        canvas.setOnMouseDragged(controls::onMouseDragged);
        canvas.setOnMouseReleased(e -> {
            meshTransformControls.onMouseRelease(e);
            if(e.isConsumed()) { return; }
            controls.onMouseReleased(e);
            if(e.isConsumed() || e.getButton() != MouseButton.PRIMARY) { return; }
            eventBus.publish(new Event(EventType.EDIT_CLEAR_SELECTION.name()));
        });
        canvas.setOnScroll(controls::onScroll);
        canvas.setOnDragDropped(this::onDragDrop);
        canvas.setOnDragOver(this::onDragOver);

        keyEventPropagator.onKeyPressed(this::keyDown);
        eventBus.subscribe(EventType.ADD_MODEL.name(), this::addModel);
        eventBus.subscribe(EventType.RESET_VIEW.name(), (e) -> controls.resetCamera());
        eventBus.subscribe(EventType.TOP_VIEW.name(), (e) -> controls.setTopView());
        eventBus.subscribe(EventType.ZOOM_IN_VIEW.name(), (e) -> controls.zoomInCamera());
        eventBus.subscribe(EventType.ZOOM_OUT_VIEW.name(), (e) -> controls.zoomOutCamera());
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
            addModel(db.getFiles().get(0).getAbsolutePath());
            //db.getFiles().forEach((f) -> addModel(f.getAbsolutePath()));
        }
    }

    public void addModel(Event event) {
        String modelPath = (String) event.getData();
        addModel(modelPath);
    }

    public void addModel(String modelPath) {
        ProgressNotification progressNotification = new ProgressNotification();
        progressNotification.setLabelText(getMessage("notification_inserting_objects"));
        progressNotification.addOnCloseAction(e -> importRunnerThread.interrupt());
        String hd = eventBus.subscribe(EventType.MODEL_LOAD_PROGRESS.name(), e -> {
            progressNotification.setProgress(((double) e.getData())/100);
        });
        ErrorNotification addModelFailedNotification = new ErrorNotification();
        addModelFailedNotification.setLabelText(getMessage("notification_too_big_to_insert"));

        String modelName = (new File(modelPath)).getName().replaceAll("\\.([^.])*$", "");

        eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), progressNotification));

        StlImporter stlImporter = new StlImporter();
        ImportRunner importRunner = new ImportRunner<TriangleMesh>(eventBus, stlImporter, modelPath);
        importRunner.setOnSucceeded(e -> {
            progressNotification.hide();
            eventBus.unsubscribe(hd);

            TriangleMesh mesh = (TriangleMesh)importRunner.getValue();
            if(mesh != null){
                eventBus.publish(new Event(EventType.MODEL_LOADED.name(), new ModelLoadedDTO((TriangleMesh)importRunner.getValue(), modelName)));
            } else {
                logger.warn("Model import failed: {}",modelPath);
                eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), addModelFailedNotification));
            }
        });
        importRunner.setOnFailed(e -> {
            progressNotification.hide();
            eventBus.unsubscribe(hd);

            Throwable t = ((ImportRunner)e.getSource()).getException();
            if((t instanceof RunningOutOfMemoryException) || (t instanceof OutOfMemoryError)){
                logger.warn("Model import failed (out of memory): {}",modelPath);
                eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), addModelFailedNotification));
            }
        });

        importRunnerThread = new Thread(importRunner);
        importRunnerThread.start();
    }

    public void keyDown(KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case TAB:
                if(keyEvent.isControlDown()) {
                    if(keyEvent.isShiftDown()) {
                        eventBus.publish(new Event(EventType.EDIT_SELECT_PREV.name()));
                    } else {
                        eventBus.publish(new Event(EventType.EDIT_SELECT_NEXT.name()));
                    }
                }
                break;
        }
    }
}
