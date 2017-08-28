package com.ysoft.dctrl.editor.mesh;

import java.util.LinkedList;
import java.util.List;

import com.ysoft.dctrl.math.BoundingBox;
import com.ysoft.dctrl.math.TransformMatrix;
import com.ysoft.dctrl.utils.ColorUtils;

import javafx.event.EventHandler;
import javafx.geometry.Point3D;
import javafx.scene.Group;
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
    private boolean isDirty;

    private boolean outOfBounds;

    private MeshView view;
    private Group node;
    private MeshGroup group;

    private List<OnMeshChange> onRotationChange;
    private List<OnMeshChange> onScaleChange;
    private List<OnMeshChange> onPositionChange;

    public ExtendedMesh() {
        this(new MeshView(), new BoundingBox());
    }

    public ExtendedMesh(TriangleMesh mesh) {
        this(new MeshView(mesh), new BoundingBox(mesh.getPoints().toArray(null)));
    }

    private ExtendedMesh(MeshView view, BoundingBox boundingBox) {
        this.view = view;
        view.setUserData(this);
        this.boundingBox = boundingBox;
        boundingBox.setColor(ColorUtils.getColorImage("#aaaa00"));
        isDirty = false;
        outOfBounds = false;
        group = null;
        node = new Group();
        node.getChildren().addAll(view, boundingBox.getNode());
        initTransforms();
    }

    private void initTransforms() {
        onRotationChange = new LinkedList<>();
        onScaleChange = new LinkedList<>();
        onPositionChange = new LinkedList<>();

        view.getTransforms().addAll(position, rotationX, rotationY, rotationZ, scale);
    }

    public BoundingBox getBoundingBox() {
        checkBoundingBox();
        return boundingBox;
    }

    @Override
    public void setBoundingBoxVisible(boolean visible) {
        boundingBox.setNodeVisible(visible);
    }

    private void checkBoundingBox() {
        if(!isDirty) { return; }

        boundingBox.update(((TriangleMesh) view.getMesh()).getPoints().toArray(null), getTransformMatrix());
        isDirty = false;
    }

    public void translateToZero() {
        MeshUtils.translateVertexesAndUpdateBoundingBox((TriangleMesh) view.getMesh(), boundingBox);
    }

    @Override
    public void setScale(Point3D scale) {
        this.scale.setX(scale.getX());
        this.scale.setY(scale.getY());
        this.scale.setZ(scale.getZ());
        isDirty = true;
        if(boundingBox.isNodeVisible()) { checkBoundingBox(); }
        handleScaleChange();
    }

    @Override
    public Point3D getScale() {
        return new Point3D(scale.getX(), scale.getY(), scale.getZ());
    }

    @Override
    public void setRotation(Point3D rotation) {
        this.rotationX.setAngle(normalizeRotation(rotation.getX()));
        this.rotationY.setAngle(normalizeRotation(rotation.getY()));
        this.rotationZ.setAngle(normalizeRotation(rotation.getZ()));
        isDirty = true;
        if(boundingBox.isNodeVisible()) { checkBoundingBox(); }
        handleRotationChange();
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
        isDirty = true;
        if(boundingBox.isNodeVisible()) { checkBoundingBox(); }
        handlePositionChange();
    }

    @Override
    public Point3D getPosition() {
        return new Point3D(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public Node getNode() {
        return node;
    }

    public MeshView getView() {
        return view;
    }

    @Override
    public void setMaterial(Material material) {
        view.setMaterial(material);
    }

    public TransformMatrix getTransformMatrix() {
        return (new TransformMatrix()).applyTranslate(getPosition()).applyEuler(getRadRotation()).applyScale(getScale());
    }

    private void handleRotationChange() {
        onRotationChange.forEach(h -> h.accept(this));
    }

    private void handleScaleChange() {
        onScaleChange.forEach(h -> h.accept(this));
    }

    private void handlePositionChange() {
        onPositionChange.forEach(h -> h.accept(this));
    }

    public void addOnRotationChangeListener(OnMeshChange eventHandler) {
        onRotationChange.add(eventHandler);
    }

    public void addOnScaleChangeListener(OnMeshChange eventHandler) {
        onScaleChange.add(eventHandler);
    }

    public void addOnPositionChangeListener(OnMeshChange eventHandler) {
        onPositionChange.add(eventHandler);
    }

    public void addOnMeshChangeListener(OnMeshChange eventHandler) {
        addOnPositionChangeListener(eventHandler);
        addOnRotationChangeListener(eventHandler);
        addOnScaleChangeListener(eventHandler);
    }

    void setGroup(MeshGroup group) {
        this.group = group;
    }

    void removeGroup() {
        group = null;
    }

    @Override
    public MeshGroup getGroup() {
        return group;
    }

    @Override
    public void setOutOfBounds(boolean outOfBounds) {
        this.outOfBounds = outOfBounds;
    }

    @Override
    public boolean isOutOfBounds() {
        return outOfBounds;
    }
}
