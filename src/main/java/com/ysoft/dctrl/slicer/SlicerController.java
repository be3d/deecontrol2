package com.ysoft.dctrl.slicer;

import com.ysoft.dctrl.slicer.param.SlicerParams;
import javafx.scene.control.ProgressBar;
import com.ysoft.dctrl.slicer.cura.Cura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * Created by kuhn on 4/25/2017.
 *
 * Basic controller for slicer.
 * Loading, changing etc.
 *
 */
@Component
public class SlicerController {

    @Autowired
    SlicerParams slicerParams;

    public String selectedSlicerID = "";
    private final String sceneSTL = System.getProperty("user.home") + File.separator + ".dctrl" + File.separator + ".slicer" + File.separator + "dctrl_scene.stl";
    public Slicer slicer;

    public SlicerController() {
        this.setSlicer("CURA");
    }

    private void setSlicer(String id){
        try {
            switch(id){
                case "CURA":{
                    this.slicer = new Cura();
                    break;
                }
            }
            this.selectedSlicerID = id;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void slice(ProgressBar progress){
        try {
            this.slicer.run(slicerParams.getAllParams(), sceneSTL, progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
