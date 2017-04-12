package com.ysoft.dctrl.ui.controller;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * Created by pilar on 30.3.2017.
 */

@Controller
public class MenuBarController extends LocalizableController implements Initializable {
    @FXML Menu language;

    @Autowired
    public MenuBarController(LocalizationResource localizationResource, EventBus eventBus, DeeControlContext deeControlContext) {
        super(localizationResource, eventBus, deeControlContext);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MenuItem english = new MenuItem("English");
        english.setUserData(Locale.US);
        MenuItem czech = new MenuItem("Czech");
        czech.setUserData(new Locale("cs", "CZ"));

        english.setOnAction(this::languageChange);
        czech.setOnAction(this::languageChange);

        language.getItems().addAll(english, czech);

        //addTranslation(menuBar);
        super.initialize(location, resources);
    }

    private void languageChange(ActionEvent event) {
        eventBus.publish(new Event(EventType.CHANGE_LANGUAGE.name(), ((MenuItem) event.getTarget()).getUserData()));
    }
}
