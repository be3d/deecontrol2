package com.ysoft.dctrl.editor.mesh;

import java.util.LinkedList;
import java.util.List;

import com.ysoft.dctrl.math.BoundingBox;
import com.ysoft.dctrl.math.TransformMatrix;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.paint.Material;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.TransformChangedEvent;
import javafx.scene.transform.Translate;

/**
 * Created by pilar on 4.4.2017.
 */
public class ExtendedMesh extends AbstractControllable implements SceneMesh {
    private static final double PI_2 = 360;

    private Rotate rotationX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotationY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotationZ = new Rotate(0, Rotate.Z_AXIS);

    private Scale scale = new Scale(1,1,1);
    private Translate position = new Translate(0,0,0);

    private BoundingBox boundingBox;

    private MeshView view;

    private List<OnMeshChange> onRotationChange;
    private List<OnMeshChange> onScaleChange;
    private List<OnMeshChange> onPositionChange;

    public ExtendedMesh() {
        view = new MeshView();
        boundingBox = new BoundingBox();
        initTransforms();
    }

    public ExtendedMesh(TriangleMesh mesh) {
        view = new MeshView(mesh);
        boundingBox = new BoundingBox(mesh.getPoints().toArray(null));
        initTransforms();
    }

    private void initTransforms() {
        onRotationChange = new LinkedList<>();
        onScaleChange = new LinkedList<>();
        onPositionChange = new LinkedList<>();

        rotationX.setOnTransformChanged(this::handleRotationChange);
        rotationY.setOnTransformChanged(this::handleRotationChange);
        rotationZ.setOnTransformChanged(this::handleRotationChange);
        scale.setOnTransformChanged(this::handleScaleChange);
        position.setOnTransformChanged(this::handlePositionChange);

        view.getTransforms().addAll(position, rotationX, rotationY, rotationZ, scale);
    }

    public BoundingBox getBoundingBox() { return boundingBox; }

    public void translateToZero() {
        MeshUtils.translateVertexesAndUpdateBoundingBox((TriangleMesh) view.getMesh(), boundingBox);
    }

    @Override
    public void setScale(Point3D scale) {
        this.scale.setX(scale.getX());
        this.scale.setY(scale.getY());
        this.scale.setZ(scale.getZ());
    }

    @Override
    public Point3D getScale() {
        return new Point3D(scale.getX(), scale.getY(), scale.getZ());
        //return new Point3D(view.getScaleX(), view.getScaleY(), view.getScaleZ());
    }

    @Override
    public void setRotation(Point3D rotation) {
        this.rotationX.setAngle(normalizeRotation(rotation.getX()));
        this.rotationY.setAngle(normalizeRotation(rotation.getY()));
        this.rotationZ.setAngle(normalizeRotation(rotation.getZ()));
    }

    private double normalizeRotation(double value) {
        return value < 0 ? (value%PI_2) + PI_2 : (value >= PI_2 ? value%PI_2 : value);
    }

    @Override
    public Point3D getRotation() {
        return new Point3D(rotationX.getAngle(), rotationY.getAngle(), rotationZ.getAngle());
    }

    private Point3D getRadRotation() {
        return new Point3D(Math.toRadians(rotationX.getAngle()), Math.toRadians(rotationY.getAngle()), Math.toRadians(rotationZ.getAngle()));
    }

    @Override
    public void setPosition(Point3D position) {
        this.position.setX(position.getX());
        this.position.setY(position.getY());
        this.position.setZ(position.getZ());
    }

    @Override
    public Point3D getPosition() {
        return new Point3D(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public Node getNode() {
        return view;
    }

    @Override
    public void setMaterial(Material material) {
        view.setMaterial(material);
    }

    public TransformMatrix getTransformMatrix() {
        return (new TransformMatrix()).applyTranslate(getPosition()).applyEuler(getRadRotation()).applyScale(getScale());
    }

    private void handleRotationChange(TransformChangedEvent event) {
        onRotationChange.forEach(h -> h.accept(this));
    }

    private void handleScaleChange(TransformChangedEvent event) {
        onScaleChange.forEach(h -> h.accept(this));
    }

    private void handlePositionChange(TransformChangedEvent event) {
        onPositionChange.forEach(h -> h.accept(this));
    }
}
