package com.ysoft.dctrl.editor.mesh;

import com.ysoft.dctrl.math.LineSegment;
import javafx.geometry.Point3D;

/**
 * Created by kuhn on 5/25/2017.
 *
 * Relative extruder move
 *
 */
public class GCodeMove {

    private final int extruder = 0;
    private final Point3D finish;
    private final GCodeMoveType type;

    public GCodeMove(Point3D finish, GCodeMoveType moveType){
        this.finish = finish;
        this.type = moveType;
    }

    public Point3D getPoint(){
        return finish;
    }

    public GCodeMoveType getType() {
        return type;
    }
}
