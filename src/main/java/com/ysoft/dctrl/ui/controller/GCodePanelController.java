package com.ysoft.dctrl.ui.controller;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
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
    @FXML
    AnchorPane gcodePanelPane;
    @FXML
    Button backToEditBtn;
    @FXML
    Button sendJobBtn;

    public GCodePanelController(LocalizationResource localizationResource, EventBus eventBus, DeeControlContext context) {
        super(localizationResource, eventBus, context);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        eventBus.subscribe(EventType.SLICER_FINISHED.name(), this::setVisible);
        eventBus.subscribe(EventType.SLICER_BACK_TO_EDIT.name(), this::setInvisible);

        backToEditBtn.setOnAction(event -> {
            eventBus.publish(new Event(EventType.SLICER_BACK_TO_EDIT.name()));
        });

        sendJobBtn.setOnAction(event -> {
            eventBus.publish(new Event(EventType.SEND_TO_SAFEQ_CLICK.name()));
        });

        super.initialize(location, resources);
    }

    private void setVisible(Event e){
        gcodePanelPane.setVisible(true);
    }
    private void setInvisible(Event e){
        gcodePanelPane.setVisible(false);
    }
}
