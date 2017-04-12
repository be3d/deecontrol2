package com.ysoft.dctrl.editor.mesh;

import javafx.scene.Node;
import javafx.scene.paint.Material;

/**
 * Created by pilar on 6.4.2017.
 */
public interface DrawableMesh {
    Node getNode();
    void setMaterial(Material material);
}
