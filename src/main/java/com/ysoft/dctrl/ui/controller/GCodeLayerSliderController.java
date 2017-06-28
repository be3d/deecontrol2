package com.ysoft.dctrl.ui.controller;

import com.ysoft.dctrl.editor.GCodeViewer;
import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.mesh.GCodeLayer;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.i18n.LocalizationResource;
import com.ysoft.dctrl.utils.DeeControlContext;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by kuhn on 6/21/2017.
 */
@Controller
public class GCodeLayerSliderController extends LocalizableController implements Initializable {

    protected SceneGraph sceneGraph;
    protected GCodeViewer gCodeViewer;

    protected ArrayList<GCodeLayer> layers = new ArrayList<>();

    @FXML
    AnchorPane gCodeLayerPickerPane;
    @FXML
    Slider layerSlider;

    public GCodeLayerSliderController(LocalizationResource localizationResource,
                                      EventBus eventBus, DeeControlContext context,
                                      GCodeViewer gCodeViewer) {

        super(localizationResource, eventBus, context);
        this.gCodeViewer = gCodeViewer;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        eventBus.subscribe(EventType.GCODE_IMPORT_COMPLETED.name(), this::initSlider);
        eventBus.subscribe(EventType.GCODE_VIEWER_CLOSE.name(), (e) -> gCodeLayerPickerPane.setVisible(false));

        super.initialize(location, resources);
    }

    private void initSlider(Event e ){
        layers = (ArrayList<GCodeLayer>)e.getData();

        layerSlider.setMin(1);
        layerSlider.setMax(layers.size()-1);
        layerSlider.setValue(layers.size()-1);

        layerSlider.valueProperty().addListener((observable, oldValue, newValue) -> {

            if (oldValue.intValue() == newValue.intValue())
                return;

            eventBus.publish(new Event(EventType.GCODE_LAYER_RENDER_DETAIL.name(), layers.get(newValue.intValue())));
            eventBus.publish(new Event(EventType.GCODE_LAYER_REMOVE_DETAIL.name(), layers.get(oldValue.intValue())));
            gCodeViewer.cutViewAtLayer(newValue.intValue());

        });

        gCodeLayerPickerPane.setVisible(true);
    }

    private void setVisible(boolean value){
        gCodeLayerPickerPane.setVisible(value);
    }
}
