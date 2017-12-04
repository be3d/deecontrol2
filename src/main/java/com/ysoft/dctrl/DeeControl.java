package com.ysoft.dctrl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Preloader;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.instance.InstanceMonitor;
import com.ysoft.dctrl.log.DeeControlConfigurationFactory;
import com.ysoft.dctrl.ui.BaseWindow;
import com.ysoft.dctrl.utils.DeeControlConfig;
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
    private static InstanceMonitor instanceMonitor;
    private static Logger logger;

    public static void main(String[] args) {
        initLogger();

        if(!OSVersion.is(OSVersion.MAC)) {
            instanceMonitor = new InstanceMonitor();
            boolean isServer = instanceMonitor.startServer();
            if (!isServer && !instanceMonitor.connectClient(new LinkedList<>(Arrays.asList(args)))) {
                throw new IllegalStateException("Other instance is running");
            }
            LauncherImpl.launchApplication(DeeControl.class, DeeControlPreloader.class, args);
        } else {
            launch(args);
        }
    }

    private static void initLogger() {
        ConfigurationFactory.setConfigurationFactory(new DeeControlConfigurationFactory());
        logger = LogManager.getLogger(DeeControl.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> Platform.runLater(() -> showError(t, e)));
        Thread.currentThread().setUncaughtExceptionHandler(this::showError);

        applicationContext = new AnnotationConfigApplicationContext(DeeControlConfig.class);
        applicationContext.registerShutdownHook();
        EventBus eventBus = applicationContext.getBean(EventBus.class);
        instanceMonitor.setEventBus(eventBus);

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
                        if(FileValidator.isModelFileSupported(f)) {
                            eventBus.publish(new Event(EventType.ADD_MODEL.name(), f));
                        }
                    }
                }
            });
        } else {
            List<String> args = getParameters().getRaw();
            if(args.size() > 0) {
               for(String p : args){
                    if(FileValidator.isModelFileSupported(p)) {
                        eventBus.publish(new Event(EventType.ADD_MODEL.name(), p));
                        break;
                    }
               }
            }
        }

        if(OSVersion.is(OSVersion.WIN)) {
            notifyPreloader(new Preloader.ProgressNotification(1.0));
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        instanceMonitor.destroy();
    }

    private void showError(Thread t, Throwable e){
        logger.error("Unhandled exception in {}", t.getName(), e);
    }

}
