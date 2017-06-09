package com.ysoft.dctrl.ui.controller;

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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;

/**
 * Created by kuhn on 5/30/2017.
 */
@Controller
public class GCodePanelController extends LocalizableController implements Initializable {

    protected SceneGraph sceneGraph;
    protected LinkedList<GCodeLayer> layers = new LinkedList<>();
    private static final String TEMP_PATH = System.getProperty("user.home") + File.separator + ".dctrl" + File.separator + ".slicer";
    private static final String GCODE_FILE_PATH = TEMP_PATH + File.separator + "sliced.gcode";

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


    public GCodePanelController(
            SceneGraph sceneGraph,
            LocalizationResource localizationResource,
            EventBus eventBus,
            DeeControlContext context) {

        super(localizationResource, eventBus, context);
        this.sceneGraph = sceneGraph;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        eventBus.subscribe(EventType.SLICER_FINISHED.name(), this::startViewer);
        eventBus.subscribe(EventType.SLICER_BACK_TO_EDIT.name(), this::setInvisible);

        displayShell.bindControlChanged(
                (observable, oldValue, newValue) -> {

                    ArrayList<GCodeMoveType> associatedTypes = new ArrayList<>();
                    associatedTypes.add(GCodeMoveType.NONE);
                    associatedTypes.add(GCodeMoveType.WALL_OUTER);
                    associatedTypes.add(GCodeMoveType.WALL_INNER);
                    associatedTypes.add(GCodeMoveType.SKIN);

                    this.sceneGraph.getgCodeGraph().showGCodeTypes(associatedTypes, (boolean)newValue);

                });

        displayTravelMoves.bindControlChanged(
                ((observable, oldValue, newValue) -> this.sceneGraph.getgCodeGraph().showGCodeType(GCodeMoveType.TRAVEL, (boolean)newValue)));
        displayInfill.bindControlChanged(
                ((observable, oldValue, newValue) -> this.sceneGraph.getgCodeGraph().showGCodeType(GCodeMoveType.FILL, (boolean)newValue)));
        displaySupports.bindControlChanged(
                ((observable, oldValue, newValue) -> this.sceneGraph.getgCodeGraph().showGCodeType(GCodeMoveType.SUPPORT, (boolean)newValue)));

        backToEditBtn.setOnAction(event -> {
            eventBus.publish(new Event(EventType.SLICER_BACK_TO_EDIT.name()));
        });

        sendJobBtn.setOnAction(event -> {
            eventBus.publish(new Event(EventType.SEND_TO_SAFEQ_CLICK.name()));
        });

        super.initialize(location, resources);
    }
    private void startViewer(Event e){
        this.setVisible();

        GCodeImporter gCodeImporter = new GCodeImporter(eventBus);
        YieldImportRunner importRunner = new YieldImportRunner<GCodeLayer>(eventBus, gCodeImporter, GCODE_FILE_PATH);
        importRunner.setOnYield((l)-> {
            eventBus.publish(new Event(EventType.GCODE_LAYER_GENERATED.name(), l));
        });
        sceneGraph.hideAllMeshes();

        new Thread(importRunner).start();
    }

    private void setVisible(){
        gcodePanelPane.setVisible(true);
    }
    private void setInvisible(Event e){
        gcodePanelPane.setVisible(false);
    }

}
