package com.ysoft.dctrl.gcode;

import com.ysoft.dctrl.editor.mesh.GCodeMoveType;

public class GCodeContext {

    private GCodeMoveType moveType;
    private boolean isTravelMove;
    private double x, y, z;
    private int layerIndex;

    public GCodeContext() {
        x = 0;
        y = 0;
        z = 0;

        moveType = GCodeMoveType.NONE;
        isTravelMove = false;
        layerIndex = -1;
    }

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

    public void setLayerIndex(int index) {
        this.layerIndex = index;
    }

    public int getLayerIndex() {
        return layerIndex;
    }

    public int setNextLayerIndex(){ return ++layerIndex; }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
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