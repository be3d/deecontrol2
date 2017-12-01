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
    protected final Cura slicer;
    protected final SlicerParamRelations paramRelations;


    protected Map<String, SlicerParam> slicerParameters;
    protected boolean isEdited = false;

    @Autowired
    public SlicerParams(EventBus eventBus, DeeControlContext deeControlContext,
                        PrinterResource printerResource, Cura slicer,
                        SlicerParamRelations paramRelations) throws IOException {

        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
        this.slicer = slicer;
        this.printerResource = printerResource;
        this.paramRelations = paramRelations;

        eventBus.subscribe(EventType.PRINTER_CHANGED.name(), this::printerChanged );
    }

    @PostConstruct
    private void initParams(){

        try{
            this.printerResource.setPrinter("edee");
        }catch(Exception e){
            logger.warn("Printer EDEE could not be set.");
        }

        this.slicerParameters = this.loadParams();
        this.paramRelations.init(this.slicerParameters);
        this.paramRelations.handleAll();
    }

    public Map<String, SlicerParam> loadParams(){
        Printer selectedPrinter = this.printerResource.getPrinter();
        if (selectedPrinter != null){
            List<SlicerParam> params = this.slicer.filterSupportedParams(selectedPrinter.getAllParams());
            Map<String, SlicerParam> slicerParameters = new HashMap<>();

            // todo reconsider not using Map but list
            for (SlicerParam p: params){
                slicerParameters.put(p.getId(), p);
            }
            return slicerParameters;

        } else {
            logger.warn("No printer selected.");
            return null;
        }
    }

    public void printerChanged(Event event){
        this.slicerParameters = loadParams();
    }

    public void updateParam(String paramID, Object value){
        slicerParameters.get(paramID).setValue(value);
        paramRelations.handle(paramID);
        eventBus.publish(new Event(EventType.SLICER_PARAM_CHANGED.name(), slicerParameters.get(paramID)));
    }

    public void updateParams(List<SlicerParam> params){
        if (params != null){
            for (SlicerParam p : params){
                this.updateParam(p.getId(), p.getValue());
            }
        }
    }

    public Map<String, SlicerParam> getAllParams(){
        return this.slicerParameters;
    }

    /**
     *
     * @param name ID of the parameter <- (SlicerParamType)
     * @return
     */
    public SlicerParam get(String name){
        return this.slicerParameters.get(name);
    }

    public void resetToDefault(){
        for (SlicerParam p : this.slicerParameters.values()){
            p.resetToDefault();
        }
    }
}
