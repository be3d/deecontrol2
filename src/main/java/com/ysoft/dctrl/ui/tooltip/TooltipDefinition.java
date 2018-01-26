package com.ysoft.dctrl.ui.tooltip;

/**
 * Created by kuhn on 9/13/2017.
 */
public enum TooltipDefinition {

    RAFT(
            "slicer_init_platform", "slicer_init_platform_tooltip",
            new String[]{"img/tooltip/init_platform_raft.png", "img/tooltip/init_platform_none.png", "img/tooltip/init_platform_brim.png"},
            new String[]{"slicer_init_platform_tooltip_raft", "slicer_init_platform_tooltip_none", "slicer_init_platform_tooltip_brim"}
    ),
    SUPPORT(
            "slicer_supports", "slicer_supports_tooltip",
            new String[]{"img/tooltip/supports-on.png", "img/tooltip/supports-off.png"},
            new String[]{"slicer_supports_tooltip_on", "slicer_supports_tooltip_off"}
    ),
    LAYER_HEIGHT(
            "slicer_layer_height", "slicer_layer_height_tooltip",
            new String[]{"img/tooltip/layer-height-0-1-mm.png", "img/tooltip/layer-height-0-2-mm.png"},
            new String[]{"slicer_layer_height_tooltip_fine", "slicer_layer_height_tooltip_coarse"}
    ),
    ROOF_THICKNESS(
            "slicer_roof_thickness", "slicer_roof_thickness_tooltip"
    ),
    BOTTOM_THICKNESS(
            "slicer_bottom_thickness", "slicer_bottom_thickness_tooltip"
    ),
    PRINT_SPEED_SOLID(
            "slicer_speed_solid", "slicer_speed_solid_tooltip"
    ),
    SHELL_THICKNESS(
            "slicer_shell_thickness", "slicer_shell_thickness_tooltip",
            new String[]{"img/tooltip/shell-thickness-0-4.png", "img/tooltip/shell-thickness-2-0.png","img/tooltip/shell-thickness-4-0.png"},
            new String[]{"slicer_shell_thickness_tooltip_thin", "slicer_shell_thickness_tooltip_medium","slicer_shell_thickness_tooltip_thick"}
    ),
    PRINT_SPEED_SHELL(
            "slicer_speed_shell", "slicer_speed_shell_tooltip"
    ),
    INFILL_PATTERN(
            "slicer_infill_pattern", "slicer_infill_pattern_tooltip",
            new String[]{"img/tooltip/infill-pattern-grid.png", "img/tooltip/infill-pattern-lines.png", "img/tooltip/infill-pattern-cubic.png"},
            new String[]{"slicer_infill_pattern_tooltip_grid", "slicer_infill_pattern_tooltip_lines", "slicer_infill_pattern_tooltip_cubic"}
    ),
    INFILL_DENSITY(
            "slicer_infill_density", "slicer_infill_density_tooltip",
            new String[]{"img/tooltip/infill-density-10.png", "img/tooltip/infill-density-75.png"},
            new String[]{"slicer_infill_density_tooltip_low", "slicer_infill_density_tooltip_high"}
    ),
    SUPPORT_DENSITY(
            "slicer_support_density", "slicer_support_density_tooltip",
            new String[]{"img/tooltip/support-density-5.png", "img/tooltip/support-density-25.png"},
            new String[]{"slicer_support_density_tooltip_low", "slicer_support_density_tooltip_high"}
    ),
    SUPPORT_PATTERN(
            "slicer_support_pattern", "slicer_support_pattern_tooltip",
            new String[]{"img/tooltip/support-pattern-lines.png", "img/tooltip/support-pattern-grid.png", "img/tooltip/support-pattern-zigzag.png"},
            new String[]{"slicer_support_pattern_tooltip_lines", "slicer_support_pattern_tooltip_grid", "slicer_support_pattern_tooltip_zigzag"}
    ),
    SUPPORT_ANGLE(
            "slicer_support_angle", "slicer_support_angle_tooltip",
            new String[]{"img/tooltip/support-angle-15.png", "img/tooltip/support-angle-55.png"},
            new String[]{"slicer_support_angle_tooltip_low", "slicer_support_angle_tooltip_high"}
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
