package com.ysoft.dctrl.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

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
    private final static List<GCodeMoveType> DRAFT_RENDER_TYPES = Arrays.asList(GCodeMoveType.WALL_OUTER, GCodeMoveType.SUPPORT);
    private final static List<GCodeMoveType> OTHER_RENDER_TYPES;
    static {
        List<GCodeMoveType> types = new ArrayList<>(Arrays.asList(GCodeMoveType.values()));
        DRAFT_RENDER_TYPES.forEach((t) -> types.remove(t));
        OTHER_RENDER_TYPES = types;
    }

    private final String gcodeFile;
    private List<GCodeLayer> layers;

    @Autowired
    public GCodeSceneGraph(EventBus eventBus, FilePathResource filePathResource) {
        super(eventBus);
        this.gcodeFile = filePathResource.getPath(FilePath.SLICER_GCODE_FILE);
        layers = new LinkedList<>();
    }

    public void loadGCode() {
        layers.clear();
        getSceneGroup().getChildren().clear();
        GCodeImporter gCodeImporter = new GCodeImporter(eventBus);
        YieldImportRunner<GCodeLayer> importRunner = new YieldImportRunner<>(eventBus, gCodeImporter, gcodeFile);
        importRunner.setOnYield((l)-> {
            loadGCodeLayerDraft(l);
        });

        new Thread(importRunner).start();
    }

    public void loadGCodeLayerDraft(GCodeLayer l) {
        layers.add(l);
        if(l.getNumber() < 2) {
            getSceneGroup().getChildren().addAll(l.getMeshViews().values());
            return;
        }

        DRAFT_RENDER_TYPES.forEach((t) -> {
            MeshView v = l.getMeshViews().get(t.name());
            if(v == null) { return; }
            getSceneGroup().getChildren().add(v);
        });
    }

    public void loadGCodeLayerDetail(GCodeLayer l) {
        if(l.getNumber() < 2) { return; }
        OTHER_RENDER_TYPES.forEach((t) -> {
            MeshView v = l.getMeshViews().get(t.name());
            if(v == null) { return; }
            getSceneGroup().getChildren().add(v);
        });
    }

    public void unloadGCodeLayerDetail(GCodeLayer l) {
        if(l.getNumber() < 2) { return; }
        OTHER_RENDER_TYPES.forEach((t) -> {
            MeshView v = l.getMeshViews().get(t.name());
            if(v == null) { return; }
            getSceneGroup().getChildren().remove(v);
        });
    }

    public void showGCodeType(GCodeMoveType type, boolean value) {
        ObservableList<Node> ch = getSceneGroup().getChildren();
        layers.forEach((l) -> {
            l.setVisible(false);
            if(!value) { return; }
            for(GCodeMoveType t : GCodeMoveType.values()) {
                MeshView v = l.getMeshViews().get(t.name());
                if(v == null) { continue; }
                if(t == type) {
                    if(!ch.contains(v)) { ch.add(v); }
                    l.setVisibleOne(t);
                } else {
                    ch.remove(v);
                }
            }
        });
    }

    public void showGCodeTypes(Collection<GCodeMoveType> types, boolean value) {
        types.forEach((t) -> showGCodeType(t, value));
    }

    public void cutViewAtLayer(int number) {
        layers.forEach((l) -> l.setVisible(l.getNumber() < number));
    }

    public void clearOneType(GCodeMoveType type) {
        layers.forEach((l) -> {
            getSceneGroup().getChildren().remove(l.getMeshViews().get(type.name()));
            l.clearMeshView(type);
        });
    }
}
