package com.ysoft.dctrl.editor.mesh;


/**
 * Created by kuhn on 5/25/2017.
 */
public enum GCodeMoveType {

    NONE(""),
    WALL_INNER("WALL-INNER"),
    WALL_OUTER("WALL-OUTER"),
    FILL("FILL"),
    SKIN("SKIN"),
    TRAVEL("TRAVEL_MOVE"),
    SUPPORT("SUPPORT"),
    SKIRT("SKIRT");

    private final String text;

    GCodeMoveType(final String text){
        this.text = text;
    }

    @Override
    public String toString(){
        return text;
    }

    public static GCodeMoveType getValueOf(String s){
        for (GCodeMoveType moveType : values()){
            if (s.equals(moveType.toString()))
                return moveType;
        }
        return null;
    }
}
