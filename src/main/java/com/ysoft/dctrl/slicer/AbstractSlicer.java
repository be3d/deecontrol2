package com.ysoft.dctrl.slicer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.slicer.cura.CuraParamMap;
import com.ysoft.dctrl.utils.DeeControlContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Created by kuhn on 4/4/2017.
 */
public abstract class AbstractSlicer implements Slicer {
    protected final EventBus eventBus;
    protected ObjectMapper objectMapper;

    public AbstractSlicer(EventBus eventBus, ObjectMapper objectMapper) throws IOException {
        super();
        this.eventBus = eventBus;
        this.objectMapper = objectMapper;
    }
}
