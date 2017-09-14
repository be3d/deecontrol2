package com.ysoft.dctrl.ui.controller;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;
import com.sun.javafx.application.HostServicesDelegate;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.input.KeyCharacterCombination;
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

    private static final FileChooser.ExtensionFilter gcodeFilter = new FileChooser.ExtensionFilter("GCode file", GCODE_EXTENSION);
    private static final FileChooser.ExtensionFilter jobFilter = new FileChooser.ExtensionFilter("3D print job", JOB_EXTENSION);
//    private final HostServicesDelegate hostServices = HostServicesFactory.getInstance();


    @FXML MenuItem settings;
    @FXML MenuItem exportAs;
    @FXML MenuItem undo;
    @FXML MenuItem redo;
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
        settings.setOnAction(this::onSettings);

        exportAs.setOnAction(this::onExportAs);
        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), (e) -> {
            exportAs.setDisable(e.getData() != SceneMode.GCODE);
        });

        undo.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN));
        undo.setOnAction( e -> System.out.println("UNDO"));
        redo.setAccelerator(new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN));
        redo.setOnAction( e -> System.out.println("REDO"));
        zoomIn.setAccelerator(new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_DOWN));
        zoomIn.setOnAction( e -> System.out.println("zoomin"));
        zoomOut.setAccelerator(new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_DOWN));
        zoomOut.setOnAction( e -> System.out.println("zoomOUT"));
//        resetCamera.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        resetCamera.setOnAction( e -> System.out.println("resetcam"));
        about.setOnAction( e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.ysoft.com/en/support-and-download"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
        });


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
}