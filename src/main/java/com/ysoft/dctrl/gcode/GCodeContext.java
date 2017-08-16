    package com.ysoft.dctrl.gcode;

import com.ysoft.dctrl.editor.mesh.GCodeMoveType;

public class GCodeContext {

    public GCodeMoveType moveType = GCodeMoveType.NONE;
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
        if(z != null && (Double)this.z != null){
            if( this.z != z){
                this.z = z;
                return true;
            }
        }
        return false;
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
        if (isTravelMove != travelMove) {
            isTravelMove = travelMove;
            return true;
        } else {
            isTravelMove = travelMove;
            return false;
        }
    }

    public boolean setMoveType(GCodeMoveType moveType) {
        if (this.moveType != moveType) {
            this.moveType = moveType;
            return true;
        } else {
            this.moveType = moveType;
            return false;
        }
    }

    public GCodeMoveType getMoveType() {
        return moveType;
    }
}