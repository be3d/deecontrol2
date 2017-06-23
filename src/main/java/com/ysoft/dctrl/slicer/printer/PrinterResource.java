package com.ysoft.dctrl.slicer.printer;

import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.slicer.AbstractConfigResource;
import com.ysoft.dctrl.utils.DeeControlContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


/**
 * Created by kuhn on 4/5/2017.
 */

@Component
public class PrinterResource extends AbstractConfigResource {

    private static final String DEFINITIONS_PATH = "print/slicer/definitions/printer";

    private List<Printer> printers;
    private Printer selectedPrinter;

    @Autowired
    public PrinterResource(EventBus eventBus, DeeControlContext deeControlContext){
        super(eventBus, deeControlContext);
        this.printers = loadPrinters();
    }

    private List<Printer> loadPrinters(){
        List<Printer> printers = super.loadObjects(DEFINITIONS_PATH, Printer.class, true);
        return printers;
    }

    public List<Printer> getAllPrinters(){
        return this.printers;
    }

    public void setPrinter(Printer printer){
        this.selectedPrinter = printer;
        this.eventBus.publish(new Event(EventType.PRINTER_CHANGED.name()));
    }

    public void setPrinter(String printerID){
        for (Printer p : printers){
            if(p.id.equals(printerID)) {
                this.setPrinter(p);
                return;
            }
        }
        System.out.println("PrinterResource: Printer " + printerID + "could not be set.");
    }

    public Printer getPrinter() {
        return this.selectedPrinter;
    }

}
