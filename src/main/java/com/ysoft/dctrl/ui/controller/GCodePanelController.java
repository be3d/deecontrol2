package com.ysoft.dctrl.ui.controller;

import com.ysoft.dctrl.editor.GCodeViewer;
import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.importer.GCodeImporter;
import com.ysoft.dctrl.editor.importer.YieldImportRunner;
import com.ysoft.dctrl.editor.mesh.GCodeLayer;
import com.ysoft.dctrl.editor.mesh.GCodeMoveType;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.controller.controlMenu.CheckBoxInline;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by kuhn on 5/30/2017.
 */
@Controller
public class GCodePanelController extends LocalizableController implements Initializable {

    protected SceneGraph sceneGraph;
    protected GCodeViewer gCodeViewer;

    @FXML
    AnchorPane gcodePanelPane;
    @FXML
    Button backToEditBtn;
    @FXML
    Button sendJobBtn;

    @FXML   CheckBoxInline displayShell;
    @FXML   CheckBoxInline displayTravelMoves;
    @FXML   CheckBoxInline displayInfill;
    @FXML   CheckBoxInline displaySupports;
    @FXML   CheckBoxInline viewOneLayer;
    @FXML
    AnchorPane layerSlider;

    public GCodePanelController(
            SceneGraph sceneGraph,
            GCodeViewer gCodeViewer,
            LocalizationResource localizationResource,
            EventBus eventBus,
            DeeControlContext context) {

        super(localizationResource, eventBus, context);
        this.sceneGraph = sceneGraph;
        this.gCodeViewer = gCodeViewer;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        eventBus.subscribe(EventType.SLICER_FINISHED.name(), (e) -> {
            eventBus.publish(new Event(EventType.GCODE_VIEWER_OPEN.name()));
            this.prepareView();
            gCodeViewer.startViewer();

        });

        eventBus.subscribe(EventType.SLICER_BACK_TO_EDIT.name(), (e) -> {
            eventBus.publish(new Event(EventType.GCODE_VIEWER_CLOSE.name()));
            this.closeView();
        });



        displayShell.bindControlChanged(
                (observable, oldValue, newValue) -> {

                      // TBD UX
//                    ArrayList<GCodeMoveType> associatedTypes = new ArrayList<>();
//                    associatedTypes.add(GCodeMoveType.NONE);
//                    associatedTypes.add(GCodeMoveType.WALL_OUTER);
//                    associatedTypes.add(GCodeMoveType.WALL_INNER);
//                    associatedTypes.add(GCodeMoveType.SKIN);

                    gCodeViewer.showGCodeType(GCodeMoveType.WALL_OUTER, (boolean)newValue);

                });

        displayTravelMoves.bindControlChanged(
                ((observable, oldValue, newValue) -> gCodeViewer.showGCodeType(GCodeMoveType.TRAVEL, (boolean)newValue)));
        displayInfill.bindControlChanged(
                ((observable, oldValue, newValue) -> gCodeViewer.showGCodeType(GCodeMoveType.FILL, (boolean)newValue)));
        displaySupports.bindControlChanged(
                ((observable, oldValue, newValue) -> gCodeViewer.showGCodeType(GCodeMoveType.SUPPORT, (boolean)newValue)));

        backToEditBtn.setOnAction(event -> {
            eventBus.publish(new Event(EventType.SLICER_BACK_TO_EDIT.name()));
        });

        sendJobBtn.setOnAction(event -> {
            eventBus.publish(new Event(EventType.SEND_TO_SAFEQ_CLICK.name()));
        });

        super.initialize(location, resources);
    }


    private void prepareView(){
        this.setVisible(true);
    }

    private void closeView(){
        this.setVisible(false);
    }

    private void setVisible(boolean value){
        gcodePanelPane.setVisible(value);
    }



}
