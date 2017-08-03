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

/**
 * Created by pilar on 8.6.2017.
 */
public abstract class YieldTask<T, R> extends Task<T> {
    private Consumer<R> onYield;
    private Timeline timeline;
    private Queue<R> queue;

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

    public void setOnYield(Consumer<R> consumer) {
        onYield = consumer;
    }

    protected void yield(R object) {
        queue.add(object);
    }
}