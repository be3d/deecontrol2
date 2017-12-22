package com.ysoft.dctrl.editor.control;

import com.sun.javafx.geom.PickRay;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGParallelCamera;
import javafx.scene.Camera;
import javafx.scene.ParallelCamera;

/**
 * Created by kuhn on 12/22/2017.
 */
public class ExtendedParallelCamera extends ParallelCamera {

    @Override
    protected NGNode impl_createPeer() {
        final NGParallelCamera peer = new ExtendedNGParallelCamera();
        peer.setNearClip((float) getNearClip());
        peer.setFarClip((float) getFarClip());
        return peer;
    }
}
