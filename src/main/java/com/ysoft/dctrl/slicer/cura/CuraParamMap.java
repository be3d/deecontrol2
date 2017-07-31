package com.ysoft.dctrl.slicer.cura;

import com.ysoft.dctrl.slicer.param.SlicerParamType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by kuhn on 4/11/2017.
 */

public class CuraParamMap extends EnumMap<SlicerParamType, String>{

    private void init() {
        this.put(SlicerParamType.MACHINE_WIDTH,"machine_width");
        this.put(SlicerParamType.MACHINE_HEIGHT,"machine_height");
        this.put(SlicerParamType.MACHINE_DEPTH,"machine_depth");
        this.put(SlicerParamType.START_GCODE,"machine_start_gcode");
        this.put(SlicerParamType.END_GCODE,"machine_end_gcode");

        this.put(SlicerParamType.SPEED_PRINT,"speed_print");
        this.put(SlicerParamType.SPEED_OUTER_WALL,"speed_wall_0");
        this.put(SlicerParamType.SPEED_INNER_WALL,"speed_wall_x");
        this.put(SlicerParamType.SPEED_WALL,"speed_wall");
        this.put(SlicerParamType.SPEED_SOLID_LAYERS,"speed_topbottom");
        this.put(SlicerParamType.SPEED_SUPPORT,"speed_support");
        this.put(SlicerParamType.SPEED_ACCELERATION_CONTROL_ENABLED,"acceleration_enabled");
        this.put(SlicerParamType.SPEED_ACCELERATION_OUTER_WALL,"acceleration_wall_0");
        this.put(SlicerParamType.SPEED_JERK_CONTROL_ENABLED,"jerk_enabled");
        this.put(SlicerParamType.SPEED_JERK_OUTER_WALL,"jerk_wall_0");


        this.put(SlicerParamType.RESOLUTION_LAYER_HEIGHT,"layer_height");
        this.put(SlicerParamType.RESOLUTION_LAYER_HEIGHT_0,"layer_height_0");
        this.put(SlicerParamType.RESOLUTION_LINE_WIDTH_0,"wall_line_width_0");

        this.put(SlicerParamType.SHELL_THICKNESS, "wall_line_count");
        this.put(SlicerParamType.SHELL_TOP_LAYERS,"top_layers");
        this.put(SlicerParamType.SHELL_BOTTOM_LAYERS,"bottom_layers");
        this.put(SlicerParamType.SHELL_OUTER_WALL_INSET,"wall_0_inset");
        this.put(SlicerParamType.SHELL_Z_SEAM_X,"z_seam_x");
        this.put(SlicerParamType.SHELL_Z_SEAM_Y,"z_seam_y");

        this.put(SlicerParamType.INFILL_OVERLAP_MM,"infill_overlap_mm");
        this.put(SlicerParamType.INFILL_LAYER_THICKNESS,"infill_sparse_thickness");
        this.put(SlicerParamType.INFILL_PATTERN,"infill_pattern");
        this.put(SlicerParamType.INFILL_DENSITY,"infill_sparse_density");

        this.put(SlicerParamType.MATERIAL_PRINT_TEMPERATURE_DEFAULT,"default_material_print_temperature");
        this.put(SlicerParamType.MATERIAL_PRINT_TEMPERATURE,"material_print_temperature");
        this.put(SlicerParamType.MATERIAL_PRINT_TEMPERATURE_0,"material_print_temperature_layer_0");
        this.put(SlicerParamType.MATERIAL_DIAMETER,"material_diameter");
        this.put(SlicerParamType.MATERIAL_RETRACT_AT_LAYER_CHANGE,"retract_at_layer_change");
        this.put(SlicerParamType.MATERIAL_FLOW,"material_flow");
        this.put(SlicerParamType.MATERIAL_RETRACTION_DISTANCE,"retraction_amount");
        this.put(SlicerParamType.MATERIAL_RETRACTION_SPEED,"retraction_speed");
        this.put(SlicerParamType.MATERIAL_RETRACTION_PRIME_SPEED,"retraction_prime_speed");
        this.put(SlicerParamType.MATERIAL_RETRACTION_MINIMUM_TRAVEL,"retraction_min_travel");

        this.put(SlicerParamType.COOL_FAN_REGMAX_SPEED_THRESHOLD, "cool_min_layer_time_fan_speed_max");
        this.put(SlicerParamType.COOL_FAN_SPEED_REG_LAYER, "cool_fan_full_layer");
        this.put(SlicerParamType.COOL_MIN_LAYER_TIME, "cool_min_layer_time");
        this.put(SlicerParamType.COOL_LIFT_HEAD, "cool_lift_head");

        this.put(SlicerParamType.SUPPORT_ENABLED,"support_enable");
        this.put(SlicerParamType.SUPPORT_PLACEMENT,"support_type");
        this.put(SlicerParamType.SUPPORT_BUILDPLATE_TYPE,"adhesion_type");
        this.put(SlicerParamType.SUPPORT_BUILDPLATE_BRIM_LINES,"brim_line_count");
        this.put(SlicerParamType.SUPPORT_DENSITY,"support_infill_rate");
        this.put(SlicerParamType.SUPPORT_PATTERN,"support_pattern");
        this.put(SlicerParamType.SUPPORT_ANGLE,"support_angle");
        this.put(SlicerParamType.SUPPORT_CONNECT_ZIGZAGS,"support_connect_zigzags");
        this.put(SlicerParamType.SUPPORT_TOP_DISTANCE,"support_top_distance");
        this.put(SlicerParamType.SUPPORT_BOTTOM_DISTANCE,"support_bottom_distance");
        this.put(SlicerParamType.SUPPORT_XY_DISTANCE,"support_xy_distance");
        this.put(SlicerParamType.SUPPORT_STAIR_STEP_HEIGHT,"support_bottom_stair_step_height");
        this.put(SlicerParamType.SUPPORT_HORIZONTAL_EXPANSION,"support_offset");
        this.put(SlicerParamType.SUPPORT_INTERFACE_ENABLE,"support_interface_enable");
        this.put(SlicerParamType.SUPPORT_ROOF_THICKNESS,"support_roof_height");
        this.put(SlicerParamType.SUPPORT_BOTTOM_THICKNESS,"support_bottom_height");
        this.put(SlicerParamType.SUPPORT_INTERFACE_RESOLUTION,"support_interface_skip_height");
        this.put(SlicerParamType.SUPPORT_INTERFACE_DENSITY,"support_interface_density");
        this.put(SlicerParamType.SUPPORT_TOWER_DIAMETER,"support_tower_diameter");
        this.put(SlicerParamType.SUPPORT_MINIMUM_DIAMETER,"support_minimal_diameter");
    }

    public CuraParamMap(EnumMap<SlicerParamType, ? extends String> m) {
        super(m);
        this.init();
    }

    public CuraParamMap(Map<SlicerParamType, ? extends String> m) {
        super(m);
        this.init();
    }
    public CuraParamMap(Class<SlicerParamType> keyType) {
        super(keyType);
        this.init();
    }

}
