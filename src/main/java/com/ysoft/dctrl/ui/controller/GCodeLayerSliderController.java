package com.ysoft.dctrl.ui.controller;

import com.ysoft.dctrl.editor.GCodeSceneGraph;
import com.ysoft.dctrl.editor.SceneMode;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.controller.controlMenu.RangeSliderVertical;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.utils.DeeControlContext;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by kuhn on 6/21/2017.
 */
@Controller
public class GCodeLayerSliderController extends LocalizableController implements Initializable {
    private GCodeSceneGraph gcodeSceneGraph;

    @FXML    AnchorPane gCodeLayerPickerPane;
    @FXML    RangeSliderVertical layerSlider;

    public GCodeLayerSliderController(LocalizationService localizationService,
                                      EventBus eventBus, DeeControlContext context,
                                      GCodeSceneGraph gcodeSceneGraph) {

        super(localizationService, eventBus, context);
        this.gcodeSceneGraph = gcodeSceneGraph;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        eventBus.subscribe(EventType.GCODE_DRAFT_RENDER_FINISHED.name(), (e) -> initSlider((Integer) e.getData()));
        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), (e) -> setVisible(e.getData() == SceneMode.GCODE));

        super.initialize(location, resources);
    }

    private void initSlider(Integer layerCount){
        layerSlider.setMin(1);
        layerSlider.setMax(layerCount+1);
        layerSlider.topValueProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.intValue() == newValue.intValue()) { return; }
            gcodeSceneGraph.cutViewAtLayer(newValue.intValue());
        });
        layerSlider.setTopValue(layerCount+1);
    }

    private void setVisible(boolean value){
        gCodeLayerPickerPane.setVisible(value);
    }
}
