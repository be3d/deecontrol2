package com.ysoft.dctrl.math;

import javafx.geometry.Point3D;

/**
 * Created by pilar on 24.3.2017.
 */
public class AxisAngleRotation {
    private Point3D axis;
    private double angle;

    public AxisAngleRotation(Point3D a, Point3D b) {
        axis = a.crossProduct(b).normalize();
        angle = Math.acos((a.dotProduct(b))/(a.magnitude()*b.magnitude()));
    }

    public Point3D rotate(Point3D v) {
        /*double size = v.magnitude();
        Point3D norm = v.normalize();

        double c = Math.cos(angle);
        double s = Math.sin(angle);
        double t = 1 - Math.cos(angle);*/

        Point3D a = Point3DUtils.copy(v);
        a = a.multiply(Math.cos(angle));

        Point3D b = v.crossProduct(axis);
        b = b.multiply(Math.sin(angle));

        Point3D e = Point3DUtils.copy(axis);
        e = e.multiply((1 - Math.cos(angle))*(e.dotProduct(v)));

        return a.add(b).add(e);
    }

    public Point3D getAxis() {
        return axis;
    }

    public double getAngle() {
        return angle;
    }

    public String toString() {
        return "AxisAngleRotation[axis=" + axis.toString() + " angle=" + angle + "]";
    }
}
