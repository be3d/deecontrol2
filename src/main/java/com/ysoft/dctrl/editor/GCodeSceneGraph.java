package com.ysoft.dctrl.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.importer.GCodeImporter;
import com.ysoft.dctrl.editor.importer.YieldImportRunner;
import com.ysoft.dctrl.editor.mesh.GCodeLayer;
import com.ysoft.dctrl.editor.mesh.GCodeMoveType;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.utils.files.FilePath;
import com.ysoft.dctrl.utils.files.FilePathResource;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.shape.MeshView;

/**
 * Created by pilar on 20.7.2017.
 */
@Component
@SubSceneMode(SceneMode.GCODE)
public class GCodeSceneGraph extends SubSceneGraph {
    private final static List<GCodeMoveType> DRAFT_RENDER_TYPES = Arrays.asList(
            GCodeMoveType.WALL_OUTER,
            GCodeMoveType.SUPPORT,
            GCodeMoveType.SKIN,
            GCodeMoveType.SKIRT,
            GCodeMoveType.NONE
    );

    private enum ViewType {DETAILED, OPTIMIZED}
    private ViewType activeView = ViewType.OPTIMIZED;
    private List<GCodeMoveType> currentlyRenderedTypes = new LinkedList<>();
    private int currentCutBottomLayerIndex = 0;
    private int currentCutTopLayerIndex = 0;

    private final String gcodeFile;
    private List<GCodeLayer> layers;
    private int layerCount = Integer.MIN_VALUE;

    @Autowired
    public GCodeSceneGraph(EventBus eventBus, FilePathResource filePathResource) {
        super(eventBus);
        gcodeFile = filePathResource.getPath(FilePath.SLICER_GCODE_FILE);
        layers = new LinkedList<>();

        eventBus.subscribe(EventType.GCODE_IMPORT_COMPLETED.name(), (e) -> layerCount = (int)e.getData());
        eventBus.subscribe(EventType.GCODE_DRAFT_RENDER_FINISHED.name(), (e) -> {
            currentlyRenderedTypes.clear();
            DRAFT_RENDER_TYPES.forEach((v) -> currentlyRenderedTypes.add(v));
            activeView = ViewType.OPTIMIZED;
        });
        eventBus.subscribe(EventType.SCENE_SET_MODE.name(), (e) -> {
            if(e.getData() != SceneMode.GCODE){
                resetGraph();
            }
        });
    }

    public void loadGCode() {
        resetGraph();

        GCodeImporter gCodeImporter = new GCodeImporter(eventBus);
        YieldImportRunner<GCodeLayer> importRunner = new YieldImportRunner<>(eventBus, gCodeImporter, gcodeFile);
        importRunner.setOnYield((l)-> {
            loadGCodeLayerDraft(l);

            if (l.getNumber() == layerCount-1){
                eventBus.publish(new Event(EventType.GCODE_DRAFT_RENDER_FINISHED.name(), layerCount));
            }
        });

        new Thread(importRunner).start();
    }

    public void loadGCodeLayerDraft(GCodeLayer l) {
        layers.add(l);

        DRAFT_RENDER_TYPES.forEach((t) -> {
            MeshView v = l.getMeshViews().get(t.name());
            if(v == null) { return; }
            getSceneGroup().getChildren().add(v);
        });
    }

    public void loadGCodeLayerDetail(GCodeLayer l) {
        ObservableList<Node> ch = getSceneGroup().getChildren();
        for (GCodeMoveType t : GCodeMoveType.values()){
            if (!currentlyRenderedTypes.contains(t)){
                MeshView v = l.getMeshViews().get(t.name());
                if(v == null) { continue; }
                if(!ch.contains(v)) { ch.add(v); }
            }
        }
    }

    public void loadGCodeLayerDetail(int index){
        if(layers.size()>0){
            loadGCodeLayerDetail(layers.get(index));
        }
    }

    public void unloadGCodeLayerDetail(GCodeLayer l) {
        ObservableList<Node> ch = getSceneGroup().getChildren();
        for (GCodeMoveType t : GCodeMoveType.values()){
            if(!currentlyRenderedTypes.contains(t)){
                MeshView v = l.getMeshViews().get(t.name());
                if(v == null) { continue; }
                if(ch.contains(v)) { ch.remove(v); }
            }
        }
    }

