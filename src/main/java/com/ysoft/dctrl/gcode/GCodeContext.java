package com.ysoft.dctrl.gcode;

import com.ysoft.dctrl.editor.mesh.GCodeMoveType;

public class GCodeContext {

    private GCodeMoveType moveType = GCodeMoveType.NONE;
    private boolean isTravelMove = false;
    private double x, y, z = 0;
    private int layer;

    public void setX(Double x) {
        if (x != null) this.x = x;
    }

    public void setY(Double y) {
        if (y != null) this.y = y;
    }

    public boolean setZ(Double z) {
        boolean res = false;
        if(z != null){
            res = this.z != z;
            this.z = z;
        }
        return res;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public int getLayer() {
        return layer;
    }

    public boolean isTravelMove() {
        return isTravelMove;
    }

    /**
     * This setter also indicates if the move type is about to change or not.
     * If yes, the geometry needs to be generated.
     *
     * @param travelMove
     * @return
     */
    public boolean setTravelMove(boolean travelMove) {
        boolean res = isTravelMove != travelMove;
        isTravelMove = travelMove;
        return res;
    }

    public boolean setMoveType(GCodeMoveType moveType) {
        boolean res = this.moveType != moveType;
        this.moveType = moveType;
        return res;
    }

    public GCodeMoveType getMoveType() {
        return moveType;
    }
}