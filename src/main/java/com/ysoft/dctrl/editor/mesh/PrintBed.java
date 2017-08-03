package com.ysoft.dctrl.editor.mesh;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 25.7.2017.
 */
public class PrintBed {
    private MeshView meshView;

    public PrintBed(double x, double y, String texture) {
        Image tex = new Image(getClass().getResourceAsStream(texture));
        TriangleMesh mesh = createMesh((float) x + 10f, (float) y + 10f, 0.8f);
        meshView = new MeshView(mesh);
        meshView.setMaterial(createMaterial(tex));
    }

    private TriangleMesh createMesh(float x, float y, float yTex) {
        float hx = x/2f;
        float hy = y/2f;

        float[] points = {
                -hx, -hy, -0.5f,
                 hx, -hy, -0.5f,
                 hx,  hy, -0.5f,
                -hx,  hy, -0.5f,
                -hx, -hy, 0,
                 hx, -hy, 0,
                 hx,  hy, 0,
                -hx,  hy, 0,
        };

        float[] tex = {
                0, 0,
                0, yTex,
                1, yTex,
                1, 0,
                0, 1,
                1, 1
        };

        int[] faces = {
                7, 0, 4, 1, 5, 2,
                7, 0, 5, 2, 6, 3,
                4, 1, 0, 4, 1, 5,
                4, 1, 1, 5, 5, 2,
                5, 1, 1, 4, 2, 5,
                5, 1, 2, 5, 6, 2,
                6, 1, 2, 4, 3, 5,
                6, 1, 3, 5, 7, 2,
                7, 1, 3, 4, 0, 5,
                7, 1, 0, 5, 4, 2,
                0, 1, 3, 0, 2, 3,
                0, 1, 2, 3, 1, 2
        };

        TriangleMesh mesh = new TriangleMesh();

        mesh.getPoints().addAll(points);
        mesh.getTexCoords().addAll(tex);
        mesh.getFaces().addAll(faces);

        return mesh;
    }

    private PhongMaterial createMaterial(Image texture) {
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(texture);
        //mat.setSpecularMap(new Image(getClass().getResourceAsStream("/img/spec.png")));
        mat.setSpecularPower(0);
        return mat;
    }

    public MeshView getView() {
        return meshView;
    }
}
