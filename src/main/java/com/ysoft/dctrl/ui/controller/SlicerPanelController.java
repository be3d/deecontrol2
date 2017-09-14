package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.SceneMode;
import com.ysoft.dctrl.editor.exporter.SceneExporter;
import com.ysoft.dctrl.editor.mesh.GCodeMeshData;
import com.ysoft.dctrl.editor.mesh.GCodeMeshProperties;
import com.ysoft.dctrl.slicer.SlicerController;
import com.ysoft.dctrl.slicer.param.SlicerParam;
import com.ysoft.dctrl.slicer.param.SlicerParamType;
import com.ysoft.dctrl.slicer.param.SlicerParams;
import com.ysoft.dctrl.slicer.profile.Profile;
import com.ysoft.dctrl.slicer.profile.ProfileResource;
import com.ysoft.dctrl.ui.controller.controlMenu.*;
import com.ysoft.dctrl.ui.tooltip.contract.TooltipData;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
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
import com.ysoft.dctrl.ui.notification.Notification;
import com.ysoft.dctrl.ui.notification.ProgressNotification;
import com.ysoft.dctrl.ui.notification.SuccessNotification;
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

    private final SceneExporter sceneExporter;
    private final SlicerController slicerController;
    private final SlicerParams slicerParams;
    private final ProfileResource profileResource;
    private final GCodeMeshProperties gCodeMeshProperties;
    private final EditSceneGraph editSceneGraph;

    private final ProgressNotification slicingProgressNotification;
    private final SuccessNotification slicingDoneNotification;

    // Layout
    @FXML AnchorPane anchorPane;
    @FXML ScrollPane scrollPane;
    @FXML VBox panelControlsContainer;
    @FXML Label advSettingsToggle;
    @FXML VBox advSettingsBox;

    // Components
    @FXML Picker profilePicker;
    @FXML Picker raftStructurePicker;
    @FXML CheckBoxLabelled supportsCheckBox;
    @FXML TextInput printJobNameInput;
    private boolean userChangedJobName;

    // Adv settings
    @FXML ToggleButtonGroup layerHeightToggle;
    @FXML ButtonIncrement roofThicknessIncrement;
    @FXML ButtonIncrement bottomThicknessIncrement;
    @FXML SliderDiscrete printSpeedSolidSlider;
    @FXML ButtonIncrement shellThicknessIncrement;
    @FXML SliderDiscrete printSpeedShellSlider;
    @FXML Picker infillPatternPicker;
    @FXML SliderDiscrete infillDensitySlider;
    @FXML SliderDiscrete supportDensitySlider;
    @FXML Picker supportPatternPicker;
    @FXML SliderDiscrete supportAngleSlider;

    @FXML Button sliceButton;
    @FXML ImageView saveProfile;
    @FXML Label editedLabel;

    @Autowired
    public SlicerPanelController(
            LocalizationService localizationService,
            EventBus eventBus,
            DeeControlContext deeControlContext,
            FilePathResource filePathResource,
            SceneExporter sceneExporter,
            SlicerController slicerController,
            SlicerParams slicerParams,
            GCodeMeshProperties gCodeMeshProperties,
            EditSceneGraph editSceneGraph,
            ProfileResource profileResource) {

        super(localizationService, eventBus, deeControlContext);
        this.sceneExporter = sceneExporter;
        this.slicerController = slicerController;
        this.slicerParams = slicerParams;
        this.profileResource = profileResource;
        this.gCodeMeshProperties = gCodeMeshProperties;
        this.editSceneGraph = editSceneGraph;
        this.sceneSTL = filePathResource.getPath(FilePath.SCENE_EXPORT_FILE);
        this.sceneImage = filePathResource.getPath(FilePath.SCENE_IMAGE_FILE);

        this.slicingProgressNotification = new ProgressNotification();
        this.slicingDoneNotification = new SuccessNotification();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        scrollPane.vvalueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                    eventBus.publish(new Event(EventType.SLICER_PANEL_SCROLLED.name()));
                });

        List<Profile> list = profileResource.getProfiles();
        ObservableList obList = FXCollections.observableList(list);

        profilePicker.setItems(obList);
        profilePicker.bindControlChanged((observable, oldValue, newValue) -> {
            profileResource.applyProfile(newValue);
            this.setEdited(oldValue == newValue);
        });
        profilePicker.selectItem(obList.get(0));

        raftStructurePicker
                .load(slicerParams.get(SlicerParamType.SUPPORT_BUILDPLATE_TYPE.name()))
                .bindParamChanged()
                .bindControlChanged((observable, oldValue, newValue) -> {
                    slicerParams.updateParam(SlicerParamType.SUPPORT_BUILDPLATE_TYPE.name(), newValue);
                    roofThicknessIncrement.updateView();
                    this.setEdited(oldValue != newValue);
                });

        supportsCheckBox
                .load(slicerParams.get(SlicerParamType.SUPPORT_ENABLED.name()))
                .bindParamChanged()
                .bindControlChanged((observable, oldValue, newValue) -> {
                    slicerParams.updateParam(SlicerParamType.SUPPORT_ENABLED.name(), newValue);
                    this.setEdited(oldValue != newValue);
                });

        // Layer height parameter also needs to be sent to GCodeViewer properties object
        SlicerParam layerHeightParam = slicerParams.get(SlicerParamType.RESOLUTION_LAYER_HEIGHT.name());
        layerHeightToggle
                .load(layerHeightParam)
                .bindParamChanged((observable, oldValue, newValue) -> {
                    gCodeMeshProperties.setLayerHeight(((Double)newValue).floatValue());
                })
                .bindControlChanged((observable, oldValue, newValue) -> {
                    if (newValue != null){
                        slicerParams.updateParam(SlicerParamType.RESOLUTION_LAYER_HEIGHT.name(), ((ToggleButton) newValue).getUserData());
                        roofThicknessIncrement.updateView();
                        bottomThicknessIncrement.updateView();
                        this.setEdited(oldValue != newValue);
                    }
                });
        gCodeMeshProperties.setLayerHeight(((Double)layerHeightParam.getValue()).floatValue());

        roofThicknessIncrement
                .bindRecalculation((e) -> (double)slicerParams.get(SlicerParamType.RESOLUTION_LAYER_HEIGHT.name()).getValue() * e)
                .load(slicerParams.get(SlicerParamType.SHELL_TOP_LAYERS.name()))
                .bindParamChanged()
                .bindControlChanged((observable, oldValue, newValue) -> setEdited(newValue != oldValue));

        bottomThicknessIncrement
                .bindRecalculation((e) -> (double)slicerParams.get(SlicerParamType.RESOLUTION_LAYER_HEIGHT.name()).getValue() * e)
                .load(slicerParams.get(SlicerParamType.SHELL_BOTTOM_LAYERS.name()))
                .bindParamChanged()
                .bindControlChanged((observable, oldValue, newValue) -> setEdited(newValue != oldValue));

        printSpeedSolidSlider
                .load(slicerParams.get(SlicerParamType.SPEED_SOLID_LAYERS.name()))
                .bindParamChanged()
                .bindControlChanged(((observable, oldValue, newValue) -> {
                    slicerParams.updateParam(SlicerParamType.SPEED_SOLID_LAYERS.name(), newValue);
                    this.setEdited(oldValue != newValue);
                }));

        shellThicknessIncrement
                .bindRecalculation((v) -> 0.4 * v)
                .load(slicerParams.get(SlicerParamType.SHELL_THICKNESS.name()))
                .bindParamChanged()
                .bindControlChanged((observable, oldValue, newValue) -> setEdited(newValue != oldValue));

        printSpeedShellSlider
                .load(slicerParams.get(SlicerParamType.SPEED_OUTER_WALL.name()))
                .bindParamChanged()
                .bindControlChanged(((observable, oldValue, newValue) -> slicerParams.updateParam(SlicerParamType.SPEED_OUTER_WALL.name(), newValue)));


        infillPatternPicker
                .load(slicerParams.get(SlicerParamType.INFILL_PATTERN.name()))
                .bindParamChanged()
                .bindControlChanged((observable, oldValue, newValue) -> {
                    slicerParams.updateParam(SlicerParamType.INFILL_PATTERN.name(), newValue);
                    this.setEdited(oldValue != newValue);
                });

        infillDensitySlider
                .load(slicerParams.get(SlicerParamType.INFILL_DENSITY.name()))
                .bindParamChanged((observable, oldValue, newValue) -> infillDensitySlider.setValue((Double)newValue))
                .bindControlChanged((observable, oldValue, newValue) -> {
                    slicerParams.updateParam(SlicerParamType.INFILL_DENSITY.name(), newValue);
                    this.setEdited(oldValue != newValue);
                });

        supportDensitySlider
                .load(slicerParams.get(SlicerParamType.SUPPORT_DENSITY.name()))
                .bindParamChanged((observable, oldValue, newValue) -> supportDensitySlider.setValue((Double)newValue))
                .bindControlChanged(((observable, oldValue, newValue) -> {
                    slicerParams.updateParam(SlicerParamType.SUPPORT_DENSITY.name(), newValue);
                    this.setEdited(oldValue != newValue);
                }));


        supportPatternPicker
                .load(slicerParams.get(SlicerParamType.SUPPORT_PATTERN.name()))
                .bindParamChanged()
                .bindControlChanged((observable, oldValue, newValue) -> {
                    slicerParams.updateParam(SlicerParamType.SUPPORT_PATTERN.name(), newValue);
                    this.setEdited(oldValue != newValue);
                });


        supportAngleSlider
                .load(slicerParams.get(SlicerParamType.SUPPORT_ANGLE.name()))
                .bindParamChanged((observable, oldValue, newValue) -> supportAngleSlider.setValue((Double)newValue))
                .bindControlChanged(((observable, oldValue, newValue) -> {
                    slicerParams.updateParam(SlicerParamType.SUPPORT_ANGLE.name(), newValue);
                    this.setEdited(oldValue != newValue);
                }));

        sliceButton.setOnAction(event -> {
            eventBus.publish(new Event(EventType.TAKE_SCENE_SNAPSHOT.name(), sceneImage));
            eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), slicingProgressNotification));
            deeControlContext.getCurrentProject().setName(printJobNameInput.getText());
            setControlsEnabled(false);
            exportScene();
        });

        saveProfile.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
                Profile savedProfile = profileResource.saveNewProfile("SAVED_USER_PROFILE");

                ObservableList profiles = FXCollections.observableList(profileResource.getProfiles());
                profilePicker.setItems(profiles);
                profilePicker.addItem(savedProfile);
                profilePicker.selectItem(savedProfile);
                profileResource.applyProfile(savedProfile);

                event.consume();
        });

        advSettingsToggle.setOnMouseClicked(event -> {
            if (advSettingsBox.isVisible()){
                advSettingsBox.setManaged(false);
                advSettingsBox.setVisible(false);
                advSettingsToggle.setText(getMessage("show_advanced_settings"));
            } else {
                advSettingsBox.setManaged(true);
                advSettingsBox.setVisible(true);
                advSettingsToggle.setText(getMessage("hide_advanced_settings"));
            }
        });

        slicingProgressNotification.setLabelText(getMessage("slicing_objects"));
        slicingProgressNotification.addOnCloseAction((e) -> {
            slicerController.stopSlice();
        });

        slicingDoneNotification.setLabelText(getMessage("slicing_completed"));

        eventBus.subscribe(EventType.SCENE_EXPORT_PROGRESS.name(), this::onSceneExportProgress);
        eventBus.subscribe(EventType.SLICER_PROGRESS.name(), this::onSlicerProgress);
        eventBus.subscribe(EventType.SLICER_FINISHED.name(), this::onSlicerFinished);
        eventBus.subscribe(EventType.MODEL_LOADED.name(), (e) -> setSliceEnabled(true));
        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), this::onEditModeActivate);
        eventBus.subscribe(EventType.EDIT_SCENE_VALID.name(), (e) -> sliceButton.setDisable(false));
        eventBus.subscribe(EventType.EDIT_SCENE_INVALID.name(), (e) -> sliceButton.setDisable(true));

        initTooltips();

        printJobNameInput.addChangeListener((obs, o, n) -> {
            if(n != null && !n.equals(editSceneGraph.getCurrentSceneName())) {
                setUserChangedJobName(true);
            }
        });
        printJobNameInput.addFocusChangedListener((obs, o, n) -> {
            if(n) { return; }
            if(printJobNameInput.getText().equals("")) {
                setUserChangedJobName(false);
                printJobNameInput.setText(editSceneGraph.getCurrentSceneName());
            }
        });

        eventBus.subscribe(EventType.EDIT_SCENE_MODEL_STACK_CHANGED.name(), (e) -> {
            if(!isUserChangedJobName()) { printJobNameInput.setText(editSceneGraph.getCurrentSceneName()); }
        });

        super.initialize(location, resources);
    }

    private boolean isUserChangedJobName() { return userChangedJobName; }
    private void setUserChangedJobName(boolean userChangedJobName) { this.userChangedJobName = userChangedJobName; }

    private void exportScene(){
        sceneExporter.exportScene(sceneSTL);
    }

    private void onSceneExportProgress(Event e) {
        slicingProgressNotification.setProgress(0.2 * (double) e.getData());
    }

    private void onSlicerProgress(Event e) {
        slicingProgressNotification.setProgress(0.2 + 0.8 * (double) e.getData());
    }

    private void onSlicerFinished(Event e){
        slicingProgressNotification.hide();
        eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), slicingDoneNotification));
        eventBus.publish(new Event(EventType.SCENE_SET_MODE.name(), SceneMode.GCODE));
    }

    private void onEditModeActivate(Event e){
        if(e.getData() == SceneMode.EDIT){
            setControlsEnabled(true);
        }
    }

    private void setControlsEnabled(boolean value){
        panelControlsContainer.setDisable(!value);
    }

    private void setSliceEnabled(boolean value){
        sliceButton.setDisable(!value);
    }

    private void setEdited(boolean value){
        saveProfile.setVisible(value);
        editedLabel.setVisible(value);
    }

    private void initTooltips(){
        raftStructurePicker.attachTooltip(eventBus, new TooltipData(getMessage("slicer_init_platform"), "", getMessage("slicer_init_platform_tooltip")));
        supportsCheckBox.attachTooltip(eventBus, new TooltipData(getMessage("slicer_supports"),"", getMessage("slicer_supports_tooltip")));
        layerHeightToggle.attachTooltip(eventBus, new TooltipData(getMessage("slicer_layer_height"), "",getMessage("slicer_layer_height_tooltip")));
        roofThicknessIncrement.attachTooltip(eventBus, new TooltipData(getMessage("slicer_roof_thickness"), "", getMessage("slicer_roof_thickness_tooltip")));
        bottomThicknessIncrement.attachTooltip(eventBus, new TooltipData(getMessage("slicer_bottom_thickness"), "", getMessage("slicer_bottom_thickness_tooltip")));
        printSpeedSolidSlider.attachTooltip(eventBus, new TooltipData(getMessage("slicer_speed_solid"), "", getMessage("slicer_speed_solid_tooltip")));
        shellThicknessIncrement.attachTooltip(eventBus, new TooltipData(getMessage("slicer_shell_thickness"), "", getMessage("slicer_shell_thickness_tooltip")));
        printSpeedShellSlider.attachTooltip(eventBus, new TooltipData(getMessage("slicer_speed_shell"), "", getMessage("slicer_speed_shell_tooltip")));
        infillPatternPicker.attachTooltip(eventBus, new TooltipData(getMessage("slicer_infill_pattern"), "",getMessage("slicer_infill_pattern_tooltip")));
        infillDensitySlider.attachTooltip(eventBus, new TooltipData(getMessage("slicer_infill_density"), "", getMessage("slicer_infill_density_tooltip")));
        supportDensitySlider.attachTooltip(eventBus, new TooltipData(getMessage("slicer_support_density"), "", getMessage("slicer_support_density_tooltip")));
        supportPatternPicker.attachTooltip(eventBus, new TooltipData(getMessage("slicer_support_pattern"), "", getMessage("slicer_support_pattern_tooltip")));
        supportAngleSlider.attachTooltip(eventBus, new TooltipData(getMessage("slicer_support_angle"), "", getMessage("slicer_support_angle_tooltip")));
    }
}