    public void unloadGCodeLayerDetail(int index){
        unloadGCodeLayerDetail(layers.get(index));
    }

    public void showGCodeType(GCodeMoveType type, boolean value) {
        ObservableList<Node> ch = getSceneGroup().getChildren();
        layers.forEach((l) -> {
            l.setVisible(l.getNumber() >= currentCutBottomLayerIndex && l.getNumber() <= currentCutTopLayerIndex);
            MeshView v = l.getMeshViews().get(type.name());
            boolean contains = ch.contains(v);
            if(value){
                if(!contains && v != null) { ch.add(v); }
            } else {
                if(contains) { ch.remove(v); }
            }
        });
        if(value) {
            currentlyRenderedTypes.add(type);
        } else {
            currentlyRenderedTypes.remove(type);
        }
    }

    public void showGCodeTypes(Collection<GCodeMoveType> types, boolean value){
        ObservableList<Node> ch = getSceneGroup().getChildren();
        layers.forEach((l) -> {
            l.setVisible(l.getNumber() >= currentCutBottomLayerIndex && l.getNumber() <= currentCutTopLayerIndex);
            for(GCodeMoveType t : types) {
                MeshView v = l.getMeshViews().get(t.name());
                boolean contains = ch.contains(v);
                if(value && !currentlyRenderedTypes.contains(t)){
                    if(!contains && v != null) { ch.add(v); }
                } else if (!value && currentlyRenderedTypes.contains(t)){
                    if(contains) { ch.remove(v); }
                }
            }

        });
        types.forEach((t) -> {
            if(value){
                if(!currentlyRenderedTypes.contains(t)) {currentlyRenderedTypes.add(t); }
            } else {
                if(currentlyRenderedTypes.contains(t)) {currentlyRenderedTypes.remove(t); }
            }
        });
    }

    public void showJustGCodeTypes(Collection<GCodeMoveType> types, boolean value) {
        ObservableList<Node> ch = getSceneGroup().getChildren();
        layers.forEach((l) -> {
            l.setVisible(l.getNumber() >= currentCutBottomLayerIndex && l.getNumber() <= currentCutTopLayerIndex);
            for(GCodeMoveType t : GCodeMoveType.values()){
                MeshView v = l.getMeshViews().get(t.name());
                if(v == null) { continue; }
                if(types.contains(t) && value) {
                    if(!ch.contains(v)) { ch.add(v); }
                } else {
                    ch.remove(v);
                }
            }
        });
        currentlyRenderedTypes.clear();
        currentlyRenderedTypes.addAll(types);
    }

    public void cutViewAtLayer(int bottom, int top) {
        int oldCutBottomLayer = currentCutBottomLayerIndex;
        int oldCutTopLayer = currentCutTopLayerIndex;
        currentCutTopLayerIndex = top;
        currentCutBottomLayerIndex = bottom;

        switch(activeView){
            case OPTIMIZED: {
                unloadGCodeLayerDetail(layers.get(oldCutBottomLayer));
                unloadGCodeLayerDetail(layers.get(oldCutTopLayer));
                loadGCodeLayerDetail(layers.get(currentCutBottomLayerIndex));
                loadGCodeLayerDetail(layers.get(currentCutTopLayerIndex));
            }
                break;
        }
        layers.forEach((l) -> l.setVisible(l.getNumber() >= bottom && l.getNumber() <= top));
    }

    public void showOptimizedView(){
        showJustGCodeTypes(DRAFT_RENDER_TYPES, true);
        loadGCodeLayerDetail(currentCutBottomLayerIndex);
        loadGCodeLayerDetail(currentCutTopLayerIndex);
        setActiveView(ViewType.OPTIMIZED);
    }

    public void showDetailedView(){
        // The geometry to show is already defined by selected checkboxes (GCodePanelController)
        unloadGCodeLayerDetail(currentCutBottomLayerIndex);
        unloadGCodeLayerDetail(currentCutTopLayerIndex);
        setActiveView(ViewType.DETAILED);
    }

    private void setActiveView(ViewType view){
        activeView = view;
    }

    private void resetGraph(){
        currentlyRenderedTypes = new LinkedList<>();
        currentCutTopLayerIndex = 0;
        currentCutBottomLayerIndex = 0;
        layerCount = Integer.MIN_VALUE;
        layers.clear();
        getSceneGroup().getChildren().clear();
    }

}
