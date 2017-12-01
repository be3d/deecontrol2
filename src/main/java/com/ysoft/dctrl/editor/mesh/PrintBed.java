package com.ysoft.dctrl.editor.mesh;

import com.ysoft.dctrl.utils.ColorUtils;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

import static com.ysoft.dctrl.editor.mesh.MeshUtils.addFaces;
import static com.ysoft.dctrl.editor.mesh.MeshUtils.getSign;

/**
 * Created by pilar on 25.7.2017.
 */
public class PrintBed {
    private Group group;
    private static final PhongMaterial GLASS = new PhongMaterial();
    private static final PhongMaterial GRID = new PhongMaterial();
    private static final PhongMaterial BORDER = new PhongMaterial();

    static {
        GLASS.setDiffuseMap(ColorUtils.getColorImage("#000000", 0.1));
        GLASS.setSpecularColor(Color.BLACK);

        GRID.setSpecularColor(Color.BLACK);
        GRID.setDiffuseMap(ColorUtils.getColorImage("#000000"));
        GRID.setSelfIlluminationMap(ColorUtils.getColorImage("#ffffff"));

        BORDER.setSpecularColor(Color.BLACK);
        BORDER.setDiffuseMap(ColorUtils.getColorImage("#000000"));
        BORDER.setSelfIlluminationMap(ColorUtils.getTexture("edee_label"));
    }

    public PrintBed(float x, float y) {
        group = new Group();
        group.setMouseTransparent(true);
        group.getChildren().addAll(getBorderMesh(x, y, 2.5f), getGridMesh(x, y, 5), getGlassMesh(x, y, 5));
    }

    private MeshView getBorderMesh(float x, float y, float border) {
        float hx = x/2f;
        float hy = y/2f;
        float hb = border/2f;
        float hz = 0.25f;

        float[] points = new float[48];
        for(int i = 0; i < 16; i++) {
            points[i*3] = getSign(i, 3) * (hx + (hb * (1 + getSign(i,0))));
            points[i*3+1] = getSign(i, 2) * (hy + (hb * (1 + getSign(i,0)) * (2.5f + (-1.5f * getSign(i, 2)))));
            points[i*3+2] = -hz + getSign(i, 1) * hz;
        }

        int[] top = {7,6,3,2,11,10,15,14,7,6};
        int[] bottom = {1,0,5,4,13,12,9,8,1,0};
        int[] side = {5,7,1,3,9,11,13,15,5,7};

        int[] faces = new int[144];
        addFaces(faces, top, 0);
        addFaces(faces, bottom, 4*12);
        addFaces(faces, side, 8*12);

        for(int i = 0; i < faces.length; i +=6) {
            if(faces[i] != 2) { continue; }
            if(faces[i+2] == 3 && faces[i+4] == 11) {
                faces[i+1] = 3;
                faces[i+2] = 16;
                faces[i+3] = 4;
                faces[i+4] = 17;
                faces[i+5] = 5;
            } else if(faces[i+2] == 11 && faces[i+4] == 10) {
                faces[i+1] = 6;
                faces[i+2] = 17;
                faces[i+3] = 7;
                faces[i+5] = 8;
            }
        }

        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(points);
        mesh.getPoints().addAll(-hx, -hy - (hb*8), 0, hx, -hy - (hb*8), 0);
        mesh.getTexCoords().addAll(
                0.5f,0,0.5f,1,1,1,
                0   ,0,0   ,1,1,1,
                0   ,0,1   ,1,1,0
                );
        mesh.getFaces().addAll(faces);
        mesh.getFaces().addAll(2,0,3,1,16,2,10,0,17,1,11,2);

        MeshView view = new MeshView(mesh);
        view.setMaterial(BORDER);
        return view;
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
            fOffset += 12;
            addFaces(faces, new int[] {facesCount+3,facesCount,facesCount+2,facesCount+1}, fOffset);
            facesCount += 4;
            fOffset += 12;
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



    public Node getNode() {
        return group;
    }
}
