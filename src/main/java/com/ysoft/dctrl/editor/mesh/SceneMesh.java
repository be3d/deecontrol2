package com.ysoft.dctrl.editor.mesh;

import com.ysoft.dctrl.math.BoundingBox;

/**
 * Created by pilar on 6.4.2017.
 */
public interface SceneMesh extends Controllable, DrawableMesh {
    BoundingBox getBoundingBox();
}
