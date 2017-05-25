package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.ysoft.dctrl.DeeControl;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

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
}
