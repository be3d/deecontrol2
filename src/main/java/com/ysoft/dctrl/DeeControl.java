package com.ysoft.dctrl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.ysoft.dctrl.ui.BaseWindow;
import com.ysoft.dctrl.utils.DeeControlConfig;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by pilar on 20.3.2017.
 */
public class DeeControl extends Application {
    private ApplicationContext applicationContext;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        applicationContext = new AnnotationConfigApplicationContext(DeeControlConfig.class);

        //DeeControlContext deeControlContext = applicationContext.getBean(DeeControlContext.class);

        BaseWindow baseWindow = applicationContext.getBean(BaseWindow.class);
        baseWindow.composeWindow(primaryStage);
        primaryStage.setTitle("DeeControl2");
        primaryStage.show();
    }
}
