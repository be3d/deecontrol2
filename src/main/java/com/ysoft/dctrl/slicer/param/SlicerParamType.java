package com.ysoft.dctrl.slicer.param;

/**
 * Created by kuhn on 4/4/2017.
 */

/**
 * Slicer independent parameters.
 */
public enum SlicerParamType {
    //machine
    MACHINE_WIDTH,
    MACHINE_HEIGHT,
    MACHINE_DEPTH,
    MACHINE_E0_NOZZLE_DIAMETER,

    //speed
    SPEED_PRINT,
    SPEED_OUTER_WALL,
    SPEED_INNER_WALL,
    SPEED_SOLID_LAYERS,
    SPEED_WALL,
    SPEED_SUPPORT,
    SPEED_ACCELERATION_CONTROL_ENABLED,
    SPEED_ACCELERATION_OUTER_WALL,
    SPEED_JERK_CONTROL_ENABLED,
    SPEED_JERK_OUTER_WALL,

    //resolution
    RESOLUTION_LAYER_HEIGHT,
    RESOLUTION_LAYER_HEIGHT_0,
    RESOLUTION_LINE_WIDTH_0,

    //shell
    SHELL_THICKNESS,
    SHELL_TOP_LAYERS,
    SHELL_BOTTOM_LAYERS,
    SHELL_OUTER_WALL_INSET,
    SHELL_Z_SEAM_X,
    SHELL_Z_SEAM_Y,

    //infill
    INFILL_OVERLAP_MM,
    INFILL_LAYER_THICKNESS,
    INFILL_PATTERN,
    INFILL_DENSITY,

    //material
    MATERIAL_PRINT_TEMPERATURE_DEFAULT,
    MATERIAL_PRINT_TEMPERATURE,
    MATERIAL_PRINT_TEMPERATURE_0,
    MATERIAL_DIAMETER,
    MATERIAL_RETRACT_AT_LAYER_CHANGE,
    MATERIAL_FLOW,
    MATERIAL_RETRACTION_DISTANCE,
    MATERIAL_RETRACTION_SPEED,
    MATERIAL_RETRACTION_PRIME_SPEED,
    MATERIAL_RETRACTION_MINIMUM_TRAVEL,


    //cooling
    COOL_FAN_REGMAX_SPEED_THRESHOLD,
    COOL_FAN_SPEED_REG_LAYER,
    COOL_MIN_LAYER_TIME,
    COOL_LIFT_HEAD,

    //support
    SUPPORT_ENABLED,
    SUPPORT_PLACEMENT,
    SUPPORT_BUILDPLATE_TYPE,
    SUPPORT_BUILDPLATE_BRIM_LINES,
    SUPPORT_DENSITY,
    SUPPORT_PATTERN,
    SUPPORT_ANGLE,
    SUPPORT_CONNECT_ZIGZAGS,
    SUPPORT_TOP_DISTANCE,
    SUPPORT_BOTTOM_DISTANCE,
    SUPPORT_XY_DISTANCE,
    SUPPORT_STAIR_STEP_HEIGHT,
    SUPPORT_HORIZONTAL_EXPANSION,
    SUPPORT_ROOF_THICKNESS,
    SUPPORT_ROOF_ENABLE,
    SUPPORT_ROOF_LINE_DISTANCE,
    SUPPORT_BOTTOM_THICKNESS,
    SUPPORT_BOTTOM_ENABLE,
    SUPPORT_BOTTOM_LINE_DISTANCE,
    SUPPORT_INTERFACE_RESOLUTION,
    SUPPORT_INTERFACE_DENSITY,
    SUPPORT_INTERFACE_ROOF_LINE_DISTANCE,
    SUPPORT_INTERFACE_BOTTOM_LINE_DISTANCE,
    SUPPORT_TOWER_DIAMETER,
    SUPPORT_MINIMUM_DIAMETER,

    //
    START_GCODE,
    END_GCODE
}
