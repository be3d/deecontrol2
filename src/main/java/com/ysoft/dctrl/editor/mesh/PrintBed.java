package com.ysoft.dctrl.editor.mesh;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * Created by pilar on 25.7.2017.
 */
public class PrintBed {
    private Group group;
    private static final PhongMaterial GLASS = new PhongMaterial();
    private static final PhongMaterial GRID = new PhongMaterial();
    private static final PhongMaterial BORDER = new PhongMaterial();

    static {
        GLASS.setDiffuseMap(new Image("/img/glass.png"));
        GLASS.setSpecularColor(Color.BLACK);

        GRID.setSpecularColor(Color.BLACK);
        GRID.setDiffuseMap(new Image("/img/black.png"));
        GRID.setSelfIlluminationMap(new Image("/img/white.png"));

        BORDER.setSpecularColor(Color.BLACK);
        BORDER.setDiffuseMap(new Image("/img/black.png"));
        BORDER.setSelfIlluminationMap(new Image("/img/gray.png"));
    }

    public PrintBed(float x, float y) {
        group = new Group();
        group.getChildren().addAll(getBorderMesh(x, y, 2.5f), getGridMesh(x, y, 5), getGlassMesh(x, y, 5));
    }

    private MeshView getBorderMesh(float x, float y, float border) {
        float hx = x/2f;
        float hy = y/2f;
        float hb = border/2f;
        float hz = 0.25f;

        float[] points = new float[48];
        for(int i = 0; i < 16; i++) {
            points[i*3] = getSign(i, 3) * hx + ((hb + getSign(i, 0) * hb) * getSign(i, 3));
            points[i*3+1] = getSign(i, 2) * hy + ((hb + getSign(i, 0) * hb) * getSign(i, 2));
            points[i*3+2] = -hz + getSign(i, 1) * hz;
        }

        int[] top = {7,6,3,2,11,10,15,14,7,6};
        int[] bottom = {1,0,5,4,13,12,9,8,1,0};
        int[] side = {5,7,1,3,9,11,13,15,5,7};

        int[] faces = new int[144];
        addFaces(faces, top, 0);
        addFaces(faces, bottom, 4*12);
        addFaces(faces, side, 8*12);

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(0,0,0,1,1,1);
        mesh.getFaces().addAll(faces);

        MeshView view = new MeshView(mesh);
        view.setMaterial(BORDER);
        return view;
    }

    private void addFaces(int[] faces, int[] other, int offset) {
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

    private MeshView getGridMesh(float x, float y, float step) {
        float[] points = new float[((int) (Math.ceil(x/step)*Math.ceil(y/step)))*4*3*2];
        int[] faces = new int[points.length];
        int pOffset = 0;
        int fOffset = 0;
        int facesCount = 0;
        float hx = x/2f, hy = y/2f;
        for(float i = -hx-0.05f; i < hx+0.05f; i+=step) {
            pOffset = addGridPlane(points, i, -hy, 0, 0.1f, y, pOffset);
            pOffset = addGridPlane(points, i, -hy, -0.5f, 0.1f, y, pOffset);
            addFaces(faces, new int[] {facesCount+1,facesCount,facesCount+2,facesCount+3}, fOffset);
            facesCount += 4;
            fOffset += 2*6;
            addFaces(faces, new int[] {facesCount+3,facesCount,facesCount+2,facesCount+1}, fOffset);
            facesCount += 4;
            fOffset += 2*6;
        }

        for(float i = -hy-0.05f; i < hy+0.05f; i+=step) {
            pOffset = addGridPlane(points, -hx, i, 0, x, 0.1f, pOffset);
            pOffset = addGridPlane(points, -hx, i, -0.5f, x, 0.1f, pOffset);
            addFaces(faces, new int[] {facesCount+1,facesCount,facesCount+2,facesCount+3}, fOffset);
            facesCount += 4;
            fOffset += 2*6;
            addFaces(faces, new int[] {facesCount+3,facesCount,facesCount+2,facesCount+1}, fOffset);
            facesCount += 4;
            fOffset += 2*6;
        }

        TriangleMesh mesh = new TriangleMesh();
        mesh.getFaces().addAll(faces);
        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(0,0,0,1,1,1);

        MeshView view = new MeshView(mesh);
        view.setMaterial(GRID);
        return view;
    }

    private MeshView getGlassMesh(float x, float y, float step) {
        float[] points = new float[((int) (Math.ceil(x/step)*Math.ceil(y/step)))*4*3*2];
        int[] faces = new int[points.length];
        int pOffset = 0;
        int fOffset = 0;
        int facesCount = 0;
        float hx = x/2f, hy = y/2f;
        for(float i = -hx; i < hx; i+=step) {
            for(float j = -hy; j < hy; j+=step) {
                pOffset = addGlassPlane(points, i, j, 0, step, pOffset, 0.05f);
                pOffset = addGlassPlane(points, i, j, -0.5f, step, pOffset, 0.05f);
                addFaces(faces, new int[] {facesCount+1,facesCount,facesCount+2,facesCount+3}, fOffset);
                facesCount += 4;
                fOffset += 2*6;
                addFaces(faces, new int[] {facesCount+3,facesCount,facesCount+2,facesCount+1}, fOffset);
                facesCount += 4;
                fOffset += 2*6;
            }
        }

        TriangleMesh mesh = new TriangleMesh();
        mesh.getFaces().addAll(faces);
        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(0,0,0,1,1,1);

        MeshView view = new MeshView(mesh);
        view.setMaterial(GLASS);
        return view;
    }

    private int addGlassPlane(float[] points, float i, float j, float z, float step, int offset, float halfLineWidth) {
        points[offset++] = i + halfLineWidth;
        points[offset++] = j + halfLineWidth;
        points[offset++] = z;
        points[offset++] = i + step - halfLineWidth;
        points[offset++] = j + halfLineWidth;
        points[offset++] = z;
        points[offset++] = i + step - halfLineWidth;
        points[offset++] = j + step - halfLineWidth;
        points[offset++] = z;
        points[offset++] = i + halfLineWidth;
        points[offset++] = j + step - halfLineWidth;
        points[offset++] = z;
        return offset;
    }

    private int addGridPlane(float[] points, float i, float j, float z, float width, float height, int offset) {
        points[offset++] = i;
        points[offset++] = j;
        points[offset++] = z;
        points[offset++] = i + width;
        points[offset++] = j;
        points[offset++] = z;
        points[offset++] = i + width;
        points[offset++] = j + height;
        points[offset++] = z;
        points[offset++] = i;
        points[offset++] = j + height;
        points[offset++] = z;
        return offset;
    }

    private float getSign(int i, int bit) {
        return ((i & (1<<bit)) > 0) ? 1 : -1;
    }

    public Node getNode() {
        return group;
    }
}
