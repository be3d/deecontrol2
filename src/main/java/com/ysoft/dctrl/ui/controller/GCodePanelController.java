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
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.ui.notification.SpinnerNotification;
import com.ysoft.dctrl.ui.notification.SuccessNotification;
import com.ysoft.dctrl.utils.DeeControlContext;

import com.ysoft.dctrl.utils.Project;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

/**
 * Created by kuhn on 5/30/2017.
 */
@Controller
public class GCodePanelController extends LocalizableController implements Initializable {

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
    @FXML CheckBoxInline displayOuterWalls;
    @FXML CheckBoxInline displayInnerWalls;
    @FXML CheckBoxInline displayTravelMoves;
    @FXML CheckBoxInline displayInfill;
    @FXML CheckBoxInline displaySupports;


    @FXML Label jobNameLabel;
    @FXML Label printTimeLabel;
    @FXML Label filamentUsageLabel;

    private SuccessNotification jobSendDoneNotification;
    private SpinnerNotification jobSendProgressNotification;

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

        this.jobSendDoneNotification = new SuccessNotification();
        this.jobSendProgressNotification = new SpinnerNotification();
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
        displayOuterWalls.bindControlChanged(
                (((observable, oldValue, newValue) -> {
                    gcodeSceneGraph.showGCodeType(GCodeMoveType.WALL_OUTER, (boolean)newValue);
                }))
        );
        displayInnerWalls.bindControlChanged(
                (observable, oldValue, newValue) -> {
                    List<GCodeMoveType> shellTypes = Arrays.asList(
                            GCodeMoveType.NONE,
                            GCodeMoveType.WALL_INNER,
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
        });

        jobSendDoneNotification.setLabelText("Print job successfully sent to YSoft SafeQ");
        jobSendProgressNotification.setLabelText("File transfer in progress…");

        sendJobBtn.setOnAction(event -> {
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

        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), (e) -> {
            if(e.getData() == SceneMode.GCODE) {
                resetControlsToDefault();
                gcodeSceneGraph.loadGCode();
                show();
            } else {
                hide();
            }
        });

        eventBus.subscribe(EventType.GCODE_DRAFT_RENDER_FINISHED.name(), (e) -> {
            optimizedViewRadio.setDisable(false);
            detailedViewRadio.setDisable(false);
        });

        super.initialize(location, resources);
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
        this.setVisible(true);
        loadProjectInfo();
    }

    private void hide(){
        this.setVisible(false);
    }

    private void setVisible(boolean value){
        gcodePanelPane.setVisible(value);
    }

    private void switchToOptimizedView(){
        displayInnerWalls.setValue(false);
        displayOuterWalls.setValue(true);
        displayTravelMoves.setValue(false);
        displayInfill.setValue(false);
        displaySupports.setValue(true);
        detailViewControls.setVisible(false);
        gcodeSceneGraph.showOptimizedView();
    }

    private void switchToDetailedView(){
        displayInnerWalls.setValue(true);
        displayTravelMoves.setValue(false);
        displayInfill.setValue(true);
        displaySupports.setValue(true);
        detailViewControls.setVisible(true);
        displayOuterWalls.setVisible(true);
        gcodeSceneGraph.showDetailedView();
    }

    private void resetControlsToDefault(){
        optimizedViewRadio.setSelected(true);
        optimizedViewRadio.setDisable(true);
        detailedViewRadio.setDisable(true);
        displayInnerWalls.setValue(false);
        displayTravelMoves.setValue(false);
        displayInfill.setValue(false);
        displaySupports.setValue(true);
        displayOuterWalls.setValue(true);
        detailViewControls.setVisible(false);
    }
}

