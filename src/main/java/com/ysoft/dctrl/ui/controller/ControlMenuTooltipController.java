package com.ysoft.dctrl.ui.controller;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.i18n.LocalizationService;
import com.ysoft.dctrl.ui.tooltip.RightPanelTooltipPane;
import com.ysoft.dctrl.ui.tooltip.Tooltip;
import com.ysoft.dctrl.utils.DeeControlContext;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;
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
    @FXML RightPanelTooltipPane wrapper;
    @FXML Label title;
    @FXML HBox imageWrapper;
    @FXML Label imageLabel;
    @FXML HBox imageLabelWrapper;
    @FXML ImageView image;
    @FXML Text text;
    @FXML Button closeBtn;

    private static final int TOGGLE_DURATION_MILLIS = 2000;
    private Timeline timeline = null;
    private int activeToggleIndex = 0;


    @Autowired
    public ControlMenuTooltipController(LocalizationService localizationService, EventBus eventBus, DeeControlContext deeControlContext) {
        super(localizationService, eventBus, deeControlContext);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        wrapper.addEventHandler(MouseEvent.ANY, javafx.event.Event::consume);
        overlay.setOnMouseClicked(event -> hideTooltip());
        closeBtn.setOnAction(e -> hideTooltip());

        imageLabelWrapper.managedProperty().bind(imageLabelWrapper.visibleProperty());
        imageWrapper.managedProperty().bind(imageWrapper.visibleProperty());

        eventBus.subscribe(EventType.SHOW_TOOLTIP.name(), (e) -> {
            showTooltip((Tooltip)e.getData());
        });
        eventBus.subscribe(EventType.SLICER_PANEL_SCROLLED.name(), (e) -> {
            hideTooltip();
        });

        wrapper.init();
        super.initialize(location, resources);
    }

    private void showTooltip(Tooltip data){
        reset();
        insertContent(data);
        overlay.setVisible(true);
        wrapper.alignToTargetElement(data.getTarget());
        wrapper.setVisible(true);
    }

    private void hideTooltip(){
        reset();
        overlay.setVisible(false);
    }

    private void insertContent(Tooltip data){
        title.setText(getMessage(data.getTitle()));
        text.setText(getMessage(data.getDescription()));

        String[] imgPaths = data.getImgPaths();
        String[] imgLabels = data.getImgLabels();

        boolean hasImage = imgPaths != null && imgPaths.length > 0;
        boolean hasImageLabel = imgLabels != null && imgLabels.length > 0;

        if(hasImage){
            image.setImage(new Image(imgPaths[0]));
            if(imgPaths.length > 1) {
                attachAnimation(data.getImgPaths(), data.getImgLabels());
            }
        }
        if(hasImageLabel){
            imageLabel.setText(getMessage(imgLabels[0]));
        }

        imageWrapper.setVisible(hasImage);
        imageLabelWrapper.setVisible(hasImageLabel);

    }

    private void attachAnimation(String[] imgs, String[] labels){
        timeline = new Timeline(new KeyFrame(Duration.millis(TOGGLE_DURATION_MILLIS), event -> {
            int i = (++activeToggleIndex) % (imgs.length);
            image.setImage(new Image(imgs[i]));
            imageLabel.setText(getMessage(labels[i]));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void reset(){
        if(timeline != null){
            timeline.stop();
        }
        activeToggleIndex = 0;
    }
}
