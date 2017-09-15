package com.ysoft.dctrl.ui.tooltip;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Component;


/**
 * Created by kuhn on 8/25/2017.
 */
@Component
public class RightPanelTooltipPane extends HBox  {

    @FXML VBox arrowWrapper;

    final static double topOffset = 2; // relative to the label it belongs to
    final static double bottomOffset = 78;
    final static double bottomOffscreen = 30; // Indicates the limit from where tooltip is considered "offscreen"

    private double height;
    private Node target;

    public void init() {
        arrowWrapper = new VBox();
        arrowWrapper.getStyleClass().addAll("arrow-wrapper", "arrow-top");
        this.getChildren().add(arrowWrapper);

        this.boundsInLocalProperty().addListener((o, oldVal, newVal) -> {
            if(newVal.getHeight() == height){ return; }
            height = newVal.getHeight();
            updateAlignment();
        });
    }


    public void alignToTargetElement(Node target) {
        this.target = target;
        updateAlignment();
    }

    public void updateAlignment(){
        if(target == null){ return; }

        double targetElementYPos = (target.localToScene(target.getBoundsInLocal())).getMinY();
        this.setTranslateY(targetElementYPos + topOffset);

        double canvasMaxY = this.getParent().getParent().getBoundsInParent().getMaxY();
        double tooltipMaxY = this.getBoundsInParent().getMaxY();
        double aboveLabelTooltipY = targetElementYPos - this.getBoundsInParent().getHeight() + bottomOffset;

        if (tooltipMaxY > canvasMaxY - bottomOffscreen && aboveLabelTooltipY > 0) {
            //Tooltip is outside the window, move it so it is aligned above the label
            this.setTranslateY(aboveLabelTooltipY);
            setTooltipStyle(TooltipAlignment.BOTTOM);
        } else {
            //Normally it's aligned below the label
            this.setTranslateY(targetElementYPos + topOffset);
            setTooltipStyle(TooltipAlignment.TOP);
        }
    }

    private void setTooltipStyle(TooltipAlignment align){
        arrowWrapper.getStyleClass().clear();
        arrowWrapper.getStyleClass().add("arrow-wrapper");

        switch(align){
            case TOP: arrowWrapper.getStyleClass().add("arrow-top");
                break;
            case BOTTOM: {
                arrowWrapper.getStyleClass().add("arrow-bottom");
            }
            break;
        }
    }

    enum TooltipAlignment{TOP, BOTTOM }
}

