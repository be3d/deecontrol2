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
}
