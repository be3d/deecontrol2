package com.ysoft.dctrl.editor.mesh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ysoft.dctrl.math.BoundingBox;
import com.ysoft.dctrl.math.Point3DUtils;
import com.ysoft.dctrl.math.TransformMatrix;

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

    public MeshGroup() {
        group = new ArrayList<>();
        groupNode = new Group();
        boundingBox = new BoundingBox();
        position = new Translate(0,0,0);
        scale = new Scale(1,1,1);
    }

    public MeshGroup(Collection<SceneMesh> meshes) {
        this();
        meshes.forEach(this::addMesh);
    }

    public void addMesh(SceneMesh mesh) {
        if(mesh instanceof ExtendedMesh) {
            addMesh((ExtendedMesh) mesh);
        } else if(mesh instanceof MeshGroup) {
            ((MeshGroup) mesh).getMeshes().forEach(this::addMesh);
        }
    }

    private void addMesh(ExtendedMesh mesh) {
        mesh.setGroup(this);
        group.add(mesh);
        addMeshNode(mesh);
        boundingBox.extend(mesh.getBoundingBox());
        updatePosition(boundingBox.getCenter());
        updateScale(new Point3D(1,1,1));
    }

    private void addMeshNode(ExtendedMesh mesh) {
        groupNode.getChildren().add(mesh.getNode());
    }

    private List<ExtendedMesh> getMeshes() { return group; }

    public List<? extends SceneMesh> getChildren() { return group; }

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
    }

    @Override
    public Point3D getScale() {
        return new Point3D(scale.getX(), scale.getY(), scale.getZ());
    }

    @Override
    public void setRotation(Point3D rotation) {
        TransformMatrix matrix = TransformMatrix.fromEulerDeg(rotation);

        group.forEach(m -> {
            Point3D p = m.getPosition().subtract(position.getX(),position.getY(),position.getZ());
            m.setPosition(matrix.applyTo(p).add(position.getX(),position.getY(),position.getZ()));
            TransformMatrix mat = TransformMatrix.fromEulerDeg(m.getRotation()).multiply(matrix);
            m.setRotation(mat.toEulerDeg());
        });
        refreshBoundingBox();
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
    }

    @Override
    public Point3D getPosition() {
        return new Point3D(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    private void refreshBoundingBox() {
        boundingBox.reset();
        group.forEach(m -> boundingBox.extend(m.getBoundingBox()));
        updatePosition(boundingBox.getCenter());
    }

    @Override
    public MeshGroup getGroup() { return null; }
}
