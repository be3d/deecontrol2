package com.ysoft.dctrl.editor.mesh;

import com.ysoft.dctrl.utils.ColorUtils;

import javafx.collections.ObservableFloatArray;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

import static com.ysoft.dctrl.editor.mesh.MeshUtils.addFaces;

public class PrinterVolumeOffset {
    private static final float HEIGHT = 0.2f;
    private static final float BOTTOM = -0.7f;
    private static final PhongMaterial SHADOW = new PhongMaterial();

    static {
        SHADOW.setSpecularColor(Color.BLACK);
        SHADOW.setDiffuseMap(ColorUtils.getColorImage("#b2b2b2", 0.2));
    }

    private MeshView node;
    private double offset;
    private Point2D size;
    private ObservableFloatArray nodePoints;

    public PrinterVolumeOffset() {
        node = createGeometry();
        size = new Point2D(0,0);
    }

    public Node getNode() {
        return node;
    }

    public void setSize(Point3D size) {
        this.size = new Point2D(size.getX(),size.getY());
        updateOuterSide();
    }

    public void setOffset(double offset) {
        this.offset = offset;
        node.setVisible(offset != 0);
        updateInnerSide();
    }

    public double getOffset() {
        return offset;
    }

    private MeshView createGeometry() {
        float[] points = new float[48];
        int[] faces = new int[192];

        int[] top = {9,1,8,0,11,3,10,2,9,1};
        int[] bottom = {12,4,13,5,14,6,15,7,12,4};
        int[] outer = {1,5,0,4,3,7,2,6,1,5};
        int[] inner = {8,12,9,13,10,14,11,15,8,12};

        addFaces(faces, top, 0);
        addFaces(faces, bottom, 4*12);
        addFaces(faces, outer, 8*12);
        addFaces(faces, inner, 12*12);

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(new float[18]);

        mesh.getFaces().addAll(faces);

        nodePoints = mesh.getPoints();

        MeshView view = new MeshView(mesh);
        view.setMaterial(SHADOW);
        return view;
    }

    private void updateInnerSide() {
        float hx = (float) (this.size.getX()/2 - offset);
        float hy = (float) (this.size.getY()/2 - offset);

        updateSide(hx, hy, 24);
    }

    private void updateOuterSide() {
        float hx = (float) this.size.getX()/2f;
        float hy = (float) this.size.getY()/2f;

        updateSide(hx, hy, 0);
        updateInnerSide();
    }

    private void updateSide(float hx, float hy, int pOffset) {
        float[] p = new float[] {
                -hx, -hy, HEIGHT,
                hx, -hy, HEIGHT,
                hx, hy, HEIGHT,
                -hx, hy, HEIGHT,
                -hx, -hy, BOTTOM,
                hx, -hy, BOTTOM,
                hx, hy, BOTTOM,
                -hx, hy, BOTTOM
        };

        for(int i = pOffset; i < p.length + pOffset; i++) {
            nodePoints.set(i, p[i - pOffset]);
        }
    }
}
