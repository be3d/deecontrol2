package com.ysoft.dctrl.ui.controller;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.exporter.SceneExporter;
import com.ysoft.dctrl.slicer.SlicerController;
import com.ysoft.dctrl.slicer.SlicerRunner;
import com.ysoft.dctrl.slicer.param.SlicerParamType;
import com.ysoft.dctrl.slicer.param.SlicerParams;
import com.ysoft.dctrl.slicer.printer.PrinterResource;
import com.ysoft.dctrl.slicer.profile.Profile;
import com.ysoft.dctrl.slicer.profile.ProfileResource;
import com.ysoft.dctrl.ui.controller.controlMenu.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.ui.notification.ProgressNotification;
import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.files.FilePath;
import com.ysoft.dctrl.utils.files.FilePathResource;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * Created by pilar on 21.3.2017.
 */

@Controller
public class SlicerPanelController extends LocalizableController implements Initializable {

    private final String sceneSTL;
    private final String sceneImage;
    private boolean edited = false;

    private final SceneExporter sceneExporter;
    private final SceneGraph sceneGraph;
    private final SlicerController slicerController;
    private final SlicerParams slicerParams;
    private final ProfileResource profileResource;

    private final ProgressNotification slicingProgressNotification;

    // Layout
    @FXML AnchorPane anchorPane;
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
    @FXML SliderDiscrete roofThicknessSlider;
    @FXML SliderDiscrete bottomThicknessSlider;
    @FXML SliderContinuous printSpeedSolidSlider;
    @FXML SliderDiscrete shellThicknessSlider;
    @FXML SliderContinuous printSpeedShellSlider;
    @FXML Picker infillPatternPicker;
    @FXML SliderContinuous infillDensitySlider;
    @FXML SliderContinuous supportDensitySlider;
    @FXML Picker supportPatternPicker;
    @FXML SliderContinuous supportAngleSlider;

    @FXML Button slice;
    @FXML ImageView saveProfile;

    //test
    //@FXML Button add;
    @FXML ProgressBar progress;
    @FXML Button cancelSlice;
    @FXML Button loadProfile;

