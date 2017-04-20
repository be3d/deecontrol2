package com.ysoft.dctrl.slicer;

/**
 * Created by kuhn on 4/4/2017.
 */

/**
 * Slicer independent parameters.
 */
public enum SlicerParams {
    //machine
    MACHINE_WIDTH, MACHINE_HEIGHT, MACHINE_DEPTH,

    //speed
    PRINT_SPEED, PRINT_SPEED_SUPPORT, PRINT_SPEED_INFILL,

    //resolution
    LAYER_HEIGHT, LAYER_HEIGHT_0,

    PERIMETER_WIDTH, EXTRUSION_WIDTH,

    // platform adhesion
    ADHESION_TYPE,

    //
    START_GCODE, END_GCODE
}
