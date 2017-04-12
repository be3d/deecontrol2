package com.ysoft.dctrl.slicer.filament;

import com.ysoft.dctrl.slicer.SlicerParam;

import java.util.List;

import static com.ysoft.dctrl.slicer.printer.Printers.DEERED_V1;
import static com.ysoft.dctrl.slicer.printer.Printers.EDEE_V1;

/**
 * Created by kuhn on 4/4/2017.
 */
public class FilamentManager {
    protected String FILAMENT_DEF = "";
    public FilamentManager(){}

        public List<SlicerParam> collectParameters(Filaments filament){
            switch(filament){
                case PLA:
                    this.FILAMENT_DEF = "pla.def.json";
                    break;
                case ABS:
                    this.FILAMENT_DEF = "abs.def.json";
                    break;
            }
            return this.readFilamentDefinition();
        }
        public List<SlicerParam> readFilamentDefinition(){
            // todo read the file to json
            return null;
        }


}
