package com.ysoft.dctrl.math;

import com.sun.javafx.geom.Vec3d;

import javafx.geometry.Point3D;

/**
 * Created by pilar on 24.3.2017.
 */
public class Point3DUtils {
    public static Point3D copy(Point3D p) {
        return new Point3D(p.getX(), p.getY(), p.getZ());
    }
}
