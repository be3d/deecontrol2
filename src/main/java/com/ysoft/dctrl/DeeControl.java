package com.ysoft.dctrl;

import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ysoft.dctrl.log.DeeControlConfigurationFactory;
import com.ysoft.dctrl.ui.BaseWindow;
import com.ysoft.dctrl.utils.DeeControlConfig;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Created by pilar on 20.3.2017.
 */
public class DeeControl extends Application {

    private ApplicationContext applicationContext;
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

        DeeControlContext deeControlContext = applicationContext.getBean(DeeControlContext.class);

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
    }
}
