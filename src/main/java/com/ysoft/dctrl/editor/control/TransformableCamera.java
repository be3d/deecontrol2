package com.ysoft.dctrl.editor.control;

import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.transform.TransformChangedEvent;

/**
 * Created by kuhn on 1/11/2018.
 */
public interface TransformableCamera {

    void setPosition(Point3D p);
    void setPositionX(double x);
    void setPositionY(double y);
    void setPositionZ(double z);

    void setRotation(double x, double y, double z);
    void setRotationX(double x);
    void setRotationY(double y);
    void setRotationZ(double z);

    void setRotationChangeListener(EventHandler<TransformChangedEvent> handler);

    Point3D getPosition();
    Point3D getRotation();

}