    @Autowired
    public SlicerPanelController(
            LocalizationService localizationService,
            EventBus eventBus,
            DeeControlContext deeControlContext,
            FilePathResource filePathResource,
            SceneExporter sceneExporter,
            SceneGraph sceneGraph,
            SlicerController slicerController,
            SlicerParams slicerParams,
            ProfileResource profileResource) {

        super(localizationService, eventBus, deeControlContext);
        this.sceneExporter = sceneExporter;
        this.sceneGraph = sceneGraph;
        this.slicerController = slicerController;
        this.slicerParams = slicerParams;
        this.profileResource = profileResource;
        this.sceneSTL = filePathResource.getPath(FilePath.SCENE_EXPORT_FILE);
        this.sceneImage = filePathResource.getPath(FilePath.SCENE_IMAGE_FILE);

        this.slicingProgressNotification = new ProgressNotification();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        List<Profile> list = profileResource.getProfiles();
        ObservableList obList = FXCollections.observableList(list);

        profilePicker.setItems(obList);
        profilePicker.bindControlChanged((observable, oldValue, newValue) -> {
            profileResource.applyProfile(newValue);
            this.setEdited(false);
        });
        profilePicker.selectItem(obList.get(0));

        raftStructurePicker
                .load(slicerParams.get(SlicerParamType.SUPPORT_BUILDPLATE_TYPE.name()))
                .bindParamChanged()
                .bindControlChanged((observable, oldValue, newValue) -> {
                    slicerParams.updateParam(SlicerParamType.SUPPORT_BUILDPLATE_TYPE.name(), newValue);
                    this.setEdited(true);
                });

        supportsCheckBox.addChangeListener((observable, oldValue, newValue) -> System.out.println(newValue));

        layerHeightSlider
                .load(slicerParams.get(SlicerParamType.RESOLUTION_LAYER_HEIGHT.name()))
                .bindParamChanged()
                .bindControlChanged(((observable, oldValue, newValue) -> {
                    slicerParams.updateParam(SlicerParamType.RESOLUTION_LAYER_HEIGHT.name(), newValue);
                    this.setEdited(true);
                }));

        roofThicknessSlider
                .load(slicerParams.get(SlicerParamType.SHELL_TOP_LAYERS.name()))
                .bindParamChanged()
                .bindControlChanged(((observable, oldValue, newValue) -> slicerParams.updateParam(SlicerParamType.SHELL_TOP_LAYERS.name(), newValue)));


        bottomThicknessSlider
                .load(slicerParams.get(SlicerParamType.SHELL_BOTTOM_LAYERS.name()))
                .bindParamChanged()
                .bindControlChanged(((observable, oldValue, newValue) -> slicerParams.updateParam(SlicerParamType.SHELL_BOTTOM_LAYERS.name(), newValue)));


        printSpeedSolidSlider
                .load(slicerParams.get(SlicerParamType.SPEED_SOLID_LAYERS.name()))
                .bindParamChanged()
                .bindControlChanged(((observable, oldValue, newValue) -> slicerParams.updateParam(SlicerParamType.SPEED_SOLID_LAYERS.name(), newValue)));

        shellThicknessSlider
                .load(slicerParams.get(SlicerParamType.SHELL_THICKNESS.name()))
                .bindParamChanged()
                .bindControlChanged(((observable, oldValue, newValue) -> slicerParams.updateParam(SlicerParamType.SHELL_THICKNESS.name(), newValue)));

        printSpeedShellSlider
                .load(slicerParams.get(SlicerParamType.SPEED_OUTER_WALL.name()))
                .bindParamChanged()
                .bindControlChanged(((observable, oldValue, newValue) -> slicerParams.updateParam(SlicerParamType.SPEED_OUTER_WALL.name(), newValue)));


        infillPatternPicker.load(slicerParams.get(SlicerParamType.INFILL_PATTERN.name()));

        infillDensitySlider
                .load(slicerParams.get(SlicerParamType.INFILL_DENSITY.name()))
                .bindParamChanged((observable, oldValue, newValue) -> infillDensitySlider.setValue((Double)newValue))
                .bindControlChanged(((observable, oldValue, newValue) -> slicerParams.updateParam(SlicerParamType.INFILL_DENSITY.name(), newValue)));


        supportDensitySlider
                .load(slicerParams.get(SlicerParamType.SUPPORT_DENSITY.name()))
                .bindParamChanged((observable, oldValue, newValue) -> supportDensitySlider.setValue((Double)newValue))
                .bindControlChanged(((observable, oldValue, newValue) -> slicerParams.updateParam(SlicerParamType.SUPPORT_DENSITY.name(), newValue)));


        supportPatternPicker.load(slicerParams.get(SlicerParamType.SUPPORT_PATTERN.name()));


        supportAngleSlider
                .load(slicerParams.get(SlicerParamType.SUPPORT_ANGLE.name()))
                .bindParamChanged((observable, oldValue, newValue) -> supportAngleSlider.setValue((Double)newValue))
                .bindControlChanged(((observable, oldValue, newValue) -> slicerParams.updateParam(SlicerParamType.SUPPORT_ANGLE.name(), newValue)));

        progress.setProgress(0);

        slice.setOnAction(event -> {
            eventBus.publish(new Event(EventType.TAKE_SCENE_SNAPSHOT.name(), sceneImage));
            eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), slicingProgressNotification));
            deeControlContext.getCurrentProject().setName(printJobNameInput.getText());
            exportScene();
        });


        cancelSlice.setOnAction(event -> {
            eventBus.publish(new Event(EventType.SLICER_STOP.name()));
        });

        loadProfile.setOnAction(event -> {
            List<Profile> profiles = profileResource.loadProfiles();
            for(Profile p : profiles){
                System.out.println(p.getId());
            }
        });

        saveProfile.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Profile savedProfile = profileResource.saveNewProfile("SAVED_USER_PROFILE");

                List<Profile> list = profileResource.getProfiles();
                ObservableList obList = FXCollections.observableList(list);

                profilePicker.setItems(obList);
                profilePicker.addItem(savedProfile);
                profilePicker.selectItem(savedProfile);
                profileResource.applyProfile(savedProfile);

                event.consume();
            }
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

        slicingProgressNotification.setLabelText("Slicing objects…");
        slicingProgressNotification.addOnCloseAction((e) -> {
            slicerController.stopSlice();
        });

        eventBus.subscribe(EventType.SCENE_EXPORT_PROGRESS.name(), this::onSceneExportProgress);
        eventBus.subscribe(EventType.SLICER_PROGRESS.name(), this::onSlicerProgress);
        eventBus.subscribe(EventType.SLICER_FINISHED.name(), (e) -> slicingProgressNotification.hide());

        super.initialize(location, resources);
    }

    private void exportScene(){
        sceneExporter.exportScene(sceneGraph, sceneSTL);
    }

    private void onSceneExportProgress(Event e) {
        slicingProgressNotification.setProgress(0.2 * (double) e.getData());
    }

    private void onSlicerProgress(Event e) {
        slicingProgressNotification.setProgress(0.2 + 0.8 * (double) e.getData());
    }

    private void setEdited(boolean value){
        saveProfile.setVisible(value);
    }
}