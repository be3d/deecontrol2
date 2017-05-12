package com.ysoft.dctrl.ui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.ysoft.dctrl.editor.exporter.SceneExporter;
import com.ysoft.dctrl.slicer.SlicerController;
import com.ysoft.dctrl.slicer.cura.Cura;
import com.ysoft.dctrl.slicer.param.SlicerParamType;
import com.ysoft.dctrl.slicer.param.SlicerParams;
import com.ysoft.dctrl.slicer.printer.PrinterResource;
import com.ysoft.dctrl.slicer.profile.Profile;
import com.ysoft.dctrl.slicer.profile.ProfileResource;
import com.ysoft.dctrl.ui.controller.controlMenu.*;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;

/**
 * Created by pilar on 21.3.2017.
 */

@Controller
public class ControlMenuController extends LocalizableController implements Initializable {

    // Layout
    @FXML AnchorPane root;
    @FXML ScrollPane scrollPane;
    @FXML Label advSettingsToggle;
    @FXML VBox advSettingsBox;


    // Components
    @FXML Picker profilePicker;
    @FXML Picker raftStructurePicker;
    @FXML CheckBoxLabelled supportsCheckBox;
    @FXML TextInput printJobNameInput;

    // Adv settings
    @FXML SliderDiscrete layerHeightSlider;
    @FXML SliderContinuous infillDensitySlider;
    @FXML SliderContinuous supportDensitySlider;


    @FXML Button slice;

    //test
    //@FXML Button add;
    @FXML ProgressBar progress;
    @FXML Button cancelSlice;
    @FXML Button loadProfile;
    @FXML Button saveProfile;

    @Autowired    PrinterResource printerResource;
    @Autowired    SlicerController slicerController;
    @Autowired    SlicerParams slicerParams;
    @Autowired    ProfileResource profileResource;

    @Autowired
    public ControlMenuController(LocalizationResource localizationResource, EventBus eventBus, DeeControlContext deeControlContext) {
        super(localizationResource, eventBus, deeControlContext);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //        add.setOnAction(event -> {
        //            System.err.println(event.toString());
        //            final FileChooser dialog = new FileChooser();
        //            File f = dialog.showOpenDialog(null);
        //            if(f == null) return;
        //            eventBus.publish(new Event(EventType.ADD_MODEL.name(), f.getAbsolutePath()));
        //        });

        List<Profile> list = profileResource.getProfiles();
        ObservableList obList = FXCollections.observableList(list);

        profilePicker.setItems(obList);
        profilePicker.addChangeListener((observable, oldValue, newValue) -> System.out.println(newValue));

        raftStructurePicker.loadFromSlicerParam(slicerParams.getAllParams().get(SlicerParamType.SUPPORT_BUILDPLATE_TYPE.name()));
        raftStructurePicker.addChangeListener((observable, oldValue, newValue) -> System.out.println(newValue));

        supportsCheckBox.addChangeListener((observable, oldValue, newValue) -> System.out.println(newValue));

        infillDensitySlider.addChangeListener((observable, oldValue, newValue) -> System.out.println(newValue));
        supportDensitySlider.addChangeListener((observable, oldValue, newValue) -> System.out.println(newValue));

        eventBus.subscribe(EventType.SLICE_STARTED.name(), (e) -> {
            ReadOnlyDoubleProperty progressProperty = (ReadOnlyDoubleProperty) e.getData();
            progress.progressProperty().bind(progressProperty);
        });

        eventBus.subscribe(EventType.SLICE_DONE.name(), (e) -> {
            progress.setVisible(false);
            progress.progressProperty().unbind();
            FileChooser fileChooser = new FileChooser();
            File out = fileChooser.showSaveDialog(root.getScene().getWindow());
            if(out == null) return;
            try {
                Files.copy(Paths.get(Cura.GCODE_FILE), out.toPath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        slice.setOnAction(event -> {
            System.err.println(event.toString());
            progress.setProgress(0);
            progress.setVisible(true);
            eventBus.publish(new Event(EventType.EXPORT_SCENE.name()));
        });

        cancelSlice.setOnAction(event -> {
            slicerController.slicer.stopTask();
        });

        loadProfile.setOnAction(event -> {
            List<Profile> profiles = profileResource.loadProfiles();
            for(Profile p : profiles){
                System.out.println(p.id);
            }
        });
        saveProfile.setOnAction(event -> {
            profileResource.saveNewProfile("newProfile01");
        });


        advSettingsToggle.setOnMouseClicked(event -> {
            if (advSettingsBox.isVisible()){
                advSettingsBox.setManaged(false);
                advSettingsBox.setVisible(false);
                advSettingsToggle.setText("Show advanced settings...");
            } else {
                advSettingsBox.setManaged(true);
                advSettingsBox.setVisible(true);
                advSettingsToggle.setText("Hide advanced settings...");
            }
        });

        // Adv settings
        //        layerHeightSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
        //            Double value = (Math.round(newValue.doubleValue()*100)/100.0);
        //            layerHeightValue.setText(value.toString());
        //            slicerParams.updateParam(SlicerParamType.LAYER_HEIGHT.name(), value);
        //        });
        //
        layerHeightSlider.addChangeListener((observable, oldValue, newValue) -> slicerParams.updateParam(SlicerParamType.LAYER_HEIGHT.name(), newValue));

        super.initialize(location, resources);
    }
}