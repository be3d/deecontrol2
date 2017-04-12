package com.ysoft.dctrl.slicer.printer;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import com.ysoft.dctrl.slicer.SlicerParam;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by kuhn on 4/5/2017.
 */
public class PrinterManager {
    protected String PRINTER_DEF = "";
    protected final ObjectMapper objectMapper;
    protected final String DEFINITIONS_PATH = "src/main/resources/print/slicer/definitions/printer";

    public PrinterManager(ObjectMapper om){
        this.objectMapper = om;
    }

    public List<SlicerParam> collectParameters(Printers printer){
        switch(printer){
            case EDEE_V1:
                this.PRINTER_DEF = "eDee_v1";
                break;
            case DEERED_V1:
                this.PRINTER_DEF = "deeRed_v1";
                break;
        }
        return this.readPrinterDefinition();
    }

    protected List<SlicerParam> readPrinterDefinition() {
        List<SlicerParam> printerParameters = new ArrayList<>();

        JsonNode rootNode = null;
        try {
            rootNode = this.objectMapper.readTree(new File(DEFINITIONS_PATH + File.separator + this.PRINTER_DEF + ".def.json"));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        Iterator<Map.Entry<String,JsonNode>> fieldsIterator = rootNode.get("SLICER_PARAMS").fields();
        while (fieldsIterator.hasNext()) {
            Map.Entry<String,JsonNode> field = fieldsIterator.next();
            printerParameters.add(new SlicerParam(field.getKey(), field.getValue()));
        }

        return printerParameters;
    }

    public Map<Printers, List<SlicerParam>> getPrinters(){
        Map<Printers, List<SlicerParam>> printerList = new EnumMap<Printers, List<SlicerParam>>(Printers.class);
        for (Printers printer : Printers.values())
            printerList.put(Printers.valueOf(printer.name()),this.collectParameters(Printers.valueOf(printer.name())));
        return printerList;
    }

    //get speed profiles

    //get quality profiles

}
