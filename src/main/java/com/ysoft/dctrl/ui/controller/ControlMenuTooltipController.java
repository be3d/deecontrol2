package com.ysoft.dctrl.ui.controller;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.ui.tooltip.RightAlignedTooltipPane;
import com.ysoft.dctrl.ui.tooltip.contract.TooltipData;
import com.ysoft.dctrl.utils.DeeControlContext;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by kuhn on 8/18/2017.
 */
@Controller
public class ControlMenuTooltipController extends LocalizableController implements Initializable {

    @FXML AnchorPane overlay;
    @FXML RightAlignedTooltipPane wrapper;
    @FXML Label title;
    @FXML HBox imageWrapper;
    @FXML ImageView image;
    @FXML Text text;
    @FXML Button closeBtn;

    @Autowired
    public ControlMenuTooltipController(LocalizationService localizationService, EventBus eventBus, DeeControlContext deeControlContext) {
        super(localizationService, eventBus, deeControlContext);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        wrapper.addEventHandler(MouseEvent.ANY, javafx.event.Event::consume);
        overlay.setOnMouseClicked(event -> hideTooltip());
        closeBtn.setOnAction(e -> hideTooltip());

        eventBus.subscribe(EventType.SHOW_TOOLTIP.name(), (e) -> {
            showTooltip((TooltipData)e.getData());
        });
        eventBus.subscribe(EventType.SLICER_PANEL_SCROLLED.name(), (e) -> {
            hideTooltip();
        });

        wrapper.init();
        super.initialize(location, resources);
    }

    private void showTooltip(TooltipData data){
        overlay.setVisible(true);
        insertContent(data);
        wrapper.alignToTargetElement(data.getTarget());
        wrapper.setVisible(true);
    }

    private void hideTooltip(){
        overlay.setVisible(false);
    }

    private void insertContent(TooltipData data){
        title.setText(data.getTitle());
        text.setText(data.getDescription());

        String path = data.getImagePath();
        if(path != null && !path.isEmpty()){
            image.setImage(new Image(path));
            imageWrapper.getStyleClass().clear();
            imageWrapper.getStyleClass().addAll("image-wrapper");
        } else {
            image.setImage(null);
            imageWrapper.getStyleClass().add("image-wrapper-collapsed");
        }
    }
}
