package com.ysoft.dctrl.editor.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.annotation.PostConstruct;

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
public class ModelInsertionStack extends LinkedHashMap<ExtendedMesh, String> {
    private final EventBus eventBus;

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

        return (new ArrayList<>(values())).get(index);
    }

    @Override
    public String put(ExtendedMesh key, String value) {
        String res = super.put(key, value);
        onChange();
        return res;
    }

    @Override
    public void putAll(Map<? extends ExtendedMesh, ? extends String> m) {
        super.putAll(m);
        onChange();
    }

    @Override
    public String remove(Object key) {
        String res = super.remove(key);
        onChange();
        return res;
    }

    @Override
    public String putIfAbsent(ExtendedMesh key, String value) {
        String res = super.putIfAbsent(key, value);
        onChange();
        return res;
    }

    @Override
    public boolean remove(Object key, Object value) {
        boolean res = super.remove(key, value);
        onChange();
        return res;
    }

    @Override
    public boolean replace(ExtendedMesh key, String oldValue, String newValue) {
        boolean res = super.replace(key, oldValue, newValue);
        onChange();
        return res;
    }

    @Override
    public String replace(ExtendedMesh key, String value) {
        String res = super.replace(key, value);
        onChange();
        return res;
    }

    @Override
    public String computeIfAbsent(ExtendedMesh key, Function<? super ExtendedMesh, ? extends String> mappingFunction) {
        String res = super.computeIfAbsent(key, mappingFunction);
        onChange();
        return res;
    }

    @Override
    public String computeIfPresent(ExtendedMesh key, BiFunction<? super ExtendedMesh, ? super String, ? extends String> remappingFunction) {
        String res = super.computeIfPresent(key, remappingFunction);
        onChange();
        return res;
    }

    @Override
    public String compute(ExtendedMesh key, BiFunction<? super ExtendedMesh, ? super String, ? extends String> remappingFunction) {
        String res = super.compute(key, remappingFunction);
        onChange();
        return res;
    }

    @Override
    public String merge(ExtendedMesh key, String value, BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        String res = super.merge(key, value, remappingFunction);
        onChange();
        return res;
    }
}
