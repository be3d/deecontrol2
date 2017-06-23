package com.ysoft.dctrl.editor.mesh;


/**
 * Created by kuhn on 6/16/2017.
 */
public class GCodeMeshData {

    float[] vertices;
    int[] faces;
    GCodeMoveType type;

    public GCodeMeshData(float[] vertices, int[] faces, GCodeMoveType type){
        this.vertices = vertices;
        this.faces = faces;
        this.type = type;
    }

    public GCodeMoveType getType() {
        return type;
    }

    public float[] getVertices() {
        return vertices;
    }

    public int[] getFaces() {
        return faces;
    }
}
