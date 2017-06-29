package com.ysoft.dctrl.slicer;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.slicer.param.SlicerParams;
import com.ysoft.dctrl.utils.DeeControlContext;
import javafx.scene.control.ProgressBar;
import com.ysoft.dctrl.slicer.cura.Cura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * Created by kuhn on 4/25/2017.
 *
 * Basic controller for slicer.
 * Loading, changing etc.
 *
 */
@Component
public class SlicerController {

    SlicerParams slicerParams;
    Map<String, Slicer> slicerMap;

    private final EventBus eventBus;

    public String selectedSlicerID = "";
    public static final String sceneSTL = System.getProperty("user.home") + File.separator + ".dctrl" + File.separator + ".slicer" + File.separator + "dctrl_scene.stl";
    private Slicer currenSlicer;


    @Autowired
    public SlicerController(EventBus eventBus, SlicerParams slicerParams, Map<String, Slicer> slicerMap) {
        this.eventBus = eventBus;
        this.slicerParams = slicerParams;
        this.slicerMap = slicerMap;
        this.setSlicer("Cura");
    }
    @PostConstruct
    private void initialize() {
        eventBus.subscribe(EventType.SCENE_EXPORTED.name(), (e) -> startSlice((String) e.getData()));
    }

    private void startSlice(String stlPath) {
        SlicerRunner slicerRunner = new SlicerRunner(eventBus, currenSlicer, slicerParams.getAllParams(), stlPath);
        new Thread(slicerRunner).start();
    }

    private void setSlicer(String id){
        this.currenSlicer = slicerMap.get(id);
        this.selectedSlicerID = id;
    }
}
