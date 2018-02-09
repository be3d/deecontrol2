package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.SceneMode;
import com.ysoft.dctrl.editor.exporter.SceneExporter;
import com.ysoft.dctrl.editor.mesh.GCodeMeshProperties;
import com.ysoft.dctrl.slicer.SlicerController;
import com.ysoft.dctrl.slicer.param.SlicerParam;
import com.ysoft.dctrl.slicer.param.SlicerParamType;
import com.ysoft.dctrl.slicer.param.SlicerParams;
import com.ysoft.dctrl.slicer.profile.Profile;
import com.ysoft.dctrl.slicer.profile.ProfileResource;
import com.ysoft.dctrl.ui.controller.controlMenu.*;
import com.ysoft.dctrl.ui.dialog.contract.DialogEventData;
import com.ysoft.dctrl.ui.dialog.contract.TextInputDialogData;
import com.ysoft.dctrl.ui.dialog.DialogType;
import com.ysoft.dctrl.ui.notification.ErrorNotification;
import com.ysoft.dctrl.ui.tooltip.TooltipDefinition;
import com.ysoft.dctrl.ui.tooltip.Tooltip;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private final ErrorNotification slicingFailedNotification;

    // Layout
    @FXML ScrollBox scrollBox;
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

        slicingProgressNotification = new ProgressNotification();
        slicingDoneNotification = new SuccessNotification();
        slicingFailedNotification = new ErrorNotification();

        slicingProgressNotification.setLabelText(getMessage("notification_slicing_objects"));
        slicingDoneNotification.setLabelText(getMessage("notification_slicing_completed"));
        slicingDoneNotification.setTimeout(8);
        slicingFailedNotification.setLabelText(getMessage("notification_slicing_failed"));
        
        slicingProgressNotification.addOnCloseAction((e) -> eventBus.publish(new Event(EventType.SLICER_CANCEL.name())));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        scrollBox.vvalueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
                    eventBus.publish(new Event(EventType.SLICER_PANEL_SCROLLED.name()));
                });

        ObservableList profiles = FXCollections.observableList(profileResource.getProfiles());
        profilePicker.setItems(profiles);
        profilePicker.bindControlChanged((observable, oldValue, newValue) -> {
            if(newValue != null){
                profileResource.applyProfile((Profile)newValue);
                this.setEdited(oldValue == newValue);
            }
        });
        profilePicker.selectItem(profiles.get(0));

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
            disableControls();
            exportScene();
        });

        saveProfile.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
            TextInputDialogData dialogData = new TextInputDialogData();

            dialogData.setHeader(getMessage("dialog_save_slicer_profile"));
            dialogData.setLabel(getMessage("dialog_slicer_profile_name"));
            dialogData.setDescription(getMessage("dialog_slicer_profile_desc"));
            dialogData.setConsumer(this::onSaveProfile);

            eventBus.publish(new Event(EventType.SHOW_DIALOG.name(), new DialogEventData(DialogType.TEXT_INPUT, dialogData)));
            event.consume();
        });

        advSettingsToggle.setOnMouseClicked(event -> {
            if (advSettingsBox.isVisible()){
                advSettingsBox.setManaged(false);
                advSettingsBox.setVisible(false);
                advSettingsToggle.setText(getMessage("slicer_show_advanced_settings"));
            } else {
                advSettingsBox.setManaged(true);
                advSettingsBox.setVisible(true);
                advSettingsToggle.setText(getMessage("slicer_hide_advanced_settings"));
            }
        });

        eventBus.subscribe(EventType.SCENE_EXPORT_PROGRESS.name(), this::onSceneExportProgress);
        eventBus.subscribe(EventType.SLICER_PROGRESS.name(), this::onSlicerProgress);
        eventBus.subscribe(EventType.SLICER_CANCELLED.name(), this::onSlicerCancelled);
        eventBus.subscribe(EventType.SLICER_FINISHED.name(), this::onSlicerFinished);
        eventBus.subscribe(EventType.SLICER_FAILED.name(), this::onSlicerFailed);
        eventBus.subscribe(EventType.EDIT_SCENE_VALID.name(), (e) -> sliceButton.setDisable(false));
        eventBus.subscribe(EventType.EDIT_SCENE_INVALID.name(), (e) -> sliceButton.setDisable(true));
        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), (e) -> {
            if(e.getData() == SceneMode.EDIT){ onEditModeActivate(); }
        });

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

    private void onSlicerCancelled(Event e){
        slicingProgressNotification.hide();
        enableControls();
    }

    private void onSlicerFinished(Event e){
        slicingProgressNotification.hide(0);
        eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), slicingDoneNotification));
        eventBus.publish(new Event(EventType.SCENE_SET_MODE.name(), SceneMode.GCODE));
    }

    private void onSlicerFailed(Event e ){
        slicingProgressNotification.hide();
        enableControls();
        eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), slicingFailedNotification));
    }

    private void onEditModeActivate(){
        slicingDoneNotification.hide();
        enableControls();
    }

    private void onSaveProfile(String name){

        if(name.isEmpty()){ throw new IllegalArgumentException(getMessage("dialog_slicer_profile_empty")); }
        for(Profile p : profileResource.getProfiles()){
            if(p.getName().equals(name)){
                throw new IllegalArgumentException(getMessage("dialog_slicer_profile_collision"));
            }
        }

        Profile savedProfile = profileResource.saveNewProfile(name);
        ObservableList profiles = FXCollections.observableList(profileResource.getProfiles());
        profilePicker.setItems(profiles);
        profilePicker.selectItem(savedProfile);

        setEdited(false);
    }

    private void enableControls() {
        setControlsEnabled(true);
    }

    private void disableControls() {
        setControlsEnabled(false);
    }

    private void setControlsEnabled(boolean value){
        panelControlsContainer.setDisable(!value);
    }

    private void setEdited(boolean value){
        saveProfile.setVisible(value);
        editedLabel.setVisible(value);
    }

    private void initTooltips(){
        raftStructurePicker.attachTooltip(eventBus, new Tooltip(TooltipDefinition.RAFT));
        supportsCheckBox.attachTooltip(eventBus, new Tooltip(TooltipDefinition.SUPPORT));
        layerHeightToggle.attachTooltip(eventBus, new Tooltip(TooltipDefinition.LAYER_HEIGHT));
        roofThicknessIncrement.attachTooltip(eventBus, new Tooltip(TooltipDefinition.ROOF_THICKNESS));
        bottomThicknessIncrement.attachTooltip(eventBus, new Tooltip(TooltipDefinition.BOTTOM_THICKNESS));
        printSpeedSolidSlider.attachTooltip(eventBus, new Tooltip(TooltipDefinition.PRINT_SPEED_SOLID));
        shellThicknessIncrement.attachTooltip(eventBus, new Tooltip(TooltipDefinition.SHELL_THICKNESS));
        printSpeedShellSlider.attachTooltip(eventBus, new Tooltip(TooltipDefinition.PRINT_SPEED_SHELL));
        infillPatternPicker.attachTooltip(eventBus, new Tooltip(TooltipDefinition.INFILL_PATTERN));
        infillDensitySlider.attachTooltip(eventBus, new Tooltip(TooltipDefinition.INFILL_DENSITY));
        supportDensitySlider.attachTooltip(eventBus, new Tooltip(TooltipDefinition.SUPPORT_DENSITY));
        supportPatternPicker.attachTooltip(eventBus, new Tooltip(TooltipDefinition.SUPPORT_PATTERN));
        supportAngleSlider.attachTooltip(eventBus, new Tooltip(TooltipDefinition.SUPPORT_ANGLE));
    }
}