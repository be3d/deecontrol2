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
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.FileChooser;

/**
 * Created by pilar on 21.3.2017.
 */

@Controller
public class SlicerPanelController extends LocalizableController implements Initializable {

    private final String sceneSTL = System.getProperty("user.home") + File.separator + ".dctrl" + File.separator + ".slicer" + File.separator + "dctrl_scene.stl";
    private boolean edited = false;

    protected final SceneExporter sceneExporter;
    protected final SceneGraph sceneGraph;
    protected final PrinterResource printerResource;
    protected final SlicerController slicerController;
    protected final SlicerParams slicerParams;
    protected final ProfileResource profileResource;

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
            LocalizationResource localizationResource,
            EventBus eventBus,
            DeeControlContext deeControlContext,
            SceneExporter sceneExporter,
            SceneGraph sceneGraph,
            PrinterResource printerResource,
            SlicerController slicerController,
            SlicerParams slicerParams,
            ProfileResource profileResource) {

        super(localizationResource, eventBus, deeControlContext);
        this.sceneExporter = sceneExporter;
        this.sceneGraph = sceneGraph;
        this.printerResource = printerResource;
        this.slicerController = slicerController;
        this.slicerParams = slicerParams;
        this.profileResource = profileResource;

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
            eventBus.publish(new Event(EventType.SLICER_SCENE_EXPORT.name()));
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

            eventBus.publish(new Event(EventType.SLICER_STOP.name()));

        });

        eventBus.subscribe(EventType.SLICER_SCENE_EXPORT.name(), this::exportScene);
        eventBus.subscribe(EventType.SLICER_SCENE_EXPORTED.name(), this::startSlicer);
        eventBus.subscribe(EventType.SLICER_PROGRESS.name(), this::slicerProgressHandle);
        eventBus.subscribe(EventType.SLICER_FINISHED.name(), this::gCodeViewerStart);

        super.initialize(location, resources);
    }

    private void exportScene(Event e){
        sceneExporter.exportScene(sceneGraph, sceneSTL);
        eventBus.publish(new Event(EventType.SLICER_SCENE_EXPORTED.name()));
    }

    private void startSlicer(Event e){
        // todo clean any previous instance of runner (this way they are stacking after slicing multiple times)
        SlicerRunner slicerRunner = new SlicerRunner(eventBus, slicerController.slicer,
                slicerParams.getAllParams(), System.getProperty("user.home") +
                    File.separator + ".dctrl" + File.separator + ".slicer" + File.separator +"dctrl_scene.stl");
        new Thread(slicerRunner).start();

    }

    private void slicerProgressHandle(Event e){
        System.out.println(( e.getData()));
    }

    private void gCodeViewerStart(Event e){}

    private void setEdited(boolean value){
        saveProfile.setVisible(value);
    }
}