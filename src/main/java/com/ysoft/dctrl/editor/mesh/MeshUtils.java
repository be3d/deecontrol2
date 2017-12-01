package com.ysoft.dctrl.editor.mesh;

import com.ysoft.dctrl.math.BoundingBox;

import javafx.collections.ObservableFloatArray;
import javafx.geometry.Point3D;
import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 6.4.2017.
 */
public class MeshUtils {
    public static void translateVertexesAndUpdateBoundingBox(TriangleMesh mesh, BoundingBox boundingBox) {
        ObservableFloatArray points = mesh.getPoints();
        Point3D halfSize = boundingBox.getHalfSize();
        Point3D diff = halfSize.add(boundingBox.getMin());
        int len = points.size();
        for(int i = 0; i < len; i += 3) {
            points.set(i    ,(float) (points.get(i    ) - diff.getX()));
            points.set(i + 1,(float) (points.get(i + 1) - diff.getY()));
            points.set(i + 2,(float) (points.get(i + 2) - diff.getZ()));
        }

        boundingBox.set(halfSize.multiply(-1), halfSize);
    }

    public static void addFaces(int[] faces, int[] other, int offset) {
        for(int i = 1, j = offset; i < other.length - 2; i+=2, j+=12) {
            faces[j] = other[i];
            faces[j+2] = other[i-1];
            faces[j+4] = other[i+1];
            faces[j+6] = other[i];
            faces[j+8] = other[i+1];
            faces[j+10] = other[i+2];
            faces[j+1] = 0;
            faces[j+3] = 1;
            faces[j+5] = 2;
            faces[j+7] = 0;
            faces[j+9] = 1;
            faces[j+11] = 2;
        }
    }

    public static float getSign(int i, int bit) {
        return ((i & (1<<bit)) > 0) ? 1 : -1;
    }
}
