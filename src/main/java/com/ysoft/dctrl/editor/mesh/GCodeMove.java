package com.ysoft.dctrl.editor.mesh;

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
    private Point3D start;
    private final GCodeMoveType type;

    public GCodeMove(Point3D finish, GCodeMoveType moveType){
        this.start = null;
        this.finish = finish;
        this.type = moveType;
    }

    public GCodeMove(Point3D start, Point3D finish,  GCodeMoveType moveType){
        this.start = start;
        this.finish = finish;
        this.type = moveType;
    }

    public Point3D getPoint(){
        return finish;
    }

    public GCodeMoveType getType() {
        return type;
    }

    public Point3D getStart() {
        return start;
    }

    public void setStart(Point3D start) { this.start = start; }

    public Point3D getFinish() {
        return finish;
    }
}
