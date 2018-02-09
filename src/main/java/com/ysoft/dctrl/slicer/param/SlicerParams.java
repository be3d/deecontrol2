package com.ysoft.dctrl.slicer.param;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.slicer.cura.Cura;
import com.ysoft.dctrl.slicer.printer.Printer;
import com.ysoft.dctrl.slicer.printer.PrinterResource;
import com.ysoft.dctrl.utils.DeeControlContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kuhn on 4/24/2017.
 *
 * This class holds map of current slicer parameters and manages their changes
 * caused by Controllers, Printer selection, Profile selection...
 *
 */
@Component
public class SlicerParams {
    private final Logger logger = LogManager.getLogger(SlicerParams.class);

    protected final EventBus eventBus;
    protected final DeeControlContext deeControlContext;

    protected final PrinterResource printerResource;
    protected final SlicerParamRelations paramRelations;

    protected Map<String, SlicerParam> slicerParameters;

    @Autowired
    public SlicerParams(EventBus eventBus, DeeControlContext deeControlContext,
                        PrinterResource printerResource,
                        SlicerParamRelations paramRelations)  {

        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
        this.printerResource = printerResource;
        this.paramRelations = paramRelations;

        eventBus.subscribe(EventType.PRINTER_CHANGED.name(), this::printerChanged );
    }

    @PostConstruct
    private void init(){
        try{
            printerResource.setPrinter("edee");
        }catch(Exception e){
            logger.warn("Printer EDEE could not be set.");
        }

        slicerParameters = loadParams();
        paramRelations.init(slicerParameters);
        paramRelations.handleAll();
    }

    public Map<String, SlicerParam> loadParams(){
        Printer selectedPrinter = printerResource.getPrinter();
        if (selectedPrinter != null){

            List<SlicerParam> params = selectedPrinter.getAllParams();
            Map<String, SlicerParam> slicerParameters = new HashMap<>();

            for (SlicerParam p: params){
                slicerParameters.put(p.getId(), p);
            }
            return slicerParameters;

        } else {
            logger.warn("No printer selected.");
            return null;
        }
    }

    public void updateParam(String paramID, Object value){
        slicerParameters.get(paramID).setValue(value);
        paramRelations.handle(paramID);
        eventBus.publish(new Event(EventType.SLICER_PARAM_CHANGED.name(), slicerParameters.get(paramID)));
    }

    public void updateParams(List<SlicerParam> params){
        if (params != null){
            for (SlicerParam p : params){
                updateParam(p.getId(), p.getValue());
            }
        }
    }

    public void updateProfileDefaults(List<SlicerParam> params){
        if (params != null){
            for (SlicerParam p : params){
                slicerParameters.get(p.getId()).setProfileDefault(p.getValue());
            }
        } else {
            for (SlicerParam p : slicerParameters.values()){
                p.setProfileDefault(p.getDefaultValue());
            }
        }
    }

    public Map<String, SlicerParam> getAllParams(){
        return slicerParameters;
    }

    public SlicerParam get(String name){
        return slicerParameters.get(name);
    }

    public void resetToDefault(){
        for (SlicerParam p : slicerParameters.values()){
            p.resetToDefault();
        }
    }

    public void printerChanged(Event event){
        this.slicerParameters = loadParams();
    }
}
