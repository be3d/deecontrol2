package com.ysoft.dctrl.slicer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.slicer.cura.CuraParamMap;
import com.ysoft.dctrl.utils.DeeControlContext;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


/**
 * Created by kuhn on 4/4/2017.
 */
@Component
public abstract class AbstractSlicer implements Slicer {

    public AbstractSlicer() throws IOException {}

}
