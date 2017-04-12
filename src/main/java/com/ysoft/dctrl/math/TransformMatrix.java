package com.ysoft.dctrl.math;

import java.util.Arrays;

import javafx.geometry.Point3D;

/**
 * Created by pilar on 10.4.2017.
 */
public class TransformMatrix {
    private double[] elements;

    public TransformMatrix() {
        elements = new double[] {
                1,0,0,0,
                0,1,0,0,
                0,0,1,0,
                0,0,0,1
        };
    }

    private TransformMatrix multiply(TransformMatrix m) {
        double[] e1 = Arrays.copyOf(elements, elements.length);
        double[] e2 = m.elements;
        for(int i = 0; i < 16; i += 4) {
            for(int j = 0; j < 4; j++) {
                elements[i + j] = e1[i]*e2[j] + e1[i + 1]*e2[j + 4] + e1[i + 2]*e2[j + 8] + e1[i + 3]*e2[j + 12];
            }
        }

        return this;
    }

    public TransformMatrix applyEuler(Point3D euler) {
        TransformMatrix rm = new TransformMatrix();
        double x = euler.getX();
        double y = euler.getY();
        double z = euler.getZ();

        double cx = Math.cos(x); double sx = Math.sin(x);
        double cy = Math.cos(y); double sy = Math.sin(y);
        double cz = Math.cos(z); double sz = Math.sin(z);

        double cxcz = cx * cz;
        double cxsz = cx * sz;
        double sxcz = sx * cz;
        double sxsz = sx * sz;

        rm.elements[0] = cy * cz;
        rm.elements[1] = - cy * sz;
        rm.elements[2] = sy;

        rm.elements[4] = cxsz + sxcz * sy;
        rm.elements[5] = cxcz - sxsz * sy;
        rm.elements[6] = - sx * cy;

        rm.elements[8] = sxsz - cxcz * sy;
        rm.elements[9] = sxcz + cxsz * sy;
        rm.elements[10] = cx * cy;

        return multiply(rm);
    }

    public TransformMatrix applyScale(Point3D scale) {
        TransformMatrix sm = new TransformMatrix();

        sm.elements[0] = scale.getX();
        sm.elements[5] = scale.getY();
        sm.elements[10] = scale.getZ();

        return multiply(sm);
    }

    public TransformMatrix applyTranslate(Point3D translate) {
        TransformMatrix tm = new TransformMatrix();

        tm.elements[3] = translate.getX();
        tm.elements[7] = translate.getY();
        tm.elements[11] = translate.getZ();

        return multiply(tm);
    }

    public Point3D applyTo(Point3D point) {
        return applyTo(point.getX(), point.getY(), point.getZ());
    }

    public Point3D applyTo(double x, double y, double z) {
        double[] e = this.elements;

        double resX = e[0] * x + e[1] * y + e[2] * z + e[3];
        double resY = e[4] * x + e[5] * y + e[6] * z + e[7];
        double resZ = e[8] * x + e[9] * y + e[10] * z + e[11];

        return new Point3D(resX,resY,resZ);
    }
}
