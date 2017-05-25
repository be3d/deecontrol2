package com.ysoft.dctrl.ui.controller;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.editor.importer.ImportRunner;
import com.ysoft.dctrl.editor.importer.StlImporter;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;

/**
 * Created by pilar on 9.5.2017.
 */

@Controller
public class NoModelPanelController extends LocalizableController {
    @FXML
    StackPane root;

    @FXML
    Button browse;

    private String eventDecriptor;


    public NoModelPanelController(LocalizationService localizationService, EventBus eventBus, DeeControlContext context) {
        super(localizationService, eventBus, context);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventDecriptor = eventBus.subscribe(EventType.MODEL_LOADED.name(), this::hide);

        browse.setOnAction(event -> {
            final FileChooser dialog = new FileChooser();
            File f = dialog.showOpenDialog(root.getScene().getWindow());
            if(f == null) return;
            eventBus.publish(new Event(EventType.ADD_MODEL.name(), f.getAbsolutePath()));
        });

        super.initialize(location, resources);
    }

    public void hide(Event event) {
        root.setVisible(false);
        eventBus.unsubscribe(eventDecriptor);
    }
}
