package com.ysoft.dctrl.slicer.cura;

import com.ysoft.dctrl.slicer.param.SlicerParamType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by kuhn on 4/11/2017.
 */

public class CuraParamMap<T> extends EnumMap<SlicerParamType, String>{

    private void init() {
        this.put(SlicerParamType.MACHINE_WIDTH,"machine_width");
        this.put(SlicerParamType.MACHINE_HEIGHT,"machine_height");
        this.put(SlicerParamType.MACHINE_DEPTH,"machine_depth");
        this.put(SlicerParamType.START_GCODE,"machine_start_gcode");
        this.put(SlicerParamType.END_GCODE,"machine_end_gcode");

        this.put(SlicerParamType.SPEED_PRINT,"speed_print");
        this.put(SlicerParamType.SPEED_OUTER_WALL,"speed_wall_0");
        this.put(SlicerParamType.SPEED_INNER_WALL,"speed_wall_x");

        this.put(SlicerParamType.LAYER_HEIGHT,"layer_height");
        this.put(SlicerParamType.LAYER_HEIGHT_0,"layer_height_0");

        this.put(SlicerParamType.SHELL_THICKNESS, "wall_thickness");
        this.put(SlicerParamType.SHELL_TOP_BOTTOM_THICKNESS,"top_bottom_thickness");
        this.put(SlicerParamType.SHELL_TOP_LAYERS,"top_layers");
        this.put(SlicerParamType.SHELL_BOTTOM_LAYERS,"bottom_layers");
        this.put(SlicerParamType.SHELL_TOP_THICKNESS,"top_thickness");

        this.put(SlicerParamType.INFILL_OVERLAP_PERCENTAGE,"infill_overlap");
        this.put(SlicerParamType.INFILL_LAYER_THICKNESS,"infill_sparse_thickness");

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
        this.put(SlicerParamType.COOL_FAN_SPEED_REG_LAYER, "cool_fan_full_at_height");

        this.put(SlicerParamType.SUPPORT_ENABLED,"support_enable");
        this.put(SlicerParamType.SUPPORT_BUILDPLATE_TYPE,"adhesion_type");

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
