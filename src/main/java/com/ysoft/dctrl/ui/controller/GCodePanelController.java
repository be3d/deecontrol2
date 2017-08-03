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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

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

    @FXML CheckBoxInline displayShell;
    @FXML CheckBoxInline displayTravelMoves;
    @FXML CheckBoxInline displayInfill;
    @FXML CheckBoxInline displaySupports;
    @FXML CheckBoxInline viewOneLayer;
    @FXML AnchorPane layerSlider;

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
        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), (e) -> {
            if(e.getData() == SceneMode.GCODE) {
                gcodeSceneGraph.loadGCode();
                show();
            } else {
                hide();
            }
        });

        displayShell.bindControlChanged(
                (observable, oldValue, newValue) -> {

                      // TBD UX
                      //ArrayList<GCodeMoveType> associatedTypes = new ArrayList<>();
                      //associatedTypes.add(GCodeMoveType.NONE);
                      //associatedTypes.add(GCodeMoveType.WALL_OUTER);
                      //associatedTypes.add(GCodeMoveType.WALL_INNER);
                      //associatedTypes.add(GCodeMoveType.SKIN);

                    gcodeSceneGraph.showGCodeType(GCodeMoveType.WALL_OUTER, (boolean)newValue);

                });

        displayTravelMoves.bindControlChanged(
                ((observable, oldValue, newValue) -> gcodeSceneGraph.showGCodeType(GCodeMoveType.TRAVEL, (boolean)newValue)));
        displayInfill.bindControlChanged(
                ((observable, oldValue, newValue) -> gcodeSceneGraph.showGCodeType(GCodeMoveType.FILL, (boolean)newValue)));
        displaySupports.bindControlChanged(
                ((observable, oldValue, newValue) -> gcodeSceneGraph.showGCodeType(GCodeMoveType.SUPPORT, (boolean)newValue)));

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

        super.initialize(location, resources);
    }


    private void show(){
        this.setVisible(true);
    }

    private void hide(){
        this.setVisible(false);
    }

    private void setVisible(boolean value){
        gcodePanelPane.setVisible(value);
    }



}
