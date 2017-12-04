package com.ysoft.dctrl.ui.controller;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.ysoft.dctrl.action.ActionStack;
import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.ui.factory.component.AboutFactory;
import com.ysoft.dctrl.utils.Clipboard;
import com.ysoft.dctrl.utils.settings.ShortcutKeys;
import javafx.application.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.SceneMode;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.dialog.RetentionFileChooser;
import com.ysoft.dctrl.ui.dialog.contract.DialogEventData;
import com.ysoft.dctrl.ui.dialog.DialogType;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Created by pilar on 30.3.2017.
 */

@Controller
public class MenuBarController extends LocalizableController implements Initializable {
    private static final String GCODE_EXTENSION = "*.gcode";
    private static final String JOB_EXTENSION = "*.3djob";
    private static final String ABOUT_URL = "";

    private static final FileChooser.ExtensionFilter GCODE_FILTER = new FileChooser.ExtensionFilter("GCode file", GCODE_EXTENSION);
    private static final FileChooser.ExtensionFilter JOB_FILTER = new FileChooser.ExtensionFilter("3D print job", JOB_EXTENSION);

    @FXML MenuItem openFile;
    @FXML MenuItem quit;
    @FXML MenuItem settings;
    @FXML MenuItem exportAs;
    @FXML MenuItem undo;
    @FXML MenuItem redo;
    @FXML MenuItem copy;
    @FXML MenuItem paste;
    @FXML MenuItem duplicate;
    @FXML MenuItem delete;
    @FXML MenuItem selectAll;
    @FXML MenuItem zoomIn;
    @FXML MenuItem zoomOut;
    @FXML MenuItem resetCamera;
    @FXML MenuItem about;

    private final RetentionFileChooser retentionFileChooser;
    private final ActionStack actionStack;
    private final EditSceneGraph editSceneGraph;
    private final Clipboard clipboard;
    private final Stage aboutStage;

    @Autowired
    public MenuBarController(LocalizationService localizationService,
                             EventBus eventBus, DeeControlContext deeControlContext,
                             RetentionFileChooser retentionFileChooser,
                             ActionStack actionStack,
                             EditSceneGraph editSceneGraph,
                             Clipboard clipboard,
                             AboutFactory aboutFactory) {
        super(localizationService, eventBus, deeControlContext);
        this.retentionFileChooser = retentionFileChooser;
        this.actionStack = actionStack;
        this.editSceneGraph = editSceneGraph;
        this.clipboard = clipboard;
        this.aboutStage = aboutFactory.buildAbout();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        openFile.setOnAction(this::onOpenFile);
        settings.setOnAction(this::onSettings);

        quit.setOnAction(this::onQuit);
        quit.setAccelerator(ShortcutKeys.QUIT);

        exportAs.setOnAction(this::onExportAs);
        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), (e) -> {
            exportAs.setDisable(e.getData() != SceneMode.GCODE);
        });


        delete.setOnAction(this::onDelete);
        delete.setAccelerator(ShortcutKeys.DELETE);
        selectAll.setAccelerator(ShortcutKeys.SELECT_ALL);
        selectAll.setOnAction(this::onSelectAll);

        zoomIn.setAccelerator(ShortcutKeys.ZOOM_IN);
        zoomIn.setOnAction(this::onZoomIn);
        zoomOut.setAccelerator(ShortcutKeys.ZOOM_OUT);
        zoomOut.setOnAction(this::onZoomOut);
        resetCamera.setOnAction(this::onResetCamera);
        about.setOnAction(this::onAbout);

        undo.setOnAction(this::onUndo);
        redo.setOnAction(this::onRedo);

        copy.setOnAction(this::onCopy);
        copy.setAccelerator(ShortcutKeys.COPY);
        paste.setOnAction(this::onPaste);
        paste.setAccelerator(ShortcutKeys.PASTE);
        duplicate.setOnAction(this::onDuplicate);
        duplicate.setAccelerator(ShortcutKeys.DUPLICATE);

        super.initialize(location, resources);
    }

    private void languageChange(ActionEvent event) {
        eventBus.publish(new Event(EventType.CHANGE_LANGUAGE.name(), ((MenuItem) event.getTarget()).getUserData()));
    }

    private void onSettings(ActionEvent event) {
        eventBus.publish(new Event(EventType.SHOW_DIALOG.name(), new DialogEventData(DialogType.PREFERENCES)));
    }

    private void onExportAs(ActionEvent event) {
        File f = retentionFileChooser.showSaveDialog(root.getScene().getWindow(), GCODE_FILTER, JOB_FILTER);
        switch (retentionFileChooser.getSelectedExtensionFilter().getExtensions().get(0)) {
            case GCODE_EXTENSION:
                eventBus.publish(new Event(EventType.GCODE_EXPORT.name(), f.getAbsolutePath()));
                break;
            case JOB_EXTENSION:
                eventBus.publish(new Event(EventType.JOB_EXPORT.name(), f.getAbsolutePath()));
                break;
        }
    }

    private void onOpenFile(ActionEvent event){
        File f = retentionFileChooser.showOpenDialog(root.getScene().getWindow(), new FileChooser.ExtensionFilter("3D models", "*.STL", "*.stl"));
        if(f == null) { return; }
        eventBus.publish(new Event(EventType.ADD_MODEL.name(), f.getAbsolutePath()));
    }

    private void onQuit(ActionEvent event){
        Platform.exit();
    }

    private void onUndo(ActionEvent event){
        actionStack.undo();
    }

    private void onRedo(ActionEvent event){
        actionStack.redo();
    }

    private void onCopy(ActionEvent event) {
        List<SceneMesh> cloned = editSceneGraph.cloneSelection();
        if(!cloned.isEmpty()) { clipboard.addModels(cloned); }
    }

    private void onPaste(ActionEvent event) {
        if(clipboard.hasModels()) {
            List<SceneMesh> models = clipboard.getModels();
            models.forEach(editSceneGraph::cloneMesh);
        } else if(clipboard.hasModelFiles()) {
            List<File> modelFiles = clipboard.getModelFiles();
            eventBus.publish(new Event(EventType.ADD_MODEL.name(), modelFiles.get(0).getAbsolutePath()));
        }
    }

    private void onDuplicate(ActionEvent event) {
        List<SceneMesh> cloned = editSceneGraph.cloneSelection();
        if(!cloned.isEmpty()) { cloned.forEach(editSceneGraph::cloneMesh); }
    }

    private void onZoomIn(ActionEvent event){
        eventBus.publish(new Event(EventType.ZOOM_IN_VIEW.name()));
    }

    private void onZoomOut(ActionEvent event){
        eventBus.publish(new Event(EventType.ZOOM_OUT_VIEW.name()));
    }

    private void onResetCamera(ActionEvent event){
        eventBus.publish(new Event(EventType.RESET_VIEW.name()));
    }

    private void onAbout(ActionEvent event){
        if(aboutStage.getOwner() == null) {
            aboutStage.initOwner(root.getScene().getWindow());
        }

        aboutStage.show();
    }

    private void onDelete(ActionEvent event){
        eventBus.publish(new Event(EventType.EDIT_DELETE_SELECTED.name()));
    }

    private void onSelectAll(ActionEvent event){
        eventBus.publish(new Event(EventType.EDIT_SELECT_ALL.name()));
    }
}