package com.ysoft.dctrl.editor.exporter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.ysoft.dctrl.editor.mesh.ExtendedMesh;
import com.ysoft.dctrl.math.TransformMatrix;

import javafx.geometry.Point3D;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 11.4.2017.
 */
public class MeshConverter {
    private final TriangleMesh mesh;
    private final TransformMatrix transformMatrix;

    public MeshConverter(ExtendedMesh extendedMesh) {
        this.mesh = (TriangleMesh) ((MeshView) extendedMesh.getNode()).getMesh();
        this.transformMatrix = extendedMesh.getTransformMatrix();
    }

    public byte[] convertToStl() {
        float[] points = mesh.getPoints().toArray(null);
        float[] normals = mesh.getNormals().toArray(null);
        int[] faces = mesh.getFaces().toArray(null);

        int facesNumber = faces.length;
        int size = facesNumber*50/9 + 84;
        ByteBuffer bb = ByteBuffer.allocate(size);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        writeHead(bb, facesNumber/9);

        for(int i = 0; i < facesNumber; i += 9) {
            writeFace(bb, i, faces, points, normals);
        }

        return bb.array();
    }

    private void writeHead(ByteBuffer buffer, int facesNumber) {
        buffer.put(new byte[80]);
        buffer.putInt(facesNumber);
    }

    private void writeFace(ByteBuffer buffer, int faceOffset, int[] faces, float[] points, float[] normals) {
        buffer.putFloat(0.0f);
        buffer.putFloat(0.0f);
        buffer.putFloat(0.0f);

        Point3D a = getVertex(faces[faceOffset    ]*3, points);
        Point3D b = getVertex(faces[faceOffset + 3]*3, points);
        Point3D c = getVertex(faces[faceOffset + 6]*3, points);

        buffer.putFloat((float) a.getX());
        buffer.putFloat((float) a.getY());
        buffer.putFloat((float) a.getZ());
        buffer.putFloat((float) b.getX());
        buffer.putFloat((float) b.getY());
        buffer.putFloat((float) b.getZ());
        buffer.putFloat((float) c.getX());
        buffer.putFloat((float) c.getY());
        buffer.putFloat((float) c.getZ());
        buffer.putChar((char) 0);
    }

    private Point3D getVertex(int pointOffset, float[] points) {
        Point3D v = new Point3D(points[pointOffset], points[pointOffset + 1], points[pointOffset + 2]);
        return transformMatrix.applyTo(v);
    }
}
