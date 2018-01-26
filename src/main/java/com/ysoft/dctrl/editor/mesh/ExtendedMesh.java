package com.ysoft.dctrl.editor.mesh;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.ysoft.dctrl.math.BoundingBox;
import com.ysoft.dctrl.math.TransformMatrix;
import com.ysoft.dctrl.utils.ColorUtils;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * Created by pilar on 4.4.2017.
 */
public class ExtendedMesh extends AbstractControllable implements SceneMesh {
    private static PhongMaterial MATERIAL = new PhongMaterial(Color.web("#cccccc"));
    private static PhongMaterial SELECTED_MATERIAL = new PhongMaterial(Color.web("#4dc824"));
    private static PhongMaterial INVALID_MATERIAL = new PhongMaterial(Color.web("#ff9999"));
    private static PhongMaterial INVALID_SELECTED_MATERIAL = new PhongMaterial(Color.web("#ff0000"));

    static {
        MATERIAL.setSpecularColor(Color.web("#333333"));
        MATERIAL.setSpecularPower(10);

        SELECTED_MATERIAL.setSpecularColor(Color.web("#333333"));
        SELECTED_MATERIAL.setSpecularPower(10);

        INVALID_MATERIAL.setSpecularColor(Color.web("#333333"));
        INVALID_MATERIAL.setSpecularPower(10);

        INVALID_SELECTED_MATERIAL.setSpecularColor(Color.web("#333333"));
        INVALID_SELECTED_MATERIAL.setSpecularPower(10);
    }

    private static PhongMaterial[] MATERIAL_LIST = new PhongMaterial[] {MATERIAL, SELECTED_MATERIAL, INVALID_MATERIAL, INVALID_SELECTED_MATERIAL};

    private static int NEXT_ID = 0;
    private static final double PI_2 = 360;

    private final int id;
    private final String name;

    private Rotate rotationX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotationY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotationZ = new Rotate(0, Rotate.Z_AXIS);

    private Scale scale = new Scale(1,1,1);
    private Translate position = new Translate(0,0,0);

    private BoundingBox boundingBox;
    private boolean isDirty;

    private boolean outOfBounds;
    private int cloneCounter;

    private MeshView view;
    private Group node;
    private MeshGroup group;

    private boolean invalid;
    private boolean selected;

    private List<Consumer<SceneMesh>> onRotationChange;
    private List<Consumer<SceneMesh>> onScaleChange;
    private List<Consumer<SceneMesh>> onPositionChange;

    public ExtendedMesh(String name) {
        this(name, new MeshView(), new BoundingBox());
    }

    public ExtendedMesh(String name, TriangleMesh mesh) {
        this(name, new MeshView(mesh), new BoundingBox(mesh.getPoints().toArray(null)));
    }

    private ExtendedMesh(String name, MeshView view, BoundingBox boundingBox) {
        this.id = NEXT_ID++;
        this.name = name;
        this.view = view;
        view.setUserData(this);
        this.boundingBox = boundingBox;
        boundingBox.setColor(ColorUtils.getColorImage("#0081ea"));
        isDirty = false;
        outOfBounds = false;
        cloneCounter = 0;
        group = null;
        node = new Group();
        node.getChildren().addAll(view, boundingBox.getNode());
        initTransforms();
    }

    private ExtendedMesh(ExtendedMesh other) {
        this(other.getName(), (TriangleMesh) other.getView().getMesh());
        setPosition(other.getPosition());
        setRotation(other.getRotation());
        setScale(other.getScale());
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

    @Override
    public boolean isBoundingBoxVisible() { return boundingBox.isNodeVisible(); }

    private void checkBoundingBox() {
        if(!isDirty) { return; }

        boundingBox.update(((TriangleMesh) view.getMesh()).getPoints().toArray(null), getTransformMatrix());
        isDirty = false;
    }

    public void translateToZero() {
        MeshUtils.translateVertexesAndUpdateBoundingBox((TriangleMesh) view.getMesh(), boundingBox);
        checkBoundingBox();
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

    private void setMaterial() {
        int mode = (selected ? 1 : 0) + (invalid ? 2 : 0);
        setMaterial(MATERIAL_LIST[mode]);
    }

    @Override
    public Material getMaterial() { return view.getMaterial(); }

    public TransformMatrix getTransformMatrix() {
        return (new TransformMatrix()).applyTranslate(getPosition()).applyEuler(getRadRotation()).applyScale(getScale());
    }

    private void handleRotationChange() {
        cloneCounter = 0;
        onRotationChange.forEach(h -> h.accept(this));
    }

    private void handleScaleChange() {
        cloneCounter = 0;
        onScaleChange.forEach(h -> h.accept(this));
    }

    private void handlePositionChange() {
        cloneCounter = 0;
        onPositionChange.forEach(h -> h.accept(this));
    }

    public void addOnRotationChangeListener(Consumer<SceneMesh> eventHandler) {
        onRotationChange.add(eventHandler);
    }

    public void addOnScaleChangeListener(Consumer<SceneMesh> eventHandler) {
        onScaleChange.add(eventHandler);
    }

    public void addOnPositionChangeListener(Consumer<SceneMesh> eventHandler) {
        onPositionChange.add(eventHandler);
    }

    @Override
    public void addOnMeshChangeListener(Consumer<SceneMesh> eventHandler) {
        addOnPositionChangeListener(eventHandler);
        addOnRotationChangeListener(eventHandler);
        addOnScaleChangeListener(eventHandler);
    }

    public void removeOnRotationChangeListener(Consumer<SceneMesh> eventHandler) {
        onRotationChange.remove(eventHandler);
    }

    public void removeOnScaleChangeListener(Consumer<SceneMesh> eventHandler) {
        onScaleChange.remove(eventHandler);
    }

    public void removeOnPositionChangeListener(Consumer<SceneMesh> eventHandler) {
        onPositionChange.remove(eventHandler);
    }

    @Override
    public void removeOnMeshChangeListener(Consumer<SceneMesh> consumer) {
        removeOnPositionChangeListener(consumer);
        removeOnRotationChangeListener(consumer);
        removeOnScaleChangeListener(consumer);
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

    public int getID() { return id; }

    public String getName() { return name; }

    @Override
    public SceneMesh clone() {
        return new ExtendedMesh(this);
    }

    @Override
    public SceneMesh clone(Point3D offset) {
        SceneMesh cloned = clone();
        cloned.setPosition(cloned.getPosition().add(offset.multiply(++cloneCounter)));
        return cloned;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        setMaterial();
    }

    @Override
    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
        setMaterial();
    }
}
