package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.event.Event;

/**
 * Created by pilar on 10.4.2017.
 */
public abstract class AbstractEditPanelController extends LocalizableController {
    protected SceneGraph sceneGraph;

    public AbstractEditPanelController(SceneGraph sceneGraph, LocalizationResource localizationResource, EventBus eventBus, DeeControlContext context) {
        super(localizationResource, eventBus, context);
        this.sceneGraph = sceneGraph;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        root.setOnMousePressed(Event::consume);
        super.initialize(location, resources);
    }
}
