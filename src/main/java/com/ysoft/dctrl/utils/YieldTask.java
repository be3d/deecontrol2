package com.ysoft.dctrl.utils;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by pilar on 8.6.2017.
 */
public abstract class YieldTask<Y,R> extends Task<R> {
    private final Logger logger = LogManager.getLogger(YieldTask.class);

    private Consumer<Y> onYield;
    private Timeline timeline;
    private Queue<Y> queue;

    public YieldTask() {
        super();
        init();
    }

    private void init() {
        onYield = null;
        queue = new ConcurrentLinkedQueue<>();
        timeline = new Timeline(new KeyFrame(Duration.millis(4), (e) -> {
            yielding();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        setOnFailed((t) -> logger.error("YieldTask failed", t.getSource().getException()));
    }

    private synchronized void yielding() {
        if(queue.isEmpty()) {
            switch (getState()) {
                case SUCCEEDED:
                case CANCELLED:
                case FAILED:
                    timeline.stop();
                    timeline.getKeyFrames().clear();
            }
            return;
        } else if(onYield  == null) {
            return;
        }
        onYield.accept(queue.poll());
    }

    public void setOnYield(Consumer<Y> consumer) {
        onYield = consumer;
    }

    protected void yield(Y object) {
        queue.add(object);
    }
}