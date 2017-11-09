package com.ysoft.dctrl.editor.importer;

import java.io.IOException;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.utils.YieldTask;

import com.ysoft.dctrl.utils.exceptions.RunningOutOfMemoryException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by pilar on 8.6.2017.
 */
public class YieldImportRunner<Y,R> extends YieldTask<Y,R> {
    private final Logger logger = LogManager.getLogger(YieldImportRunner.class);

    private EventBus eventBus;
    private YieldModelImporter<Y> importer;
    private String path;

    public YieldImportRunner(EventBus eventBus, YieldModelImporter<Y> importer, String path) {
        this.eventBus = eventBus;
        this.importer = importer;
        this.path = path;
    }

    @Override
    protected R call() throws RunningOutOfMemoryException, OutOfMemoryError, InterruptedException {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(200), (e) -> {
            eventBus.publish(new Event(EventType.MODEL_LOAD_PROGRESS.name(), importer.getProgress()));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        R result = null;
        try {
            importer.setOnYield(this::yield);
            result = (R)importer.load(path);
        } catch (IOException e) {
            logger.warn(e);
        }

        timeline.stop();
        return result;
    }
}