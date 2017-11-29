package com.ysoft.dctrl.ui.controller;

import com.ysoft.dctrl.editor.GCodeSceneGraph;
import com.ysoft.dctrl.editor.SceneMode;
import com.ysoft.dctrl.editor.mesh.GCodeMoveType;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.safeq.SafeQSender;
import com.ysoft.dctrl.safeq.job.JobCreator;
import com.ysoft.dctrl.ui.controller.controlMenu.CheckBoxInline;
import com.ysoft.dctrl.ui.controller.dialog.PreferencesTab;
import com.ysoft.dctrl.ui.dialog.contract.DialogEventData;
import com.ysoft.dctrl.ui.dialog.DialogType;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.ui.notification.*;
import com.ysoft.dctrl.utils.DeeControlContext;

import com.ysoft.dctrl.utils.Project;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/**
 * Created by kuhn on 5/30/2017.
 */
@Controller
public class GCodePanelController extends LocalizableController implements Initializable {
    private final Logger logger = LogManager.getLogger(GCodePanelController.class);

    private GCodeSceneGraph gcodeSceneGraph;
    private SafeQSender safeQSender;
    private JobCreator jobCreator;

    @FXML
    AnchorPane gcodePanelPane;
    @FXML
    Button backToEditBtn;
    @FXML
    Button sendJobBtn;

    @FXML ToggleGroup viewToggleGroup;
    @FXML RadioButton optimizedViewRadio;
    @FXML RadioButton detailedViewRadio;
    @FXML VBox detailViewControls;
    @FXML CheckBoxInline displayShell;
    @FXML CheckBoxInline displayTravelMoves;
    @FXML CheckBoxInline displayInfill;
    @FXML CheckBoxInline displaySupports;


    @FXML Label jobNameLabel;
    @FXML Label printTimeLabel;
    @FXML Label filamentUsageLabel;

    private SuccessNotification jobSendDoneNotification;
    private ErrorNotification jobSendFailedNotification;
    private SpinnerNotification jobSendProgressNotification;
    private SpinnerNotification gCodeRenderingNotification;
    private SuccessNotification gCodeRenderingFailed;
    private AlertNotification gCodeRenderingCancelledNotification;

