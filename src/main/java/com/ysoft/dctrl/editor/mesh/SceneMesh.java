package com.ysoft.dctrl.editor.mesh;

import java.util.function.Consumer;

import com.ysoft.dctrl.math.BoundingBox;

import javafx.geometry.Point3D;

/**
 * Created by pilar on 6.4.2017.
 */
public interface SceneMesh extends Controllable, DrawableMesh {
    BoundingBox getBoundingBox();
    void setBoundingBoxVisible(boolean visible);
    boolean isBoundingBoxVisible();
    MeshGroup getGroup();
    void setOutOfBounds(boolean outOfBounds);
    boolean isOutOfBounds();
    SceneMesh clone();
    SceneMesh clone(Point3D offset);

    void setSelected(boolean selected);
    void setInvalid(boolean valid);

    void addOnMeshChangeListener(Consumer<SceneMesh> consumer);
    void removeOnMeshChangeListener(Consumer<SceneMesh> consumer);
}
