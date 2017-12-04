package com.ysoft.dctrl.slicer.cura;

import com.ysoft.dctrl.slicer.param.SlicerParamType;

import java.util.*;

/**
 * Created by kuhn on 4/11/2017.
 */

public class CuraParamMap extends EnumMap<SlicerParamType, String>{

    public CuraParamMap(Class<SlicerParamType> keyType) {
        super(keyType);
        this.init();
    }

    private void init() {
        put(SlicerParamType.MACHINE_WIDTH,"machine_width");
        put(SlicerParamType.MACHINE_HEIGHT,"machine_height");
        put(SlicerParamType.MACHINE_DEPTH,"machine_depth");
        put(SlicerParamType.MACHINE_E0_NOZZLE_DIAMETER,"machine_nozzle_size");
        put(SlicerParamType.START_GCODE,"machine_start_gcode");
        put(SlicerParamType.END_GCODE,"machine_end_gcode");

        put(SlicerParamType.SPEED_PRINT,"speed_print");
        put(SlicerParamType.SPEED_OUTER_WALL,"speed_wall_0");
        put(SlicerParamType.SPEED_INNER_WALL,"speed_wall_x");
        put(SlicerParamType.SPEED_WALL,"speed_wall");
        put(SlicerParamType.SPEED_SOLID_LAYERS,"speed_topbottom");
        put(SlicerParamType.SPEED_SUPPORT,"speed_support");
        put(SlicerParamType.SPEED_ACCELERATION_CONTROL_ENABLED,"acceleration_enabled");
        put(SlicerParamType.SPEED_ACCELERATION_OUTER_WALL,"acceleration_wall_0");
        put(SlicerParamType.SPEED_JERK_CONTROL_ENABLED,"jerk_enabled");
        put(SlicerParamType.SPEED_JERK_OUTER_WALL,"jerk_wall_0");

        put(SlicerParamType.RESOLUTION_LAYER_HEIGHT,"layer_height");
        put(SlicerParamType.RESOLUTION_LAYER_HEIGHT_0,"layer_height_0");
        put(SlicerParamType.RESOLUTION_LINE_WIDTH_0,"wall_line_width_0");

        put(SlicerParamType.SHELL_THICKNESS, "wall_line_count");
        put(SlicerParamType.SHELL_TOP_LAYERS,"top_layers");
        put(SlicerParamType.SHELL_BOTTOM_LAYERS,"bottom_layers");
        put(SlicerParamType.SHELL_OUTER_WALL_INSET,"wall_0_inset");
        put(SlicerParamType.SHELL_Z_SEAM_X,"z_seam_x");
        put(SlicerParamType.SHELL_Z_SEAM_Y,"z_seam_y");

        put(SlicerParamType.INFILL_OVERLAP_MM,"infill_overlap_mm");
        put(SlicerParamType.INFILL_OVERLAP_MIN,"");
        put(SlicerParamType.INFILL_OVERLAP_MAX,"");
        put(SlicerParamType.INFILL_LAYER_THICKNESS,"infill_sparse_thickness");
        put(SlicerParamType.INFILL_PATTERN,"infill_pattern");
        put(SlicerParamType.INFILL_DENSITY,"infill_sparse_density");

        put(SlicerParamType.MATERIAL_PRINT_TEMPERATURE_DEFAULT,"default_material_print_temperature");
        put(SlicerParamType.MATERIAL_PRINT_TEMPERATURE,"material_print_temperature");
        put(SlicerParamType.MATERIAL_PRINT_TEMPERATURE_0,"material_print_temperature_layer_0");
        put(SlicerParamType.MATERIAL_DIAMETER,"material_diameter");
        put(SlicerParamType.MATERIAL_RETRACT_AT_LAYER_CHANGE,"retract_at_layer_change");
        put(SlicerParamType.MATERIAL_FLOW,"material_flow");
        put(SlicerParamType.MATERIAL_RETRACTION_DISTANCE,"retraction_amount");
        put(SlicerParamType.MATERIAL_RETRACTION_SPEED,"retraction_retract_speed");
        put(SlicerParamType.MATERIAL_RETRACTION_PRIME_SPEED,"retraction_prime_speed");
        put(SlicerParamType.MATERIAL_RETRACTION_MINIMUM_TRAVEL,"retraction_min_travel");

        put(SlicerParamType.COOL_FAN_REGMAX_SPEED_THRESHOLD, "cool_min_layer_time_fan_speed_max");
        put(SlicerParamType.COOL_FAN_SPEED_REG_LAYER, "cool_fan_full_layer");
        put(SlicerParamType.COOL_MIN_LAYER_TIME, "cool_min_layer_time");
        put(SlicerParamType.COOL_LIFT_HEAD, "cool_lift_head");

        put(SlicerParamType.SUPPORT_ENABLED,"support_enable");
        put(SlicerParamType.SUPPORT_PLACEMENT,"support_type");
        put(SlicerParamType.SUPPORT_BUILDPLATE_TYPE,"adhesion_type");
        put(SlicerParamType.SUPPORT_BUILDPLATE_BRIM_LINES,"brim_line_count");
        put(SlicerParamType.SUPPORT_DENSITY,"support_infill_rate");
        put(SlicerParamType.SUPPORT_PATTERN,"support_pattern");
        put(SlicerParamType.SUPPORT_ANGLE,"support_angle");
        put(SlicerParamType.SUPPORT_CONNECT_ZIGZAGS,"support_connect_zigzags");
        put(SlicerParamType.SUPPORT_TOP_DISTANCE,"support_top_distance");
        put(SlicerParamType.SUPPORT_BOTTOM_DISTANCE,"support_bottom_distance");
        put(SlicerParamType.SUPPORT_XY_DISTANCE,"support_xy_distance");
        put(SlicerParamType.SUPPORT_STAIR_STEP_HEIGHT,"support_bottom_stair_step_height");
        put(SlicerParamType.SUPPORT_HORIZONTAL_EXPANSION,"support_offset");
        put(SlicerParamType.SUPPORT_ROOF_ENABLE,"support_roof_enable");
        put(SlicerParamType.SUPPORT_ROOF_THICKNESS,"support_roof_height");
        put(SlicerParamType.SUPPORT_BOTTOM_ENABLE,"support_bottom_enable");
        put(SlicerParamType.SUPPORT_BOTTOM_THICKNESS,"support_bottom_height");
        put(SlicerParamType.SUPPORT_INTERFACE_RESOLUTION,"support_interface_skip_height");
        put(SlicerParamType.SUPPORT_INTERFACE_DENSITY,"support_interface_density");
        put(SlicerParamType.SUPPORT_INTERFACE_ROOF_LINE_DISTANCE,"support_roof_line_distance");
        put(SlicerParamType.SUPPORT_INTERFACE_BOTTOM_LINE_DISTANCE,"support_bottom_line_distance");
        put(SlicerParamType.SUPPORT_TOWER_DIAMETER,"support_tower_diameter");
        put(SlicerParamType.SUPPORT_MINIMUM_DIAMETER,"support_minimal_diameter");
    }

}
