package com.ysoft.dctrl.editor.mesh;

import javafx.geometry.Point3D;

/**
 * Created by pilar on 6.4.2017.
 */
public interface Controllable {
    void scale(double scale);
    void scale(Point3D scale);
    void scaleX(double scale);
    void scaleY(double scale);
    void scaleZ(double scale);

    void setScale(double scale);
    void setScale(Point3D scale);
    void setScaleX(double scale);
    void setScaleY(double scale);
    void setScaleZ(double scale);

    Point3D getScale();
    double getScaleX();
    double getScaleY();
    double getScaleZ();

    void rotate(Point3D rotation);
    void rotateX(double angle);
    void rotateY(double angle);
    void rotateZ(double angle);

    void setRotation(Point3D rotation);
    void setRotationX(double angle);
    void setRotationY(double angle);
    void setRotationZ(double angle);

    Point3D getRotation();
    double getRotationX();
    double getRotationY();
    double getRotationZ();

    void move(Point3D diff);
    void moveX(double diff);
    void moveY(double diff);
    void moveZ(double diff);

    void setPosition(Point3D position);
    void setPositionX(double position);
    void setPositionY(double position);
    void setPositionZ(double position);

    Point3D getPosition();
    double getPositionX();
    double getPositionY();
    double getPositionZ();


}
