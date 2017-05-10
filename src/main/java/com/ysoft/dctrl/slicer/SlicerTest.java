package com.ysoft.dctrl.slicer;

import com.ysoft.dctrl.event.*;
import com.ysoft.dctrl.slicer.cura.Cura;
import com.ysoft.dctrl.slicer.param.SlicerParam;
import com.ysoft.dctrl.slicer.param.SlicerParamType;
import com.ysoft.dctrl.slicer.printer.Printer;
import com.ysoft.dctrl.slicer.printer.PrinterResource;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by kuhn on 4/5/2017.
 */
public class SlicerTest {
    public static void main(String[] args) throws Exception{

        EventBus eventBus = new DefaultEventBus();
        // PRINTER
        PrinterResource printerResource = new PrinterResource(eventBus);
     //   SlicerManager slicerManager = new SlicerManager(eventBus, null, printerResource, new Cura());


        //select printer
        List<Printer> printerList = printerResource.getAllPrinters();
        printerResource.setPrinter(printerList.get(0)); // set eDee


       // Map<Printers,List<SlicerParam>> printers =  slicerManager.getPrinterList();
        //slicerManager.setPrinter(Printers.EDEE_V1.name());

        // FILAMENT
        //Map<Filaments,List<SlicerParam>> filaments = slicerManager.getFilamentList();
        //slicerManager.setFilament(Filaments.PLA.name());

        // PARAMETERS
//        Map<String, SlicerParam> params =  slicerManager.collectParameters();
//        boolean b = slicerManager.slicer.supportsParam("asdf");
//        boolean b2 = slicerManager.slicer.supportsParam("MACHINE_WIDTH");

        //List<SlicerParam> parameters = slicerManager.getParameterList(); // with ranges

//        params.get(SlicerParamType.LAYER_HEIGHT.name()).setValue(0.11);
       // slicerManager.setParam(SlicerParamType.LAYER_HEIGHT.name(), 0.21); // float

    // eventbus
        //eventBus.subscribe(EventType.PRINTER_CHANGE.name(), (SlicerManager) -> System.out.println("Hahaha"));

        // SLICE
        String modelSTL = System.getProperty("user.home") + File.separator + ".dctrl" + File.separator + ".slicer" + File.separator + "dctrl_scene.stl";;
      //  slicerManager.slice(modelSTL);


    }
}
