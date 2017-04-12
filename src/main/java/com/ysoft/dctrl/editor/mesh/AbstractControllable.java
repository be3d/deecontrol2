package com.ysoft.dctrl.editor.mesh;

import javafx.geometry.Point3D;

/**
 * Created by pilar on 6.4.2017.
 */
public abstract class AbstractControllable implements Controllable {
    @Override
    public void scale(double scale) {
        setScale(getScale().multiply(scale));
    }

    @Override
    public void scale(Point3D scale) {
        setScale(new Point3D(getScaleX()*scale.getX(), getScaleY()*scale.getY(), getScaleZ()*scale.getZ()));
    }

    @Override
    public void scaleX(double scale) {
        setScaleX(getScaleX()*scale);
    }

    @Override
    public void scaleY(double scale) {
        setScaleY(getScaleY()*scale);
    }

    @Override
    public void scaleZ(double scale) {
        setScaleZ(getScaleZ()*scale);
    }

    @Override
    public void setScale(double scale) {
        setScale(new Point3D(scale, scale, scale));
    }

    @Override
    public void setScaleX(double scale) {
        setScale(new Point3D(scale, getScaleY(), getScaleZ()));
    }

    @Override
    public void setScaleY(double scale) {
        setScale(new Point3D(getScaleX(), scale, getScaleZ()));
    }

    @Override
    public void setScaleZ(double scale) {
        setScale(new Point3D(getScaleX(), getScaleY(), scale));
    }

    @Override
    public double getScaleX() {
        return getScale().getX();
    }

    @Override
    public double getScaleY() {
        return getScale().getY();
    }

    @Override
    public double getScaleZ() {
        return getScale().getZ();
    }

    @Override
    public void rotate(Point3D rotation) {
        setRotation(getRotation().add(rotation));
    }

    @Override
    public void rotateX(double angle) {
        setRotationX(getRotationX() + angle);
    }

    @Override
    public void rotateY(double angle) {
        setRotationY(getRotationY() + angle);

    }

    @Override
    public void rotateZ(double angle) {
        setRotationZ(getRotationZ() + angle);
    }

    @Override
    public void setRotationX(double angle) {
        setRotation(new Point3D(angle, getRotationY(), getRotationZ()));
    }

    @Override
    public void setRotationY(double angle) {
        setRotation(new Point3D(getRotationX(), angle, getRotationZ()));
    }

    @Override
    public void setRotationZ(double angle) {
        setRotation(new Point3D(getRotationX(), getRotationY(), angle));
    }

    @Override
    public double getRotationX() {
        return getRotation().getX();
    }

    @Override
    public double getRotationY() {
        return getRotation().getY();
    }

    @Override
    public double getRotationZ() {
        return getRotation().getZ();
    }

    @Override
    public void move(Point3D diff) {
        setPosition(getPosition().add(diff));
    }

    @Override
    public void moveX(double diff) {
        setPositionX(getPositionX() + diff);
    }

    @Override
    public void moveY(double diff) {
        setPositionY(getPositionY() + diff);
    }

    @Override
    public void moveZ(double diff) {
        setPositionZ(getPositionZ() + diff);
    }

    @Override
    public void setPositionX(double position) {
        setPosition(new Point3D(position, getPositionY(), getPositionZ()));
    }

    @Override
    public void setPositionY(double position) {
        setPosition(new Point3D(getPositionX(), position, getPositionZ()));
    }

    @Override
    public void setPositionZ(double position) {
        setPosition(new Point3D(getPositionX(), getPositionY(), position));
    }

    @Override
    public double getPositionX() {
        return getPosition().getX();
    }

    @Override
    public double getPositionY() {
        return getPosition().getY();
    }

    @Override
    public double getPositionZ() {
        return getPosition().getZ();
    }
}
