package com.ysoft.dctrl.editor.mesh;

import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * Created by kuhn on 5/24/2017.
 */
public class GCodeMesh extends TriangleMesh {
    private GCodeMoveType type;
    private MeshView view;

    public GCodeMesh(TriangleMesh mesh, GCodeMoveType type, Material material){
        view = new MeshView(mesh);
        //view.setVisible(false);
        view.setMaterial(material);

        this.type = type;
    }

    public GCodeMoveType getType() {
        return type;
    }

    public void setType(GCodeMoveType type) {
        this.type = type;
    }

    public MeshView getNode(){
        return view;
    }

    public void setVisible(boolean value){
        this.view.setVisible(value);
    }
}
