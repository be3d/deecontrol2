package com.ysoft.dctrl.slicer.printer;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by kuhn on 4/5/2017.
 */

@Component
public class PrinterResource {

    private static ObjectMapper objectMapper;
    private static EventBus eventBus;

    private static final String DEFINITIONS_PATH = "/print/slicer/definitions/printer";

    List<Printer> printers;
    Printer selectedPrinter;

    @Autowired
    public PrinterResource(EventBus eventBus){
        System.err.println("Printer resource init");
        objectMapper = new ObjectMapper();

        this.eventBus = eventBus;
        this.printers = loadPrinters();
    }

    private List<Printer> loadPrinters(){
        List<Printer> printers = new ArrayList<>();
        File [] printerFiles = new File[0];

        try {
            URL printerDefinitions = PrinterResource.class.getResource(DEFINITIONS_PATH);

            if (printerDefinitions == null) throw new IOException("Printer definitions folder not found.");

            System.out.println(printerDefinitions.toURI());
            System.out.println(Paths.get(printerDefinitions.toURI()));
            File printerDefinitionsFolder = Paths.get(printerDefinitions.toURI()).toFile();
            printerFiles = printerDefinitionsFolder.listFiles();

            if (printers == null) throw new IOException("No printers found.");

        } catch ( IOException e) {
            System.out.println( e.getMessage());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        for(File f : printerFiles){
            if(f.isFile()){
                try{
                    Printer p = this.objectMapper.readValue(f, Printer.class);
                    if (p.id != null){
                        printers.add(p);
                    }
                } catch (JsonMappingException e){
                    e.printStackTrace();
                } catch ( IOException e){
                    System.out.println("Printer definition error." + f.toString() + " " + e.getMessage());
                    e.printStackTrace();
                }catch (IllegalArgumentException e){
                    System.out.println("Printer parameter parsing error. " + f.toString() + " " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
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
