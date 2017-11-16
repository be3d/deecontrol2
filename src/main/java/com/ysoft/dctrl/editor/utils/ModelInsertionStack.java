package com.ysoft.dctrl.editor.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.mesh.ExtendedMesh;
import com.ysoft.dctrl.editor.mesh.MeshGroup;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;

/**
 * Created by pilar on 5.9.2017.
 */

@Component
public class ModelInsertionStack extends ArrayList<ExtendedMesh> {
    private static final long serialVersionUID = 1;

    private final transient EventBus eventBus;
    private final transient Comparator<ExtendedMesh> comparator = Comparator.comparingInt(ExtendedMesh::getID);

    @Autowired
    public ModelInsertionStack(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void removeSceneMesh(SceneMesh sceneMesh) {
        if(sceneMesh instanceof MeshGroup) {
            ((MeshGroup) sceneMesh).getChildren().forEach(this::remove);
        } else {
            remove(sceneMesh);
        }

        onChange();
    }

    public void addSceneMesh(SceneMesh sceneMesh) {
        if(sceneMesh instanceof MeshGroup) {
            ((MeshGroup) sceneMesh).getChildren().forEach(this::add);
        } else {
            add((ExtendedMesh) sceneMesh);
        }

        onChange();
    }

    private void onChange() {
        eventBus.publish(new Event(EventType.EDIT_SCENE_MODEL_STACK_CHANGED.name()));
    }

    public String getFirstName() {
        return isEmpty() ? null : getName(0);
    }

    public String getName(int index) {
        if(index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index out of bounds (" + index + "/" + size() + ")");
        }

        return get(index).getName();
    }

    @Override
    public boolean add(ExtendedMesh mesh) {
        int index = Collections.binarySearch(this, mesh, comparator);
        super.add(-index - 1, mesh);
        return true;
    }

    @Override
    public void add(int index, ExtendedMesh element) {
        throw new UnsupportedOperationException("Inserting to index is not supported");
    }

    @Override
    public boolean addAll(Collection<? extends ExtendedMesh> c) {
        boolean res = true;
        for(ExtendedMesh e : c) {
            res = res && add(e);
        }

        return res;
    }

    @Override
    public boolean addAll(int index, Collection<? extends ExtendedMesh> c) {
        throw new UnsupportedOperationException("Inserting to index is not supported");
    }
}
