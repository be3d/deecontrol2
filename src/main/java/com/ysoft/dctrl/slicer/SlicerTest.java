package com.ysoft.dctrl.slicer;

import com.ysoft.dctrl.slicer.filament.Filaments;
import com.ysoft.dctrl.slicer.printer.Printers;

import java.util.List;
import java.util.Map;

/**
 * Created by kuhn on 4/5/2017.
 */
public class SlicerTest {
    public static void main(String[] args) throws Exception{

        SlicerManager slicerManager = new SlicerManager(null, null);

        // PRINTER
        Map<Printers,List<SlicerParam>> printers =  slicerManager.getPrinterList();
        slicerManager.setPrinter(Printers.EDEE_V1.name());

        // FILAMENT
        Map<Filaments,List<SlicerParam>> filaments = slicerManager.getFilamentList();
        slicerManager.setFilament(Filaments.PLA.name());

        // PARAMETERS
        List<SlicerParam> parameters = slicerManager.getParameterList(); // with ranges

        slicerManager.setParam(SlicerParams.EXTRUSION_WIDTH.name(), "0.05"); // float

        // SLICE.
        slicerManager.slice();
    }
}
