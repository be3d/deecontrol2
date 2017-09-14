package com.ysoft.dctrl.event;

/**
 * Created by pilar on 30.3.2017.
 */
public enum EventType {
    UNDO_EMPTY,
    UNDO_NOT_EMPTY,
    REDO_EMPTY,
    REDO_NOT_EMPTY,
    ADD_ACTION,

    OPEN_MODEL,
    CHANGE_LANGUAGE,
    ADD_MODEL,
    PRINTER_CHANGED,
    EXPORT_SCENE,
    SCENE_EXPORT_PROGRESS,
    SCENE_EXPORTED,
    SCENE_SET_MODE,

    EDIT_CLEAR_SELECTION,
    EDIT_SELECT_NEXT,
    EDIT_SELECT_PREV,
    EDIT_DELETE_SELECTED,
    EDIT_GROUP,
    EDIT_UNGROUP,
    EDIT_SCENE_VALID,
    EDIT_SCENE_INVALID,
    EDIT_SCENE_MODEL_STACK_CHANGED,

    SLICER_PARAM_CHANGED,
    SLICER_PANEL_SCROLLED,
    SLICER_STOP,
    SLICER_FINISHED,
    SLICER_PROGRESS,
    GCODE_VIEWER_OPEN,
    GCODE_VIEWER_CLOSE,
    GCODE_IMPORT_COMPLETED,
    GCODE_DRAFT_RENDER_FINISHED,
    GCODE_RENDER_FINISHED,
    GCODE_EXPORT,

    GCODE_HEAD_WRITE_PROGRESS,
    GCODE_HRAD_WRITTEN,

    CONTROL_MOVE_MODEL_CLICK,
    CONTROL_SCALE_MODEL_CLICK,
    CONTROL_ROTATE_MODEL_CLICK,
    MODEL_MULTISELECTION,
    MODEL_SELECTED,
    MODEL_LOAD_PROGRESS,
    MODEL_LOADED,
    RESET_VIEW,
    TOP_VIEW,
    CENTER_SELECTED_MODEL,
    ALIGN_LEFT_SELECTED_MODEL,
    ALIGN_RIGHT_SELECTED_MODEL,
    ALIGN_FRONT_SELECTED_MODEL,
    ALIGN_BACK_SELECTED_MODEL,
    SCALE_MAX_SELECTED_MODEL,
    SHOW_DIALOG,
    TAKE_SCENE_SNAPSHOT,
    JOB_FILE_PROGRESS,
    JOB_FILE_DONE,
    JOB_SEND_DONE,
    JOB_EXPORT,
    SHOW_NOTIFICATION,
    SHOW_TOOLTIP
}
