package com.ysoft.dctrl.slicer.param;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;

/**
 * Created by kuhn on 4/21/2017.
 *
 * Change of some parameters effects values of other parameters.
 * This map stores relationships between those parameters as function handles.
 *
 */
@Component
public class SlicerParamRelations {
    private final Logger logger = LogManager.getLogger(SlicerParamRelations.class);
    private final EventBus eventBus;

    private Map<SlicerParamType, Runnable> map;

    public SlicerParamRelations(EventBus eventBus) {
        this.eventBus = eventBus;
        map = new HashMap<>();
    }

    public void init(Map<String,SlicerParam> params){
        map.put(SlicerParamType.SUPPORT_BUILDPLATE_TYPE, () -> {
            SlicerParam p = params.get(SlicerParamType.SUPPORT_BUILDPLATE_TYPE.name());
            double extrusionWidth = (double) params.get(SlicerParamType.RESOLUTION_LINE_WIDTH_0.name()).getValue();
            int brimLineNumber = (int) params.get(SlicerParamType.SUPPORT_BUILDPLATE_BRIM_LINES.name()).getValue();
            double volumeOffset = 0;
            switch ((String) p.getValue()) {
                case "brim":
                    volumeOffset = extrusionWidth * brimLineNumber;
                    break;
                case "raft":
                    //number taken from fdmprinter configuration
                    volumeOffset = 15;
                    break;
            }

            eventBus.publish(new Event(EventType.PRINT_VOLUME_OFFSET_CHANGED.name(), volumeOffset));
        });

    }

    public void handleAll() {
        map.forEach((type, handler) -> handler.run());
    }

    public void handle(String paramID) {
        try{
            handle(SlicerParamType.valueOf(paramID));
        }
        catch (IllegalArgumentException e){
            logger.warn("Slicer parameter {} not found", paramID, e);
        }
    }

    public void handle(SlicerParamType slicerParamType) {
        if(map.containsKey(slicerParamType)) {
            map.get(slicerParamType).run();
        }
    }

}
