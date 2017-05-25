package com.ysoft.dctrl.editor.mesh;

import javafx.geometry.Point3D;

import java.util.LinkedList;

/**
 * Created by kuhn on 5/24/2017.
 */
public class GCodeLayer {

    private int number = 0;
    private LinkedList<GCodeMove> moves = new LinkedList<>();

    public GCodeLayer(int number) {
        this.number = number;
    }

    public void processCmd(GCodeMoveType moveType, double x, double y, double z){
        this.moves.add(new GCodeMove(new Point3D(x,y,z), moveType));
    }

    public LinkedList<GCodeMove> getMoves() {
        return moves;
    }
}