    public GCodePanelController(
            GCodeSceneGraph gcodeSceneGraph,
            JobCreator jobCreator,
            SafeQSender safeQSender,
            LocalizationService localizationService,
            EventBus eventBus,
            DeeControlContext context) {

        super(localizationService, eventBus, context);
        this.gcodeSceneGraph = gcodeSceneGraph;
        this.jobCreator = jobCreator;
        this.safeQSender = safeQSender;

        jobSendDoneNotification = new SuccessNotification();
        jobSendProgressNotification = new SpinnerNotification();
        jobSendFailedNotification = new ErrorNotification();
        gCodeRenderingNotification = new SpinnerNotification();
        gCodeRenderingNotification.setLabelText(getMessage("notification_gcode_rendering_in_progress"));
        gCodeRenderingFailed = new SuccessNotification();
        gCodeRenderingFailed.setLabelText(getMessage("notification_gcode_rendering_done_no_preview"));
        gCodeRenderingFailed.setTimeout(0);
        gCodeRenderingCancelledNotification = new AlertNotification();
        gCodeRenderingCancelledNotification.setLabelText(getMessage("notification_gcode_rendering_cancelled"));
        gCodeRenderingCancelledNotification.setTimeout(5);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resetControlsToDefault();

        viewToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            switch((String)newValue.getUserData()){
                case "optimizedView": switchToOptimizedView();
                    break;
                case "detailedView": switchToDetailedView();
                    break;
            }
        });
        displayShell.bindControlChanged(
                (observable, oldValue, newValue) -> {
                    List<GCodeMoveType> shellTypes = Arrays.asList(
                            GCodeMoveType.NONE,
                            GCodeMoveType.WALL_INNER,
                            GCodeMoveType.WALL_OUTER,
                            GCodeMoveType.SKIN
                    );
                    gcodeSceneGraph.showGCodeTypes(shellTypes, (boolean)newValue);
                });
        displayTravelMoves.bindControlChanged(
                ((observable, oldValue, newValue) -> {
                    gcodeSceneGraph.showGCodeType(GCodeMoveType.TRAVEL, (boolean)newValue);
                }));
        displayInfill.bindControlChanged(
                ((observable, oldValue, newValue) -> {
                    gcodeSceneGraph.showGCodeType(GCodeMoveType.FILL, (boolean)newValue);
                }));
        displaySupports.bindControlChanged(
                ((observable, oldValue, newValue) -> {
                    gcodeSceneGraph.showGCodeType(GCodeMoveType.SUPPORT, (boolean)newValue);
                }));

        backToEditBtn.setOnAction(event -> {
            eventBus.publish(new Event(EventType.SCENE_SET_MODE.name(), SceneMode.EDIT));
            deeControlContext.getCurrentProject().resetPrintInfo();
        });

        jobSendDoneNotification.setLabelText(getMessage("notification_file_transfer_success"));
        jobSendProgressNotification.setLabelText(getMessage("notification_file_transfer_in_progress"));
        jobSendFailedNotification.setLabelText(getMessage("notification_print_job_send_fail"));

        AlertLinkNotification safeqNotSetNotification = new AlertLinkNotification();
        safeqNotSetNotification.setLabelText(getMessage("notification_safeq_settings_not_set"));
        safeqNotSetNotification.setLinkText(getMessage("notification_safeq_settings_show"));
        safeqNotSetNotification.setOnLinkAction((e) -> {
            eventBus.publish(new Event(EventType.SHOW_DIALOG.name(), new DialogEventData(DialogType.PREFERENCES, PreferencesTab.NETWORK)));
        });
        safeqNotSetNotification.setTimeout(10);

        BooleanSupplier checkSafeQSettings = () -> {
            if(!deeControlContext.getSettings().getSafeQSettings().isSet()) {
                eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), safeqNotSetNotification));
                return false;
            }
            return true;
        };

        sendJobBtn.setOnAction(event -> {
            if(!checkSafeQSettings.getAsBoolean()) { return; }
            jobSendDoneNotification.hide();
            eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), jobSendProgressNotification));
            sendJobBtn.setDisable(true);
            jobCreator.createJobFile();
            eventBus.subscribeOnce(EventType.JOB_FILE_DONE.name(), (e) -> {
                safeQSender.sendJob((String) e.getData());
            });
        });

        eventBus.subscribe(EventType.JOB_SEND_DONE.name(), (e) -> {
            jobSendProgressNotification.hide();
            sendJobBtn.setDisable(false);
            eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), jobSendDoneNotification));
        });

        eventBus.subscribe(EventType.JOB_SEND_FAILED.name(), (e) -> {
            jobSendProgressNotification.hide();
            sendJobBtn.setDisable(false);
            eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), jobSendFailedNotification));
        });

        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), (e) -> {
            if(e.getData() == SceneMode.GCODE){
                startGCodeLoading();
                show();
            } else {
                hide();
            }
        });

        eventBus.subscribe(EventType.GCODE_DRAFT_RENDER_FINISHED.name(), this::onGCRenderFinished);
        eventBus.subscribe(EventType.GCODE_RENDER_OUTTA_MEMORY.name(), this::onNotEnoughMemory);
        eventBus.subscribe(EventType.GCODE_RENDER_CANCEL.name(), this::onGCRenderCancelled);

        super.initialize(location, resources);

        checkSafeQSettings.getAsBoolean();
    }

    private void startGCodeLoading(){
        eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), gCodeRenderingNotification));

        gCodeRenderingNotification.addOnCloseAction(e -> {
            eventBus.publish(new Event(EventType.GCODE_RENDER_CANCEL.name()));
        });

        resetControlsToDefault();
        gcodeSceneGraph.loadGCode();
    }

    private void onNotEnoughMemory(){
        gCodeRenderingNotification.hide();
        eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), gCodeRenderingFailed));
    }

    private void onNotEnoughMemory(Event e){
        onNotEnoughMemory();
    }

    private void onGCRenderFinished(Event e){
        gCodeRenderingNotification.hide(1000);

        optimizedViewRadio.setDisable(false);
        detailedViewRadio.setDisable(false);
    }

    private void onGCRenderCancelled(Event e){
        eventBus.publish(new Event(EventType.SHOW_NOTIFICATION.name(), gCodeRenderingCancelledNotification));
    }

    private void loadProjectInfo(){
        Project project = deeControlContext.getCurrentProject();

        long sec = new Long(project.getPrintDuration());
        long hr = TimeUnit.SECONDS.toHours(sec);
        long min = TimeUnit.SECONDS.toMinutes(sec) - hr*60;

        printTimeLabel.setText((hr > 0 ? hr + " hr " : "") + (min > 0 ? min + " min" : ""));
        jobNameLabel.setText(project.getName());
        filamentUsageLabel.setText(String.valueOf((project.getMaterialUsage().get("PLA")/1000f))+" m");
    }

    private void show(){
        resetControlsToDefault();
        setVisible(true);
        loadProjectInfo();
    }

    private void hide(){
        setVisible(false);
    }

    private void setVisible(boolean value){
        gcodePanelPane.setVisible(value);
    }

    private void switchToOptimizedView(){
        displayShell.setValue(false);
        displayTravelMoves.setValue(false);
        displayInfill.setValue(false);
        displaySupports.setValue(true);
        detailViewControls.setVisible(false);
        gcodeSceneGraph.showOptimizedView();
    }

    private void switchToDetailedView(){
        displayShell.setValue(true);
        displayTravelMoves.setValue(false);
        displayInfill.setValue(true);
        displaySupports.setValue(true);
        detailViewControls.setVisible(true);
        gcodeSceneGraph.showDetailedView();
    }

    private void resetControlsToDefault(){
        optimizedViewRadio.setSelected(true);
        optimizedViewRadio.setDisable(true);
        detailedViewRadio.setDisable(true);
        displayShell.setValue(false);
        displayTravelMoves.setValue(false);
        displayInfill.setValue(false);
        displaySupports.setValue(true);
        detailViewControls.setVisible(false);
    }
}

