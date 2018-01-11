package com.ysoft.dctrl.editor.mesh.shape;

import javafx.collections.ObservableList;
import javafx.scene.paint.Material;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Transform;

public class Arrow {
    private static final int BOTTOM = 0;
    private static final int TOP = 1;

    private MeshView view;

    public Arrow(int divisions, double length, double radius, double headLength, double headRadius) {
        view = createMeshView(divisions, length, radius, headLength, headRadius);
    }

    public void setMaterial(Material material) {
        view.setMaterial(material);
    }

    public MeshView getView() {
        return view;
    }

    public ObservableList<Transform> getTransforms() {
        return view.getTransforms();
    }

    private MeshView createMeshView(int divisions, double length, double radius, double headLength, double headRadius) {
        float[] points = computePoints(divisions, (float) length, (float) radius, (float) headLength, (float) headRadius);
        int[] faces = computeFaces(divisions);

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(points);
        mesh.getFaces().addAll(faces);
        mesh.getTexCoords().addAll(0,0);

        return new MeshView(mesh);
    }

    private float[] computePoints(int divisions, float length, float radius, float headLength, float headRadius) {
        float[] points = new float[(2 + 3 * divisions) * 3];
        points[0] = points[1] = points[2] = 0;
        points[3] = points[4] = 0;
        points[5] = length + headLength;
        for(int i = 0; i < divisions; i++) {
            double alpha = i * 2 * Math.PI / (double) divisions;
            float x = (float) Math.cos(alpha);
            float y = (float) Math.sin(alpha);
            int offset = 6 + i*9;
            addPoint(offset, points, x, y, 0, radius);
            addPoint(offset + 3, points, x, y, length, radius);
            addPoint(offset + 6, points, x, y, length, headRadius);
        }
        return points;
    }

    private void addPoint(int offset, float[] points, float x, float y, float z, float radius) {
        points[offset] = x * radius;
        points[offset + 1] = y * radius;
        points[offset + 2] = z;
    }

    private int[] computeFaces(int divisions) {
        int[] faces = new int[divisions * 6 * 3 * 2];

        for(int i = 0; i < divisions; i++) {
            int edgeOffset = 2 + i * 3;
            int neighborEdgeOffset = 2 + ((i + 1) == divisions ? 0 : i + 1 ) * 3;
            int faceOffset = i * 6 * 6;
            addFace(faceOffset, faces, BOTTOM, neighborEdgeOffset, edgeOffset);
            addFace(faceOffset + 6, faces, neighborEdgeOffset, edgeOffset + 1, edgeOffset);
            addFace(faceOffset + 12, faces, neighborEdgeOffset, neighborEdgeOffset + 1, edgeOffset + 1);
            addFace(faceOffset + 18, faces, neighborEdgeOffset + 1, edgeOffset + 2, edgeOffset + 1);
            addFace(faceOffset + 24, faces, neighborEdgeOffset + 1, neighborEdgeOffset + 2, edgeOffset + 2);
            addFace(faceOffset + 30, faces, neighborEdgeOffset + 2, TOP, edgeOffset + 2);
        }

        return faces;
    }

    private void addFace(int offset, int[] faces, int a, int b, int c) {
        faces[offset] = a;
        faces[offset + 1] = 0;
        faces[offset + 2] = b;
        faces[offset + 3] = 0;
        faces[offset + 4] = c;
        faces[offset + 5] = 0;
    }
}
