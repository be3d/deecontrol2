package com.ysoft.dctrl.utils;

import com.ysoft.dctrl.editor.mesh.GCodeMoveType;

public class GCodeContext {

    public GCodeMoveType moveType = GCodeMoveType.NONE;
    private boolean isTravelMove = false;

    private double x, y, z;
    private int layer;

    public void setX(Double x) {
        if (x != null) this.x = x;
    }

    public void setY(Double y) {
        if (y != null) this.y = y;
    }

    public void setZ(Double z) {
        if (z != null) this.z = z;
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