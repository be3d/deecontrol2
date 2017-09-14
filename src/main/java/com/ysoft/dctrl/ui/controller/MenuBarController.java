package com.ysoft.dctrl.ui.controller;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.SceneMode;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.dialog.RetentionFileChooser;
import com.ysoft.dctrl.ui.dialog.contract.DialogEventData;
import com.ysoft.dctrl.ui.factory.dialog.DialogType;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

/**
 * Created by pilar on 30.3.2017.
 */

@Controller
public class MenuBarController extends LocalizableController implements Initializable {
    private static final String GCODE_EXTENSION = "*.gcode";
    private static final String JOB_EXTENSION = "*.3djob";
    private static final String ABOUT_URL = "https://www.ysoft.com/en/support-and-download";

    private static final FileChooser.ExtensionFilter gcodeFilter = new FileChooser.ExtensionFilter("GCode file", GCODE_EXTENSION);
    private static final FileChooser.ExtensionFilter jobFilter = new FileChooser.ExtensionFilter("3D print job", JOB_EXTENSION);
//    private final HostServicesDelegate hostServices = HostServicesFactory.getInstance();

    private enum Shortcuts{
        UNDO(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN)),
        REDO(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN)),
        SELECT_ALL(new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN)),
        ZOOM_IN(new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_DOWN)),
        ZOOM_OUT(new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_DOWN))
        ;


        private KeyCodeCombination k;
        Shortcuts(KeyCodeCombination k){ this.k = k; }

        public KeyCodeCombination get() {
            return k;
        }
    }

    @FXML MenuItem openFile;
    @FXML MenuItem quit;
    @FXML MenuItem settings;
    @FXML MenuItem exportAs;
    @FXML MenuItem undo;
    @FXML MenuItem redo;
    @FXML MenuItem delete;
    @FXML MenuItem selectAll;
    @FXML MenuItem zoomIn;
    @FXML MenuItem zoomOut;
    @FXML MenuItem resetCamera;
    @FXML MenuItem about;

    private RetentionFileChooser retentionFileChooser;

    @Autowired
    public MenuBarController(LocalizationService localizationService, EventBus eventBus, DeeControlContext deeControlContext, RetentionFileChooser retentionFileChooser) {
        super(localizationService, eventBus, deeControlContext);
        this.retentionFileChooser = retentionFileChooser;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        openFile.setOnAction(this::onOpenFile);
        settings.setOnAction(this::onSettings);
        quit.setOnAction( this::onQuit);

        exportAs.setOnAction(this::onExportAs);
        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), (e) -> {
            exportAs.setDisable(e.getData() != SceneMode.GCODE);
        });

        undo.setAccelerator(Shortcuts.UNDO.get());
        undo.setOnAction(this::onUndo);
        redo.setAccelerator(Shortcuts.REDO.get());
        redo.setOnAction(this::onRedo);

        delete.setOnAction(this::onDelete);
        selectAll.setAccelerator(Shortcuts.SELECT_ALL.get());
        selectAll.setOnAction(this::onSelectAll);

        zoomIn.setAccelerator(Shortcuts.ZOOM_IN.get());
        zoomIn.setOnAction(this::onZoomIn);
        zoomOut.setAccelerator(Shortcuts.ZOOM_OUT.get());
        zoomOut.setOnAction(this::onZoomOut);
        resetCamera.setOnAction(this::onResetCamera);
        about.setOnAction(this::onAbout);

        super.initialize(location, resources);
    }

    private void languageChange(ActionEvent event) {
        eventBus.publish(new Event(EventType.CHANGE_LANGUAGE.name(), ((MenuItem) event.getTarget()).getUserData()));
    }

    private void onSettings(ActionEvent event) {
        eventBus.publish(new Event(EventType.SHOW_DIALOG.name(), new DialogEventData(DialogType.PREFERENCES)));
    }

    private void onExportAs(ActionEvent event) {
        File f = retentionFileChooser.showSaveDialog(root.getScene().getWindow(), gcodeFilter, jobFilter);
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
        if(f == null) return;
        eventBus.publish(new Event(EventType.ADD_MODEL.name(), f.getAbsolutePath()));
    }

    private void onQuit(ActionEvent event){
        Platform.exit();
    }

    private void onUndo(ActionEvent event){
        //eventBus.publish(new Event(EventType.SHOW_DIALOG.name());
    }

    private void onRedo(ActionEvent event){
        //eventBus.publish(new Event(EventType.SHOW_DIALOG.name());
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
        try {
            Desktop.getDesktop().browse(new URI(ABOUT_URL));
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    private void onDelete(ActionEvent event){
        eventBus.publish(new Event(EventType.EDIT_DELETE_SELECTED.name()));
    }

    private void onSelectAll(ActionEvent event){
        eventBus.publish(new Event(EventType.EDIT_SELECT_ALL.name()));
    }
}