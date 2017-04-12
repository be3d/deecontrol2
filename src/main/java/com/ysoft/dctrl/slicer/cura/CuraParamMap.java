package com.ysoft.dctrl.slicer.cura;

import com.ysoft.dctrl.slicer.SlicerParams;
import java.util.EnumMap;

/**
 * Created by kuhn on 4/11/2017.
 */
public class CuraParamMap {
    public static final EnumMap<SlicerParams, String> map = new EnumMap<>(SlicerParams.class);

    static {
        map.put(SlicerParams.MACHINE_WIDTH,"machine_width");
        map.put(SlicerParams.MACHINE_HEIGHT,"machine_height");
        map.put(SlicerParams.MACHINE_DEPTH,"machine_depth");

        map.put(SlicerParams.PRINT_SPEED,"speed_print");
        map.put(SlicerParams.PRINT_SPEED_INFILL,"speed_infill");
        map.put(SlicerParams.PRINT_SPEED_SUPPORT,"speed_support");

        map.put(SlicerParams.LAYER_HEIGHT,"layer_height");
        map.put(SlicerParams.LAYER_HEIGHT_0,"layer_height");

        map.put(SlicerParams.ADHESION_TYPE,"adhesion_type");

        // todo find out why cura doesnt start with these
        //  map.put(SlicerParams.START_GCODE,"machine_start_gcode");
        // map.put(SlicerParams.END_GCODE,"machine_end_gcode");
    }
}
