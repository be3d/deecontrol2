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

    public static Point3D setX(Point3D p, double x) {
        return new Point3D(x, p.getY(), p.getZ());
    }

    public static Point3D setY(Point3D p, double y) {
        return new Point3D(p.getX(), y, p.getZ());
    }

    public static Point3D setZ(Point3D p, double z) {
        return new Point3D(p.getX(), p.getY(), z);
    }

    public static Point3D applyMatrix(Point3D p, Matrix3D m) {
        double[] e = m.getElements();
        double[] res = new double[3];
        for(int i = 0; i < 9; i += 3) {
            res[i/3] = e[i]*p.getX() + e[i + 1]*p.getY() + e[i + 2]*p.getZ();
        }
        return new Point3D(res[0], res[1], res[2]);
    }
}
