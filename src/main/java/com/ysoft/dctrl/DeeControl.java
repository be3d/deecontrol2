package com.ysoft.dctrl;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.instance.InstanceMonitor;
import com.ysoft.dctrl.log.DeeControlConfigurationFactory;
import com.ysoft.dctrl.ui.BaseWindow;
import com.ysoft.dctrl.utils.DeeControlConfig;
import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.OSVersion;
import com.ysoft.dctrl.utils.files.FileValidator;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Created by pilar on 20.3.2017.
 */
public class DeeControl extends Application {
    private ConfigurableApplicationContext applicationContext;
    private InstanceMonitor instanceMonitor;

    public static void main(String[] args) {
        initLogger();
        launch(args);
    }

    private static void initLogger() {
        ConfigurationFactory.setConfigurationFactory(new DeeControlConfigurationFactory());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        applicationContext = new AnnotationConfigApplicationContext(DeeControlConfig.class);
        applicationContext.registerShutdownHook();
        EventBus eventBus = applicationContext.getBean(EventBus.class);

        if(!OSVersion.is(OSVersion.MAC)) {
            instanceMonitor = new InstanceMonitor();
            boolean isServer = instanceMonitor.startServer(eventBus);
            if (!isServer && !instanceMonitor.connectClient(getParameters().getRaw())) {
                throw new IllegalStateException("Other instance is running");
            }
        }

        BaseWindow baseWindow = applicationContext.getBean(BaseWindow.class);
        baseWindow.composeWindow(primaryStage);
        primaryStage.setTitle("DeeControl2");
        primaryStage.getIcons().addAll(
                new Image(getClass().getResourceAsStream("/img/ico/icon-16.png")),
                new Image(getClass().getResourceAsStream("/img/ico/icon-32.png")),
                new Image(getClass().getResourceAsStream("/img/ico/icon-48.png")),
                new Image(getClass().getResourceAsStream("/img/ico/icon-256.png"))
        );
        primaryStage.show();

        if(OSVersion.is(OSVersion.MAC)) {
            com.sun.glass.ui.Application glassApp = com.sun.glass.ui.Application.GetApplication();
            glassApp.setEventHandler(new com.sun.glass.ui.Application.EventHandler() {
                @Override
                public void handleOpenFilesAction(com.sun.glass.ui.Application app, long time, String[] files) {
                    for(String f : files) {
                        if(FileValidator.isModelFileSupproted(f)) {
                            eventBus.publish(new Event(EventType.ADD_MODEL.name(), f));
                        }
                    }
                }
            });
        } else {
            List<String> args = getParameters().getRaw();
            if(args.size() > 0) {
               for(String p : args){
                    if(FileValidator.isModelFileSupproted(p)) {
                        eventBus.publish(new Event(EventType.ADD_MODEL.name(), p));
                        break;
                    }
               }
            }
        }


    }

    @Override
    public void stop() throws Exception {
        super.stop();
        instanceMonitor.destroy();
    }
}
