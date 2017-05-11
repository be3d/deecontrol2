package com.ysoft.dctrl.editor.mesh;

/**
 * Created by pilar on 11.4.2017.
 */
@FunctionalInterface
public interface OnMeshChange {
    public void accept(ExtendedMesh mesh);
}
