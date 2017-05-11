package com.ysoft.dctrl.math;

import java.util.Arrays;

import javafx.geometry.Point3D;

/**
 * Created by pilar on 3.4.2017.
 */
public class Matrix3D {
    private double[] elements = new double[9];

    public Matrix3D() {
        elements[0] = 1;
        elements[1] = 0;
        elements[2] = 0;
        elements[3] = 0;
        elements[4] = 1;
        elements[5] = 0;
        elements[6] = 0;
        elements[7] = 0;
        elements[8] = 1;
    }

    public Matrix3D(Matrix3D m) {
        double[] e = m.elements;
        for(int i = 0; i < elements.length; i++) { elements[i] = e[i]; }
    }

    public Matrix3D(double[] elements) {
        for(int i = 0; i < this.elements.length; i++) { this.elements[i] = elements[i]; }
    }

    public Matrix3D multiply(Matrix3D m) {
        double[] e1 = elements;
        double[] e2 = m.elements;
        Matrix3D res = new Matrix3D();
        for(int i = 0; i < 9; i += 3) {
            for(int j = 0; j < 3; j++) {
                res.elements[i + j] = e1[i]*e2[j] + e1[i + 1]*e2[j + 3] + e1[i + 2]*e2[j + 6];
            }
        }

        return res;
    }

    public Point3D toEuler() {
        double[] e = elements;
        double x = Math.atan2(e[7], e[8]);
        double y = Math.atan2(-e[6], Math.sqrt(e[7]*e[7] + e[8]*e[8]));
        double z = Math.atan2(e[3], e[0]);
        return new Point3D(x, y, z);
    }

    public double[] getElements() {
        return Arrays.copyOf(elements, elements.length);
    }
}
