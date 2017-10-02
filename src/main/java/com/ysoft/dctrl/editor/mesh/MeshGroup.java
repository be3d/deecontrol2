package com.ysoft.dctrl.editor.mesh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.ysoft.dctrl.math.BoundingBox;
import com.ysoft.dctrl.math.Point3DUtils;
import com.ysoft.dctrl.math.TransformMatrix;
import com.ysoft.dctrl.math.Utils;
import com.ysoft.dctrl.utils.ColorUtils;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Material;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * Created by pilar on 4.4.2017.
 */
public class MeshGroup extends AbstractControllable implements SceneMesh {
    private Group groupNode;
    private List<ExtendedMesh> group;
    private BoundingBox boundingBox;

    private Translate position;
    private Scale scale;

    private List<Consumer<SceneMesh>> onChangeListeners;

    private boolean outOfBounds;

    public MeshGroup() {
        onChangeListeners = new LinkedList<>();
        group = new ArrayList<>();
        groupNode = new Group();
        boundingBox = new BoundingBox();
        boundingBox.setColor(ColorUtils.getColorImage("#0081ea"));
        position = new Translate(0,0,0);
        scale = new Scale(1,1,1);
        groupNode.getChildren().add(boundingBox.getNode());
        outOfBounds = false;
    }

    public MeshGroup(Collection<SceneMesh> meshes) {
        this();
        meshes.forEach(this::addMesh);
    }

    private MeshGroup(MeshGroup other) {
        this();
        setPosition(other.getPosition());
        setScale(other.getScale());
        other.getChildren().forEach(ch -> {
            addMesh(ch.clone());
        });
    }

    public void addMesh(SceneMesh mesh) {
        if(mesh instanceof ExtendedMesh) {
            addMesh((ExtendedMesh) mesh);
        } else if(mesh instanceof MeshGroup) {
            ((MeshGroup) mesh).getChildren().forEach(this::addMesh);
        }
    }

    private void addMesh(ExtendedMesh mesh) {
        mesh.setGroup(this);
        group.add(mesh);
        addMeshNode(mesh);
        mesh.setBoundingBoxVisible(false);
        boundingBox.extend(mesh.getBoundingBox());
        updatePosition(boundingBox.getCenter());
        updateScale(new Point3D(1,1,1));
    }

    private void addMeshNode(ExtendedMesh mesh) {
        groupNode.getChildren().add(mesh.getNode());
    }

    public List<ExtendedMesh> getChildren() { return group; }

    public void dismiss() {
        groupNode.getChildren().clear();
        group.forEach(ExtendedMesh::removeGroup);
        group.clear();
    }

    @Override
    public Node getNode() {
        return groupNode;
    }

    @Override
    public void setMaterial(Material material) {
        group.forEach(mesh -> mesh.setMaterial(material));
    }

    private void updateScale(Point3D scale) {
        this.scale.setX(scale.getX());
        this.scale.setY(scale.getY());
        this.scale.setZ(scale.getZ());
    }

    @Override
    public void setScale(Point3D scale) {
        double diffX = scale.getX()/this.scale.getX();
        double diffY = scale.getY()/this.scale.getY();
        double diffZ = scale.getZ()/this.scale.getZ();

        group.forEach(m -> {
            m.setScale(new Point3D(m.getScaleX()*diffX, m.getScaleY()*diffY, m.getScaleZ()*diffZ));
            Point3D pos = m.getPosition();
            double mX = ((pos.getX() - position.getX()) * diffX) + position.getX();
            double mY = ((pos.getY() - position.getY()) * diffY) + position.getY();
            double mZ = ((pos.getZ() - position.getZ()) * diffZ) + position.getZ();
            m.setPosition(new Point3D(mX, mY, mZ));
        });

        refreshBoundingBox();
        updateScale(scale);
        onChange();
    }

    @Override
    public Point3D getScale() {
        return new Point3D(scale.getX(), scale.getY(), scale.getZ());
    }

    @Override
    public void setRotation(Point3D rotation) {
        TransformMatrix posMatrix = TransformMatrix.fromEulerDeg(rotation);
        Point3D axis = rotation.normalize();
        double max = Utils.max(rotation.getX(), rotation.getY(), rotation.getZ());
        double min = Utils.min(rotation.getX(), rotation.getY(), rotation.getZ());
        double angle = (Math.abs(max) > Math.abs(min)) ? max : min;

        group.forEach(m -> {
            Point3D p = m.getPosition().subtract(position.getX(),position.getY(),position.getZ());
            m.setPosition(posMatrix.applyTo(p).add(position.getX(),position.getY(),position.getZ()));
            TransformMatrix rotMatrix = TransformMatrix.getRotationAxis(axis, Math.toRadians(-angle));
            rotMatrix.multiply(TransformMatrix.fromEulerDeg(m.getRotation()));
            m.setRotation(rotMatrix.toEulerDeg());
        });
        refreshBoundingBox();
        onChange();
    }

    @Override
    public Point3D getRotation() {
        return Point3D.ZERO;
    }

    private void updatePosition(Point3D position) {
        this.position.setX(position.getX());
        this.position.setY(position.getY());
        this.position.setZ(position.getZ());
    }

    @Override
    public void setPosition(Point3D position) {
        double diffX = position.getX() - this.position.getX();
        double diffY = position.getY() - this.position.getY();
        group.forEach((m) -> m.setPosition(new Point2D(m.getPositionX()+diffX, m.getPositionY()+diffY)));
        refreshBoundingBox();
        updatePosition(position);
        onChange();
    }

    @Override
    public Point3D getPosition() {
        return new Point3D(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public void setBoundingBoxVisible(boolean visible) {
        boundingBox.setNodeVisible(visible);
    }

    private void refreshBoundingBox() {
        boundingBox.reset();
        group.forEach(m -> boundingBox.extend(m.getBoundingBox()));
        updatePosition(boundingBox.getCenter());
    }

    @Override
    public MeshGroup getGroup() { return null; }

    @Override
    public void setOutOfBounds(boolean outOfBounds) {
        this.outOfBounds = outOfBounds;
    }

    @Override
    public boolean isOutOfBounds() {
        return outOfBounds;
    }

    @Override
    public SceneMesh clone() {
        return new MeshGroup(this);
    }

    private void onChange() {
        onChangeListeners.forEach(c -> c.accept(this));
    }

    @Override
    public void addOnMeshChangeListener(Consumer<SceneMesh> consumer) {
        onChangeListeners.add(consumer);
    }

    @Override
    public void removeOnMeshChangeListener(Consumer<SceneMesh> consumer) {
        onChangeListeners.remove(consumer);
    }
}
