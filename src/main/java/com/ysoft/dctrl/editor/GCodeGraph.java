package com.ysoft.dctrl.editor;

import com.ysoft.dctrl.editor.mesh.GCodeLayer;
import com.ysoft.dctrl.editor.mesh.GCodeMesh;
import com.ysoft.dctrl.editor.mesh.GCodeMoveType;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by kuhn on 6/8/2017.
 */

public class GCodeGraph {
    private LinkedHashMap<String,LinkedList<GCodeMesh>> sceneGCode;

    private final EventBus eventBus;
    private final SceneGraph sceneGraph;


    @Autowired
    public GCodeGraph(EventBus eventBus, SceneGraph sceneGraph){
        this.eventBus = eventBus;
        this.sceneGraph = sceneGraph;

        sceneGCode = new LinkedHashMap<>();
        for (GCodeMoveType type : GCodeMoveType.values()){
            sceneGCode.put(type.name(), new LinkedList<>());
        }

        eventBus.subscribe(EventType.GCODE_LAYER_GENERATED.name(), this::addGCodeMesh);
    }

    public void addGCodeMesh(Event e){

        GCodeLayer layer = (GCodeLayer)e.getData();

        for (LinkedList<GCodeMesh> meshList: layer.getGeometry().values()){
            for (GCodeMesh mesh : meshList){
                sceneGCode.get(mesh.getType().name()).add(mesh);
                sceneGraph.getSceneGroup().getChildren().add(mesh.getNode());
            }
        }
    }

    public void addGCodeMesh(GCodeLayer layer){
        for (LinkedList<GCodeMesh> meshList: layer.getGeometry().values()){
            for (GCodeMesh mesh : meshList){
                sceneGCode.get(mesh.getType().name()).add(mesh);
                sceneGraph.getSceneGroup().getChildren().add(mesh.getNode());
            }
        }
    }

    public void showGCodeType(GCodeMoveType type, boolean value){
        for(GCodeMesh mesh : sceneGCode.get(type.name())){
            mesh.setVisible(value);
        }
    }

    public void showGCodeTypes(ArrayList<GCodeMoveType> types, boolean value){
        for (GCodeMoveType type : types){
            this.showGCodeType(type, value);
        }
    }
}
