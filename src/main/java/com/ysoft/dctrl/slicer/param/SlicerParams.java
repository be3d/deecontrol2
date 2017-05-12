package com.ysoft.dctrl.slicer.param;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.slicer.cura.Cura;
import com.ysoft.dctrl.slicer.printer.Printer;
import com.ysoft.dctrl.slicer.printer.PrinterResource;
import com.ysoft.dctrl.utils.DeeControlContext;
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
    protected final EventBus eventBus;
    protected final DeeControlContext deeControlContext;
    protected final PrinterResource printerResource;
    protected final Cura slicer;
    protected final SlicerParamRelations paramRelations;


    protected Map<String, SlicerParam> slicerParameters;
    protected boolean isEdited = false;

    @Autowired
    public SlicerParams(EventBus eventBus, DeeControlContext deeControlContext, PrinterResource printerResource, Cura slicer, SlicerParamRelations paramRelations) throws IOException {
        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
        this.slicer = slicer;
        this.printerResource = printerResource;
        this.paramRelations = paramRelations;


        eventBus.subscribe(EventType.PRINTER_CHANGED.name(), this::printerChanged );
        eventBus.subscribe(EventType.SLICER_PARAM_CHANGED.name(), this::slicerParamChanged );
    }

    @PostConstruct
    private void initParams(){

        List<Printer> printerList =  printerResource.getAllPrinters();
        try{
            this.printerResource.setPrinter("edee");
        }catch(Exception e){
            System.out.println("Printer EDEE could not be set.");
        }

        this.slicerParameters = this.loadParams();
        this.paramRelations.init(this.slicerParameters);
    }

    public Map<String, SlicerParam> loadParams(){
        Printer selectedPrinter = this.printerResource.getPrinter();
        if (selectedPrinter != null){
            List<SlicerParam> params = this.slicer.filterSupportedParams(selectedPrinter.getAllParams());
            Map<String, SlicerParam> slicerParameters = new HashMap<>();

            // todo reconsider not using Map but list
            for (SlicerParam p: params){
                slicerParameters.put(p.id, p);
            }
            return slicerParameters;

        } else {
            System.out.println("No printer selected.");
            return null;
        }
    }

    public void printerChanged(Event event){
        this.slicerParameters = loadParams();
        // reload available profiles...
        System.out.println("PRINTER CHANGED diff to parameters.");
    }

    public void slicerParamChanged(Event event){
        try{
            (paramRelations.getHandle(SlicerParamType.valueOf((String)event.getData()))).run();
        }catch (NullPointerException e){
            // linked parameter does not have to exist
        }
    }

    public void updateParam(String paramID, Object value){
        this.slicerParameters.get(paramID).setVal(value);
        this.eventBus.publish(new Event(EventType.SLICER_PARAM_CHANGED.name(), paramID));
    }

    public void updateParams(List<SlicerParam> params){
        for (SlicerParam p : params){
            this.updateParam(p.id, p.getValue());
        }
    }

    public void updateParams(Map<String, SlicerParam> params){
        for (String key : params.keySet()){
            this.updateParam(key, params.get(key));
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
