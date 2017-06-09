package com.ysoft.dctrl.editor.mesh;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Material;
import javafx.scene.shape.MeshView;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by kuhn on 5/24/2017.
 */
public class GCodeLayer implements DrawableMesh {

    private int number = 0;
    private LinkedList<GCodeMove> moveBuffer = new LinkedList<>();

    private HashMap<String, LinkedList<GCodeMesh>> geometry = new HashMap<>();
    private MeshView view;

    public GCodeLayer(int number) {
        this.number = number;
        view = new MeshView();
    }

    public void processCmd(GCodeMoveType moveType, double x, double y, double z){
        if (moveBuffer.size() > 0){
            this.moveBuffer.add(new GCodeMove(moveBuffer.getLast().getFinish(), new Point3D(x,y,z), moveType));
        } else {
            this.moveBuffer.add(new GCodeMove(new Point3D(x,y,z), moveType));
        }
    }

    public LinkedList<GCodeMove> getMoveBuffer() {
        return moveBuffer;
    }

    public void clearMoveBuffer(){
        // The last move needs to be preserved, so we know its endpoint
        if (moveBuffer.size() > 0){
            GCodeMove lastMove = moveBuffer.getLast();
            lastMove.setStart(null);
            this.moveBuffer = new LinkedList<>();
            this.moveBuffer.add(lastMove);
        }
    }

    public void addMesh(GCodeMesh mesh){
        if (mesh != null){
            if (geometry.get(mesh.getType().name()) == null){
                geometry.put(mesh.getType().name(), new LinkedList<>());
            }
            geometry.get(mesh.getType().name()).add(mesh);
        }
    }

    public HashMap<String, LinkedList<GCodeMesh>> getGeometry() {
        return geometry;
    }

    @Override
    public Node getNode() {
        return view;
    }

    @Override
    public void setMaterial(Material material) {

    }
}
