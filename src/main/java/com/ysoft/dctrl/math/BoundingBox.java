package com.ysoft.dctrl.math;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.collections.ObservableFloatArray;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * Created by pilar on 4.4.2017.
 */
public class BoundingBox {
    private static final double RECT_HEIGHT = 0.5;

    protected Point3D min;
    protected Point3D max;

    private Point2D minRect;
    private Point2D maxRect;

    private Group node;
    private PhongMaterial material;
    private List<Cylinder> lines;
    private List<Triplet> triplets;

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
        this.maxRect = new Point2D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        this.minRect = new Point2D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
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

        if(Math.abs(z - RECT_HEIGHT/2) < RECT_HEIGHT/2) {
            if(x < this.minRect.getX()) { this.minRect = Point2DUtils.setX(this.minRect, x); }
            if(x > this.maxRect.getX()) { this.maxRect = Point2DUtils.setX(this.maxRect, x); }

            if(y < this.minRect.getY()) { this.minRect = Point2DUtils.setY(this.minRect, y); }
            if(y > this.maxRect.getY()) { this.maxRect = Point2DUtils.setY(this.maxRect, y); }
        }
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

    public Point2D getMinRect() {
        return minRect;
    }

    public Point2D getMaxRect() {
        return maxRect;
    }

    public boolean intersects(BoundingBox bb) {
        return !(min.getX() > bb.max.getX() || max.getX() < bb.min.getX() ||
                 min.getY() > bb.max.getY() || max.getY() < bb.min.getY() ||
                 min.getZ() > bb.max.getZ() || max.getZ() < bb.min.getZ());
    }

    public boolean contains(Point3D point) {
        return compareWithDeviation(min.getX(), point.getX()) && compareWithDeviation(point.getX(), max.getX()) &&
                compareWithDeviation(min.getY(), point.getY()) && compareWithDeviation(point.getY(), max.getY()) &&
                compareWithDeviation(min.getZ(), point.getZ()) && compareWithDeviation(point.getZ(), max.getZ());
    }

    protected boolean compareWithDeviation(double low, double high) {
        return compareWithDeviation(low, high, 1E-5);
    }

    protected boolean compareWithDeviation(double low, double high, double deviation) {
        return high > low || Math.abs(high-low) < deviation;
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
        initTriplets();
        node = new Group();
        node.setMouseTransparent(true);
        material = new PhongMaterial();
        material.setDiffuseColor(Color.BLACK);
        Rotate z90 = new Rotate(90, new Point3D(0,0,1));
        Rotate x0 = new Rotate(0);
        Rotate x90 = new Rotate(90, new Point3D(1,0,0));
        List<Rotate> rotates = new LinkedList<>(Arrays.asList(
                x90,z90,x90,z90,
                x0,x0,x0,x0,
                x90,z90,x90,z90
        ));
        lines = createCylinders(rotates);
        node.getChildren().addAll(lines);
    }

    private void initTriplets() {
        triplets = new LinkedList<>();
        triplets.add(new Triplet(this::getMin, this::getMin, this::getCenter));
        triplets.add(new Triplet(this::getCenter, this::getMin, this::getMin));
        triplets.add(new Triplet(this::getMax, this::getMin, this::getCenter));
        triplets.add(new Triplet(this::getCenter, this::getMin, this::getMax));
        triplets.add(new Triplet(this::getMin, this::getCenter, this::getMin));
        triplets.add(new Triplet(this::getMax, this::getCenter, this::getMin));
        triplets.add(new Triplet(this::getMax, this::getCenter, this::getMax));
        triplets.add(new Triplet(this::getMin, this::getCenter, this::getMax));
        triplets.add(new Triplet(this::getMin, this::getMax, this::getCenter));
        triplets.add(new Triplet(this::getCenter, this::getMax, this::getMin));
        triplets.add(new Triplet(this::getMax, this::getMax, this::getCenter));
        triplets.add(new Triplet(this::getCenter, this::getMax, this::getMax));
    }

    private List<Cylinder> createCylinders(Collection<Rotate> rotates) {
        List<Cylinder> res = new LinkedList<>();
        rotates.forEach((r) -> {
            Cylinder c = new Cylinder(0.1,1);
            c.setMaterial(material);
            c.getTransforms().addAll(new Translate(0,0,0), r);
            res.add(c);
        });
        return res;
    }

    private void updateNode() {
        for(int i = 0; i < 12; i++) {
            Translate t = (Translate) lines.get(i).getTransforms().get(0);
            t.setX(triplets.get(i).x.get().getX());
            t.setY(triplets.get(i).y.get().getY());
            t.setZ(triplets.get(i).z.get().getZ());
        }

        lines.get(0).setHeight(getSize().getZ());
        lines.get(1).setHeight(getSize().getX());
        lines.get(2).setHeight(getSize().getZ());
        lines.get(3).setHeight(getSize().getX());
        lines.get(4).setHeight(getSize().getY());
        lines.get(5).setHeight(getSize().getY());
        lines.get(6).setHeight(getSize().getY());
        lines.get(7).setHeight(getSize().getY());
        lines.get(8).setHeight(getSize().getZ());
        lines.get(9).setHeight(getSize().getX());
        lines.get(10).setHeight(getSize().getZ());
        lines.get(11).setHeight(getSize().getX());
    }

    public void setColor(Image colorImage) {
        material.setSelfIlluminationMap(colorImage);
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

    private class Triplet {
        public final Supplier<Point3D> x;
        public final Supplier<Point3D> y;
        public final Supplier<Point3D> z;

        Triplet(Supplier<Point3D> x, Supplier<Point3D> y, Supplier<Point3D> z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }


    }
}
