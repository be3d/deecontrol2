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
import com.ysoft.dctrl.utils.DeeControlContext;

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
    private Map<Labeled, String> translations;
    private Map<MenuItem, String> menuTranslations;
    private LocalizationResource localizationResource;

    public LocalizableController(LocalizationResource localizationResource, EventBus eventBus, DeeControlContext context) {
        super(eventBus, context);
        this.localizationResource = localizationResource;
        this.translations = new HashMap<>();
        this.menuTranslations = new HashMap<>();
    }

    @PostConstruct
    public void registerEvent() {
        eventBus.subscribe(EventType.CHANGE_LANGUAGE.name(), this::onTranslate);
    }

    public void initialize(URL location, ResourceBundle resources) {
        translate(deeControlContext.getStartUpLocale());
    }

    protected final void addTranslation(Node node) {
        if(!(node instanceof Parent)) { return; }
        Parent parent = (Parent) node;
        if(parent instanceof Labeled) {
            Labeled labeled = (Labeled) parent;
            if(labeled.getText().startsWith("?_")) {
                String key = labeled.getText().substring(2);
                translations.put(labeled, key);
            }
        }

        parent.getChildrenUnmodifiable().forEach(this::addTranslation);
        if(parent instanceof MenuBar) {
            ((MenuBar) parent).getMenus().forEach(this::addTranslation);
        }
    }

    protected final void addTranslation(MenuItem menuItem) {
        if(menuItem.getText().startsWith("?_")) {
            String key = menuItem.getText().substring(2);
            menuTranslations.put(menuItem, key);
        }

        if(menuItem instanceof Menu) {
            ((Menu) menuItem).getItems().forEach(this::addTranslation);
        }
    }

    private final void onTranslate(Event e) {
        Locale newLocale = e.getData() instanceof Locale ? (Locale) e.getData() : null;
        translate(newLocale);
    }

    protected final void translate(Locale locale) {
        translations.forEach((labeled, s) -> {
            labeled.setText(localizationResource.getMessage(locale, s));
        });
        menuTranslations.forEach((menuItem, s) -> {
            menuItem.setText(localizationResource.getMessage(locale, s));
        });
    }
}
