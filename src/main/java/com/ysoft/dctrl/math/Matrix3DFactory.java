package com.ysoft.dctrl.math;

/**
 * Created by pilar on 3.4.2017.
 */
public class Matrix3DFactory {
    public static Matrix3D getXRotationMatrix(double angle) {
        return new Matrix3D(new double[] {
                1, 0              , 0,
                0, Math.cos(angle), -Math.sin(angle),
                0, Math.sin(angle), Math.cos(angle)
        });
    }

    public static Matrix3D getYRotationMatrix(double angle) {
        return new Matrix3D(new double[] {
                Math.cos(angle) , 0, Math.sin(angle),
                0               , 1, 0,
                -Math.sin(angle), 0, Math.cos(angle)
        });
    }

    public static Matrix3D getZRotationMatrix(double angle) {
        return new Matrix3D(new double[] {
                Math.cos(angle), -Math.sin(angle), 0,
                Math.sin(angle), Math.cos(angle) , 0,
                0              , 0               , 1
        });
    }
}
