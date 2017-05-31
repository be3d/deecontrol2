package com.ysoft.dctrl.editor.mesh;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Material;
import javafx.scene.shape.MeshView;

import java.util.LinkedList;

/**
 * Created by kuhn on 5/24/2017.
 */
public class GCodeLayer implements DrawableMesh {

    private int number = 0;
    private LinkedList<GCodeMove> moves = new LinkedList<>();
    private MeshView view;

    public GCodeLayer(int number) {
        this.number = number;
        view = new MeshView();
    }

    public void processCmd(GCodeMoveType moveType, double x, double y, double z){
        this.moves.add(new GCodeMove(new Point3D(x,y,z), moveType));
    }

    public LinkedList<GCodeMove> getMoves() {
        return moves;
    }

    @Override
    public Node getNode() {
        return view;
    }

    @Override
    public void setMaterial(Material material) {

    }
}
