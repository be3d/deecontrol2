package com.ysoft.dctrl.editor.mesh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ysoft.dctrl.math.BoundingBox;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Material;

/**
 * Created by pilar on 4.4.2017.
 */
public class MeshGroup extends AbstractControllable implements SceneMesh {
    private Group groupNode;
    private List<ExtendedMesh> group;

    public MeshGroup() {
        group = new ArrayList<>();
        groupNode = new Group();
    }

    public MeshGroup(Collection<ExtendedMesh> meshes) {
        group = new ArrayList<>(meshes);
        groupNode = new Group();
        group.forEach(this::addMeshNode);
    }

    public void addMesh(ExtendedMesh mesh) {
        group.add(mesh);
        addMeshNode(mesh);
    }

    private void addMeshNode(ExtendedMesh mesh) {
        groupNode.getChildren().add(mesh.getNode());
    }

    @Override
    public Node getNode() {
        return groupNode;
    }

    @Override
    public void setMaterial(Material material) {
        group.forEach(mesh -> mesh.setMaterial(material));
    }

    @Override
    public void setScale(Point3D scale) {

    }

    @Override
    public Point3D getScale() {
        return null;
    }

    @Override
    public void setRotation(Point3D rotation) {

    }

    @Override
    public Point3D getRotation() {
        return null;
    }

    @Override
    public void setPosition(Point3D position) {

    }

    @Override
    public Point3D getPosition() {
        return null;
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }
}
