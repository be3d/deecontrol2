package com.ysoft.dctrl.editor;

import com.ysoft.dctrl.editor.importer.GCodeImporter;
import com.ysoft.dctrl.editor.importer.YieldImportRunner;
import com.ysoft.dctrl.editor.mesh.*;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;

import com.ysoft.dctrl.utils.DeeControlContext;
import com.ysoft.dctrl.utils.files.FilePath;
import com.ysoft.dctrl.utils.files.FilePathResource;

import javafx.scene.shape.MeshView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by kuhn on 6/8/2017.
 */
@Component
public class GCodeViewer {

    private final EventBus eventBus;
    private final DeeControlContext deeControlContext;
    private final SceneGraph sceneGraph;

    protected ArrayList<GCodeLayer> layers = new ArrayList<>();

    private final String slicedGcodeFile;

    protected final List<String> DRAFT_RENDER_TYPES = Arrays.asList(
            GCodeMoveType.WALL_OUTER.name(),
            GCodeMoveType.SUPPORT.name()
    );

    @Autowired
    public GCodeViewer(EventBus eventBus, DeeControlContext deeControlContext, SceneGraph sceneGraph, FilePathResource filePathResource){
        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
        this.sceneGraph = sceneGraph;
        this.slicedGcodeFile = filePathResource.getPath(FilePath.SLICER_GCODE_FILE);

        eventBus.subscribe(EventType.GCODE_LAYER_GENERATED.name(), this::loadGCodeLayerDraft);
        eventBus.subscribe(EventType.GCODE_LAYER_RENDER_DETAIL.name(), this::loadGCodeLayerDetail);
        eventBus.subscribe(EventType.GCODE_LAYER_REMOVE_DETAIL.name(), this::unloadGCodeLayerDetail);
        eventBus.subscribe(EventType.GCODE_IMPORT_COMPLETED.name(), this::gCodeImportCompleted);
    }

    public void startViewer(){
        GCodeImporter gCodeImporter = new GCodeImporter(eventBus);
        YieldImportRunner<GCodeLayer> importRunner = new YieldImportRunner<>(eventBus, gCodeImporter, slicedGcodeFile);
        importRunner.setOnYield((l)-> {
            eventBus.publish(new Event(EventType.GCODE_LAYER_GENERATED.name(), l));
        });
        sceneGraph.hideAllMeshes();

        new Thread(importRunner).start();
    }

    public void loadGCodeLayerDraft(Event e) {
        GCodeLayer layer = (GCodeLayer) e.getData();
        for (String key : layer.getMeshViews().keySet()) {

            // The first layers are to be rendered completely
            if (layer.getNumber() < 2) {
                sceneGraph.getSceneGroup().getChildren().add(layer.getMeshViews().get(key));
                continue;
            }

            if (DRAFT_RENDER_TYPES.contains(key)) {
                sceneGraph.getSceneGroup().getChildren().add(layer.getMeshViews().get(key));
            }
        }
    }

    public void loadGCodeLayerDetail(Event e){
        GCodeLayer layer = (GCodeLayer)e.getData();
        if(layer.getNumber() > 1){
            for (String key : layer.getMeshViews().keySet()){
                if (!DRAFT_RENDER_TYPES.contains(key)){
                    try{
                        sceneGraph.getSceneGroup().getChildren().add(layer.getMeshViews().get(key));
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void unloadGCodeLayerDetail(Event e){
        GCodeLayer layer = (GCodeLayer)e.getData();
        if(layer.getNumber() > 1){
            for (String key : layer.getMeshViews().keySet()){
                if (!DRAFT_RENDER_TYPES.contains(key)){
                    sceneGraph.getSceneGroup().getChildren().remove(layer.getMeshViews().get(key));
                }
            }
        }
    }

    public void showGCodeType(GCodeMoveType type, boolean value){
        for(GCodeLayer l : layers){

            l.setVisible(false); // hide the whole layer first

            if(value){  // show only the selected type
                for (String key : l.getMeshViews().keySet()){

                    if (key.equals(type.name())){
                        MeshView view = l.getMeshViews().get(key);
                        if (view != null){
                            if (!sceneGraph.getSceneGroup().getChildren().contains(view)){
                                sceneGraph.getSceneGroup().getChildren().add(view);
                            }
                            l.setVisibleOne(type);
                        }
                    } else {
                        sceneGraph.getSceneGroup().getChildren().remove(l.getMeshViews().get(key));
                    }
                }
            }
        }
    }

    public void showGCodeTypes(ArrayList<GCodeMoveType> types, boolean value){
        for (GCodeMoveType type : types){
            this.showGCodeType(type, value);
        }
    }

    public void cutViewAtLayer(int number){
        for (GCodeLayer l : layers){
            if (l.getNumber() < number){
                l.setVisible(true);
            } else {
                l.setVisible(false);
            }
        }
    }

    public void clearOneType(GCodeMoveType type){
        for (GCodeLayer l : layers) {
            for (String key : l.getMeshViews().keySet()){
                if (key.equals(type.name())){
                    sceneGraph.getSceneGroup().getChildren().remove(l.getMeshViews().get(key));
                }
            }

            l.clearMeshView(type);
        }
    }

    private void gCodeImportCompleted(Event e){
        layers = (ArrayList<GCodeLayer>)e.getData();
        System.out.println("Import completed layers: " + layers.size());
    }


}
