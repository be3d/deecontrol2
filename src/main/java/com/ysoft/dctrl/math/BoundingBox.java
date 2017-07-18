package com.ysoft.dctrl.math;

import javafx.geometry.Point3D;

/**
 * Created by pilar on 4.4.2017.
 */
public class BoundingBox {
    private Point3D min;
    private Point3D max;

    public BoundingBox() {
        init();
    }

    public BoundingBox(float[] vertices) {
        init();
        int len = vertices.length;

        for(int i = 0; i < len; i +=3) {
            checkVertex(vertices[i], vertices[i+1], vertices[i+2]);
        }
    }

    public BoundingBox(float[] vertices, TransformMatrix matrix) {
        update(vertices, matrix);
    }

    private void init() {
        this.max = new Point3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
        this.min = new Point3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public void update(float[] vertices, TransformMatrix matrix) {
        init();
        int len = vertices.length;

        for(int i = 0; i < len; i+=3) {
            checkVertex(matrix.applyTo(vertices[i], vertices[i+1], vertices[i+2]));
        }
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
    }

    public void setMin(Point3D min) {
        this.min = min;
    }

    public void setMax(Point3D max) {
        this.max = max;
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

    public Point3D getSize() {
        return new Point3D(max.getX() - min.getX(), max.getY() - min.getY(), max.getZ() - min.getZ());
    }

    public Point3D getHalfSize() {
        return getSize().multiply(0.5);
    }

    @Override
    public String toString() {
        return "BoundingBox[\n" + min + "\n" + max + "\n]";
    }
}
