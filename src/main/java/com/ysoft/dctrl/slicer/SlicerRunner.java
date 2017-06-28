package com.ysoft.dctrl.slicer;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.slicer.param.SlicerParam;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kuhn on 5/29/2017.
 */
public class SlicerRunner extends Task<Slicer> {

    private final Slicer slicer;
    private final EventBus eventBus;
    private final String scene; // stl file path
    private final Map<String, SlicerParam> slicerParams;

    private Timer progressTimer;

    public SlicerRunner(EventBus eventBus, Slicer slicer, Map<String, SlicerParam> slicerParams,
                        String scene) {
        this.slicer = slicer;
        this.eventBus = eventBus;
        this.slicerParams = slicerParams;
        this.scene = scene;

        eventBus.subscribe(EventType.SLICER_STOP.name(), this::stopTask);
    }

    @Override
    protected Slicer call() throws Exception {
        progressTimer = new Timer();
        progressTimer.scheduleAtFixedRate(getUpdateTask(), 0, 250);

        try{
            slicer.run(slicerParams, scene, null);
        }catch(Exception e){
            System.out.println("Slicer fuck");
            e.printStackTrace();
        }

        return null;
    }

    private TimerTask getUpdateTask() {
        return new TimerTask() {
            @Override
            public void run() {
                eventBus.publish(new Event(EventType.SLICER_PROGRESS.name(), slicer.getProgress()));
            }
        };
    }

    @Override
    protected void succeeded(){
        this.cleanup();
        eventBus.publish(new Event(EventType.SLICER_FINISHED.name()));
    }

    @Override
    protected void cancelled(){
        this.cleanup();
        super.cancelled();
    }

    @Override protected void failed(){
        this.cleanup();
        super.failed();

    }

    public void stopTask(Event e){
        System.out.println("Cancelling slicer...");
        this.cancel();
    }

    private void cleanup(){
        progressTimer.cancel();
    }
}