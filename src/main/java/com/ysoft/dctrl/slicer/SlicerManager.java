package com.ysoft.dctrl.slicer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.slicer.cura.Cura;
import com.ysoft.dctrl.slicer.filament.Filaments;
import com.ysoft.dctrl.slicer.printer.Printers;
import com.ysoft.dctrl.slicer.filament.FilamentManager;
import com.ysoft.dctrl.utils.DeeControlContext;

import com.ysoft.dctrl.slicer.printer.PrinterManager;
import com.ysoft.dctrl.slicer.filament.FilamentManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kuhn on 4/5/2017.
 */
public class SlicerManager {
    protected final EventBus eventBus;
    protected final DeeControlContext deeControlContext;
    protected final ObjectMapper objectMapper;

    protected PrinterManager printerManager;
    protected FilamentManager filamentManager;
    protected Slicer slicer;
    protected Map<String, Object> slicerParameters;


    public SlicerManager(EventBus eventBus, DeeControlContext deeControlContext) throws IOException {
        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
        this.objectMapper = new ObjectMapper(new JsonFactory());

        this.slicerParameters = new HashMap<String, Object>();
        this.slicer = new Cura(this.eventBus, this.objectMapper);
        this.printerManager = new PrinterManager(this.objectMapper);
        this.filamentManager = new FilamentManager();

    }

    public void setPrinter(String printerID){
        this.setUpSlicerParams(this.printerManager.collectParameters(Printers.valueOf(printerID)));
    }

    public Map<Printers,List<SlicerParam>> getPrinterList(){
        return this.printerManager.getPrinters();
    }

    public void setFilament(String filamentID){
        this.setUpSlicerParams(this.filamentManager.collectParameters(Filaments.valueOf(filamentID)));
    }

    // todo, depends on the selected printer
    public Map<Filaments,List<SlicerParam>> getFilamentList(){return null;}


    public void setParam(String paramID, Object data){
        // check if out of bounds
        this.slicerParameters.put(paramID, data);
    }

    // Collects everything from context and saves,
    public void saveUserProfile(){}

    // Replaces current context with userProfile values
    public void applyUserProfile(String userProfileID){}

    public List<SlicerParam> getParameterList(){
        // ask Slicer/printer for supported parameters
        return null;
    }

    public void slice() throws Exception {
        this.slicer.run(slicerParameters);
    }

    public void registerProgressEvent() {
        //eventBus.subscribe(EventType.CHANGE_LANGUAGE.name(), this::onTranslate);
    }

    private void setUpSlicerParams(List<SlicerParam> slicerParams){
        if (slicerParams != null){
            for(SlicerParam p : slicerParams){
                this.slicerParameters.put(p.id, p.data);
            }
        }
    }
}
