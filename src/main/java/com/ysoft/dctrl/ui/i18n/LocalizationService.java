package com.ysoft.dctrl.ui.i18n;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.scene.Node;
import javafx.scene.Parent;

/**
 * Created by pilar on 16.5.2017.
 */

@Component
public class LocalizationService {
    private final Logger logger = LogManager.getLogger(LocalizationService.class);

    private final Map<Container, String> translations;
    private final LocalizationResource localizationResource;
    private final EventBus eventBus;
    private final Locale startUpLocale;
    private Locale currentLocale;

    @Autowired
    public LocalizationService(LocalizationResource localizationResource, EventBus eventBus, DeeControlContext context) {
        translations = new HashMap<>();
        this.localizationResource = localizationResource;
        this.eventBus = eventBus;
        this.startUpLocale = context.getSettings().getStartUpLocale();
        this.currentLocale = startUpLocale;
    }

    @PostConstruct
    public void registerEvent() {
        eventBus.subscribe(EventType.CHANGE_LANGUAGE.name(), this::onTranslate);
    }

    public final void addTranslation(Object node) {
        if (node == null) {
            return;
        }

        addMethodContainer("Text", node);
        addMethodContainer("PromptText", node);
        addMethodContainer("Label", node);

        if(node instanceof Parent) { ((Parent) node).getChildrenUnmodifiable().forEach(this::addTranslation); }
        if(node instanceof TabPane) { ((TabPane) node).getTabs().forEach(this::addTranslation); }
        if(node instanceof Tab) { addTranslation(((Tab) node).getContent()); }
        if(node instanceof MenuBar) { ((MenuBar) node).getMenus().forEach(this::addTranslation); }
        if(node instanceof Menu) { ((Menu) node).getItems().forEach(this::addTranslation); }
        if(node instanceof ScrollPane) { addTranslation(((ScrollPane)node).getContent()); }

    }

    private void addMethodContainer(String methodSuffix, Object node) {
        try {
            Method set = node.getClass().getMethod("set" + methodSuffix, String.class);
            Method get = node.getClass().getMethod("get" + methodSuffix);
            String text = (String) get.invoke(node);
            if (text != null && text.startsWith("?_")) {
                String key = text.substring(2);
                Container cont = new Container(node, set);
                translations.put(cont, key);
                translate(cont, startUpLocale, key);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.debug("Not localizable element");
        }
    }

    private void onTranslate(Event e) {
        Locale newLocale = e.getData() instanceof Locale ? (Locale) e.getData() : null;
        currentLocale = newLocale;
        translate(newLocale);
    }

    private void translate(Locale locale) {
        translations.forEach((container, s) -> {
            container.setText(localizationResource.getMessage(locale, s));
        });
    }

    private void translate(Container container, Locale locale, String key) {
        container.setText(localizationResource.getMessage(locale, key));
    }

    public String getMessage(String key) {
        return localizationResource.getMessage(currentLocale, key);
    }

    private static class Container {
        private final Logger logger = LogManager.getLogger(Container.class);

        private final Method set;
        private final Object node;

        public Container(Object node, Method set) {
            this.node = node;
            this.set = set;
        }

        public void setText(String text) {
            try {
                set.invoke(node, text);
            } catch (IllegalAccessException | InvocationTargetException e) {
                logger.warn("Unable to translate", e);
            }
        }
    }
}
