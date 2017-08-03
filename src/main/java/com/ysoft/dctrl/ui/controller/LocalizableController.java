package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;

/**
 * Created by pilar on 30.3.2017.
 */
public abstract class LocalizableController extends AbstractController implements Initializable {
    private LocalizationService localizationService;

    @FXML protected Node root;

    public LocalizableController(LocalizationService localizationService, EventBus eventBus, DeeControlContext context) {
        super(eventBus, context);
        this.localizationService = localizationService;
    }

    public void initialize(URL location, ResourceBundle resources) {
        localizationService.addTranslation(root);
    }

    protected String getMessage(String key) {
        return localizationService.getMessage(key);
    }
}
