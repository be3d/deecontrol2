package com.ysoft.dctrl.ui.tooltip;

/**
 * Created by kuhn on 9/13/2017.
 */
public enum TooltipDefinition {

    RAFT(
            "slicer_init_platform", "slicer_init_platform_tooltip",
            new String[]{"img/tooltip/off.png", "img/tooltip/on.png"},
            new String[]{"slicer_init_platform_tooltip_raft", "slicer_init_platform_tooltip_none", "slicer_init_platform_tooltip_brim"}
    ),
    SUPPORT(
            "slicer_supports", "slicer_supports_tooltip",
            new String[]{"img/tooltip/on.png", "img/tooltip/off.png"},
            new String[]{"slicer_supports_tooltip_on", "slicer_supports_tooltip_off"}
    ),
    LAYER_HEIGHT(
            "slicer_layer_height", "slicer_layer_height_tooltip"
//                new String[]{"img/tooltip/on.png", "img/tooltip/off.png"},
//                new String[]{"slicer_supports_tooltip_on", "slicer_supports_tooltip_off"}
    ),
    ROOF_THICKNESS(
            "slicer_roof_thickness", "slicer_roof_thickness_tooltip"
//                new String[]{"img/tooltip/on.png", "img/tooltip/off.png"},
    ),
    BOTTOM_THICKNESS(
            "slicer_bottom_thickness", "slicer_bottom_thickness_tooltip"
    ),
    PRINT_SPEED_SOLID(
            "slicer_speed_solid", "slicer_speed_solid_tooltip"
    ),
    SHELL_THICKNESS(
            "slicer_shell_thickness", "slicer_shell_thickness_tooltip"
    ),
    PRINT_SPEED_SHELL(
            "slicer_speed_shell", "slicer_speed_shell_tooltip"
    ),
    INFILL_PATTERN(
            "slicer_infill_pattern", "slicer_infill_pattern_tooltip"
    ),
    INFILL_DENSITY(
            "slicer_infill_density", "slicer_infill_density_tooltip"
    ),
    SUPPORT_DENSITY(
            "slicer_support_density", "slicer_support_density_tooltip"
    ),
    SUPPORT_PATTERN(
            "slicer_support_pattern", "slicer_support_pattern_tooltip"
    ),
    SUPPORT_ANGLE(
            "slicer_support_angle", "slicer_support_angle_tooltip"
    );

    private String title;
    private String text;
    private String[] imgPaths;
    private String[] imgLabels;

    TooltipDefinition(String title, String text, String[] imgPaths, String[] imgLabels){
        this.title = title;
        this.text = text;
        this.imgPaths = imgPaths;
        this.imgLabels = imgLabels;
    }

    TooltipDefinition(String title, String text){
        this(title, text, new String[]{}, new String[]{});
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String[] getImgPaths() {
        return imgPaths;
    }

    public String[] getImgLabels() {
        return imgLabels;
    }
}
