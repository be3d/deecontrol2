package com.ysoft.dctrl.editor.importer;

import java.io.IOException;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;

import com.ysoft.dctrl.utils.exceptions.RunningOutOfMemoryException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import com.ysoft.dctrl.utils.YieldTask;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by pilar on 13.4.2017.
 */
public class ImportRunner<R> extends YieldTask<Void,R> {
    private final Logger logger = LogManager.getLogger(YieldImportRunner.class);

    private final ModelImporter<R> modelImporter;
    private final String path;
    private final EventBus eventBus;

    public ImportRunner(EventBus eventBus, ModelImporter<R> modelImporter, String path) {
        this.modelImporter = modelImporter;
        this.path = path;
        this.eventBus = eventBus;
    }

    @Override
    protected R call() throws IOException, RunningOutOfMemoryException, OutOfMemoryError, InterruptedException {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), (e) -> {
            eventBus.publish(new Event(EventType.MODEL_LOAD_PROGRESS.name(), modelImporter.getProgress()));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        R result;
        try {
            result = modelImporter.load(path);
        }finally {
            timeline.stop();
        }

        return result;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        modelImporter.cancel();
        return super.cancel(mayInterruptIfRunning);
    }
}
