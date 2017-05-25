package com.ysoft.dctrl.editor.mesh;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Material;
import javafx.scene.shape.MeshView;

/**
 * Created by kuhn on 5/24/2017.
 */
public class GCodeMesh implements DrawableMesh {

    Point3D[] path;
    private MeshView view;

    public GCodeMesh(){
        view = new MeshView();
    }

    public GCodeMesh(Point3D[] points) {
        path = points;
    }

    @Override
    public Node getNode() {
        return view;
    }

    @Override
    public void setMaterial(Material material) {

    }

    // build Triangle mesh from section of points

    // set material <- presenter

}
