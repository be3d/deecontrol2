package com.ysoft.dctrl.ui.controller.controlMenu;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.ui.tooltip.contract.TooltipData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;


/**
 * Created by kuhn on 8/22/2017.
 */
public abstract class BaseTooltipControl extends BaseCustomControl {

    @FXML   HBox labelWrapper;
    @FXML   Button tooltipBtn;

    public void attachTooltip(EventBus eventBus, TooltipData data){
        if(tooltipBtn == null){ return; }
        data.setTarget(this);

        tooltipBtn.setOnAction(e -> {
            eventBus.publish(new Event(EventType.SHOW_TOOLTIP.name(), data));
        });
        labelWrapper.hoverProperty().addListener((observable, oldVal, newVal) ->{
            tooltipBtn.setVisible(newVal);
        });
    }
}
