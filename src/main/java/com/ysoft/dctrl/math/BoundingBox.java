package com.ysoft.dctrl.math;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.ysoft.dctrl.utils.ColorUtils;

import javafx.collections.ObservableFloatArray;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

/**
 * Created by pilar on 4.4.2017.
 */
public class BoundingBox {
    private Point3D min;
    private Point3D max;

    private MeshView node;
    private ObservableFloatArray nodePoints;

    private Consumer<BoundingBox> onChangeHandler;

    public BoundingBox() {
        init();
    }

    public BoundingBox(float[] vertices) {
        init();
        update(vertices, new TransformMatrix());
    }

    public BoundingBox(float[] vertices, TransformMatrix matrix) {
        init();
        update(vertices, matrix);
    }

    protected void init() {
        onChangeHandler = null;
        initNode();
        reset();
    }

    public void reset() {
        this.max = new Point3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        this.min = new Point3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        nodePoints.clear();
        nodePoints.addAll(new float[8*3]);
    }

    public void update(float[] vertices, TransformMatrix matrix) {
        reset();
        int len = vertices.length;

        for(int i = 0; i < len; i+=3) {
            checkVertex(matrix.applyTo(vertices[i], vertices[i+1], vertices[i+2]));
        }
        updateNode();
        onChange();
    }

    public void extend(BoundingBox bb) {
        this.max = new Point3D(Math.max(max.getX(), bb.getMax().getX()), Math.max(max.getY(), bb.getMax().getY()), Math.max(max.getZ(), bb.getMax().getZ()));
        this.min = new Point3D(Math.min(min.getX(), bb.getMin().getX()), Math.min(min.getY(), bb.getMin().getY()), Math.min(min.getZ(), bb.getMin().getZ()));
        updateNode();
        onChange();
    }

    private void checkVertex(Point3D point) {
        checkVertex(point.getX(), point.getY(), point.getZ());
    }

    private void checkVertex(double x, double y, double z) {
        if(x < this.min.getX()) { this.min = Point3DUtils.setX(this.min, x); }
        if(x > this.max.getX()) { this.max = Point3DUtils.setX(this.max, x); }

        if(y < this.min.getY()) { this.min = Point3DUtils.setY(this.min, y); }
        if(y > this.max.getY()) { this.max = Point3DUtils.setY(this.max, y); }

        if(z < this.min.getZ()) { this.min = Point3DUtils.setZ(this.min, z); }
        if(z > this.max.getZ()) { this.max = Point3DUtils.setZ(this.max, z); }
    }

    public Point3D getMin() {
        return min;
    }

    public Point3D getMax() {
        return max;
    }

    public void set(Point3D min, Point3D max) {
        this.min = min;
        this.max = max;
        updateNode();
        onChange();
    }

    public void setMin(Point3D min) {
        this.min = min;
        updateNode();
        onChange();
    }

    public void setMax(Point3D max) {
        this.max = max;
        updateNode();
        onChange();
    }

    public boolean intersects(BoundingBox bb) {
        return !(min.getX() > bb.max.getX() || max.getX() < bb.min.getX() ||
                 min.getY() > bb.max.getY() || max.getY() < bb.min.getY() ||
                 min.getZ() > bb.max.getZ() || max.getZ() < bb.min.getZ());
    }

    public boolean contains(Point3D point) {
        return min.getX() <= point.getX() && max.getX() >= point.getX() &&
                min.getY() <= point.getY() && max.getY() >= point.getY() &&
                min.getZ() <= point.getZ() && max.getZ() >= point.getZ();
    }

    public boolean contains(BoundingBox bb) {
        return contains(bb.getMin()) && contains(bb.getMax());
    }

    public Point3D getSize() {
        return new Point3D(max.getX() - min.getX(), max.getY() - min.getY(), max.getZ() - min.getZ());
    }

    public Point3D getHalfSize() {
        return getSize().multiply(0.5);
    }

    public Point3D getCenter() { return getMin().add(getHalfSize()); }

    public void setNodeVisible(boolean visible) {
        node.setVisible(visible);
    }

    public boolean isNodeVisible() {
        return node.isVisible();
    }

    public Node getNode() {
        return node;
    }

    private void initNode() {
        TriangleMesh mesh = new TriangleMesh();
        mesh.getPoints().addAll(new float[8*3]);
        mesh.getTexCoords().addAll(0,0);
        mesh.getFaces().addAll(
                0,0,1,0,0,0,
                1,0,3,0,1,0,
                3,0,2,0,3,0,
                2,0,0,0,2,0,
                4,0,5,0,4,0,
                5,0,7,0,5,0,
                7,0,6,0,7,0,
                6,0,4,0,6,0,
                0,0,4,0,0,0,
                1,0,5,0,1,0,
                3,0,7,0,3,0,
                2,0,6,0,2,0
        );
        node = new MeshView(mesh);
        node.setDrawMode(DrawMode.LINE);
        PhongMaterial m = new PhongMaterial();
        m.setDiffuseColor(Color.BLACK);
        m.setSelfIlluminationMap(ColorUtils.getColorImage("#000000"));
        node.setMaterial(m);
        node.setPickOnBounds(false);
        nodePoints = mesh.getPoints();
    }

    private void updateNode() {
        for(int i = 0; i < 8; i++) {
            nodePoints.set(i*3  , (float) (getBit(i, 0) ? max.getX() : min.getX()));
            nodePoints.set(i*3+1, (float) (getBit(i, 1) ? max.getY() : min.getY()));
            nodePoints.set(i*3+2, (float) (getBit(i, 2) ? max.getZ() : min.getZ()));
        }
    }

    public void setColor(Image colorImage) {
        ((PhongMaterial) node.getMaterial()).setSelfIlluminationMap(colorImage);
    }

    private boolean getBit(int value, int bit) {
        return (value & (1<<bit)) > 0;
    }

    private void onChange() {
        if(onChangeHandler != null) {
            onChangeHandler.accept(this);
        }
    }

    public void setOnChange(Consumer<BoundingBox> handler) {
        onChangeHandler = handler;
    }

    @Override
    public String toString() {
        return "BoundingBox[\n" + min + "\n" + max + "\n]";
    }
}
