package com.ysoft.dctrl.editor.control;

import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.sg.prism.NGParallelCamera;

/**
 * Created by kuhn on 12/22/2017.
 */
public class ExtendedNGParallelCamera extends NGParallelCamera {
    double viewWidth, viewHeight;
    Vec3d position;
    GeneralTransform3D projViewTx;
    Affine3D localToWorldTx;

    @Override
    public void setViewWidth(double viewWidth) {
        this.viewWidth = viewWidth;
    }

    public double getViewWidth() {
        return viewWidth;
    }

    @Override
    public void setViewHeight(double viewHeight) {
        this.viewHeight = viewHeight;
    }

    public double getViewHeight() {
        return viewHeight;
    }

    @Override
    public void setPosition(Vec3d position) {
        this.position = position;
    }

    public Vec3d getPosition() {
        return position;
    }

    @Override
    public void setWorldTransform(Affine3D localToWorldTx) {
        this.localToWorldTx = localToWorldTx;
    }

    public Affine3D getWorldTransform() {
        return localToWorldTx;
    }

    @Override
    public void setProjViewTransform(GeneralTransform3D projViewTx) {
        this.projViewTx = projViewTx;
    }

    public GeneralTransform3D getProjViewTx() {
        return projViewTx;
    }
}
