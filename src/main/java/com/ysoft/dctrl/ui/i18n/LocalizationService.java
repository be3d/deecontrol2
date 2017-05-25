package com.ysoft.dctrl.ui.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Labeled;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * Created by pilar on 16.5.2017.
 */

@Component
public class LocalizationService {
    private final Map<Labeled, String> translations;
    private final Map<MenuItem, String> menuTranslations;
    private final LocalizationResource localizationResource;
    private final EventBus eventBus;
    private final Locale startUpLocale;

    @Autowired
    public LocalizationService(LocalizationResource localizationResource, EventBus eventBus, DeeControlContext context) {
        translations = new HashMap<>();
        menuTranslations = new HashMap<>();
        this.localizationResource = localizationResource;
        this.eventBus = eventBus;
        this.startUpLocale = context.getSettings().getStartUpLocale();
    }

    @PostConstruct
    public void registerEvent() {
        eventBus.subscribe(EventType.CHANGE_LANGUAGE.name(), this::onTranslate);
    }

    public final void addTranslation(Node node) {
        if(!(node instanceof Parent)) { return; }
        Parent parent = (Parent) node;
        if(parent instanceof Labeled) {
            Labeled labeled = (Labeled) parent;
            if(labeled.getText().startsWith("?_")) {
                String key = labeled.getText().substring(2);
                translations.put(labeled, key);
                translate(labeled, startUpLocale, key);
            }
        }

        parent.getChildrenUnmodifiable().forEach(this::addTranslation);
        if(parent instanceof MenuBar) {
            ((MenuBar) parent).getMenus().forEach(this::addTranslation);
        }
    }

    public final void addTranslation(MenuItem menuItem) {
        if(menuItem.getText().startsWith("?_")) {
            String key = menuItem.getText().substring(2);
            menuTranslations.put(menuItem, key);
            translate(menuItem, startUpLocale, key);
        }

        if(menuItem instanceof Menu) {
            ((Menu) menuItem).getItems().forEach(this::addTranslation);
        }
    }

    private void onTranslate(Event e) {
        Locale newLocale = e.getData() instanceof Locale ? (Locale) e.getData() : null;
        translate(newLocale);
    }

    private void translate(Locale locale) {
        translations.forEach((labeled, s) -> {
            labeled.setText(localizationResource.getMessage(locale, s));
        });
        menuTranslations.forEach((menuItem, s) -> {
            menuItem.setText(localizationResource.getMessage(locale, s));
        });
    }

    private void translate(Labeled labeled, Locale locale, String key) {
        labeled.setText(localizationResource.getMessage(locale, key));
    }

    private void translate(MenuItem menuItem, Locale locale, String key) {
        menuItem.setText(localizationResource.getMessage(locale, key));
    }
}
