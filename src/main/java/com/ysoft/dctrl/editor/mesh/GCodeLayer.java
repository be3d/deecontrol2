package com.ysoft.dctrl.editor.mesh;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Material;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import java.util.*;

/**
 * Created by kuhn on 5/24/2017.
 */
public class GCodeLayer {

    private int number;
    private LinkedList<GCodeMove> moveBuffer;
    private LinkedHashMap<String,MeshView> meshViewsMap;
    private LinkedHashMap<String, LinkedList<GCodeMeshData>> meshDataMap;

    private static final GCodeMeshProperties gCodeMeshProperties;
    private static final GCodeMeshGenerator gCodeMeshGenerator;

    static {
        gCodeMeshProperties = new GCodeMeshProperties();
        gCodeMeshGenerator = new GCodeMeshGenerator(gCodeMeshProperties);
    }

    public GCodeLayer(int number) {
        this.number = number;
        moveBuffer = new LinkedList<>();
        meshViewsMap = new LinkedHashMap<>();
        meshDataMap = new LinkedHashMap<>();
    }

    public int getNumber() {
        return number;
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

    public void addMeshData(GCodeMeshData mesh){
        //if (mesh!= null && ( mesh.getType() == GCodeMoveType.SUPPORT || mesh.getType() == GCodeMoveType.WALL_OUTER)){
        if (mesh != null){
            if (meshDataMap.get(mesh.getType().name()) == null){
                meshDataMap.put(mesh.getType().name(), new LinkedList<>());
            }
            meshDataMap.get(mesh.getType().name()).add(mesh);
        }
    }

    public HashMap<String, LinkedList<GCodeMeshData>> getGeometryData() {
        return meshDataMap;
    }


    public void generateMeshViews(){
        for (LinkedList<GCodeMeshData> meshList: meshDataMap.values()){
            MeshView view = new MeshView(constructMeshFromSegments(meshList));
            view.setMaterial(gCodeMeshProperties.getMaterial(meshList.getFirst().getType()));
            view.getTransforms().addAll(new Rotate(180, new Point3D(0,0,1)), new Translate(-75,-75));
            meshViewsMap.put(meshList.getFirst().getType().name() , view);
        }
    }

    public void generateMeshViews(GCodeMoveType... types){
        for (GCodeMoveType t : types){
            LinkedList<GCodeMeshData> meshList = meshDataMap.get(t.name());
            MeshView view = new MeshView(constructMeshFromSegments(meshList));
            view.setMaterial(gCodeMeshProperties.getMaterial(meshList.getFirst().getType()));
            meshViewsMap.put(meshList.getFirst().getType().name() , view);
        }
    }

    private TriangleMesh constructMeshFromSegments(LinkedList<GCodeMeshData> meshList){
        TriangleMesh mesh = new TriangleMesh();
        for (GCodeMeshData meshData : meshList) {

            // Face indexes must be recalculated by offset of already added geometry
            int offset = mesh.getPoints().size()/3;
            int[] faces = new int[meshData.getFaces().length];
            for (int i = 0; i < faces.length; i++){
                if (i % 2 == 0){
                    // Odd values must be left 0, because those are texture coords.
                    faces[i] = meshData.getFaces()[i] + offset;
                }
            }
            mesh.getPoints().addAll(meshData.getVertices());
            mesh.getFaces().addAll(faces);
            mesh.getTexCoords().addAll(0, 0);
        }
        return mesh;
    }

    public LinkedHashMap<String, MeshView> getMeshViews() {
        return meshViewsMap;
    }

    public void clearMeshView(GCodeMoveType type){
        meshViewsMap.put(type.name(), null);
    }

    public void clearMeshViews(){
        meshViewsMap.clear();
    }

    public void setVisible(boolean value){
        for (MeshView view : meshViewsMap.values()){
            if(view != null) view.setVisible(value);
        }
    }

    public void finalizeLayer(){
        //generateMeshViews();
    }

    public void finalizeSegment() {
        addMeshData(gCodeMeshGenerator.run(getMoveBuffer()));
        clearMoveBuffer();
    }
}
