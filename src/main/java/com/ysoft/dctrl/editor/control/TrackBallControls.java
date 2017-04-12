package com.ysoft.dctrl.editor.control;

import com.sun.javafx.geom.Matrix3f;
import com.ysoft.dctrl.math.AxisAngleRotation;
import com.ysoft.dctrl.math.Matrix3D;
import com.ysoft.dctrl.math.Matrix3DFactory;
import com.ysoft.dctrl.math.Point3DUtils;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * Created by pilar on 24.3.2017.
 */
public class TrackBallControls {
    private static final double ROTATE_SPEED = 0.01;
    private static final double ZOOM_SPEED = 5;
    private static final double PAN_SPEED = 0.1;

    private static final double MAX_ZOOM = 20;
    private static final double MIN_ZOOM = 800;

    private static final Point3D DEFAULT_LOOK_AT = new Point3D(0,1,0);
    private static final Point2D DEFAULT_LOOK_AXIS = new Point2D(0, 1);

    private final ExtendedPerspectiveCamera camera;
    private boolean enabled;
    private Point3D target;
    private Point3D position;

    private double alpha;
    private double theta;

    private Point2D previousMousePosition;

    private State currentState;
    private State previousState;

    public TrackBallControls(ExtendedPerspectiveCamera camera) {
        this(camera, new Point3D(0, -100, 0));
    }

    public TrackBallControls(ExtendedPerspectiveCamera camera, Point3D initialPosition) {
        this.camera = camera;
        target = new Point3D(0,0,0);
        position = new Point3D(0,0,0);
        alpha = Math.PI;
        theta = 0;
        previousMousePosition = new Point2D(0,0);
        currentState = State.NONE;
        previousState = State.NONE;

        setCameraPosition(new Point3D(initialPosition.getX(), initialPosition.getY(), initialPosition.getZ()));
        camera.setRotationX(theta);
        camera.setRotationY(alpha);
    }

    private enum State {
        NONE, ROTATE, ZOOM, PAN
    }

    private void setState(State state) {
        previousState = currentState;
        currentState = state;
    }

    private void lookAtTarget() {
        double zDiff = position.getZ() - target.getZ();
        double dist = position.distance(target);
        double xAngle = Math.toDegrees(Math.acos(zDiff/dist));

        double yAngle = Math.toDegrees(Math.acos(DEFAULT_LOOK_AXIS.dotProduct(getDiff2D(target, position).normalize())));

        camera.setRotationX(xAngle);
        camera.setRotationY(yAngle);
    }

    private void updatePosition() {
        camera.setPosition(position);
    }

    public void onMousePressed(MouseEvent event) {
        if(currentState != State.NONE) { return; }
        switch (event.getButton()) {
            case NONE:
                return;
            case PRIMARY:
                setState(State.ROTATE);
                break;
            case MIDDLE:
                break;
            case SECONDARY:
                setState(State.PAN);
                break;
        }

        previousMousePosition = new Point2D(event.getSceneX(), event.getSceneY());
    }

    public void onMouseDragged(MouseEvent event) {
        Point2D p = new Point2D(event.getSceneX(), event.getSceneY());
        switch (currentState) {
            case NONE:
                return;
            case ROTATE:
                rotateCamera(p);
                break;
            case ZOOM:
                break;
            case PAN:
                panCamera(p);
                break;
        }
    }

    public void onMouseReleased(MouseEvent event) {
        setState(State.NONE);
    }

    public void onScroll(ScrollEvent event) {
        double d = Math.signum(event.getDeltaY()) * ZOOM_SPEED;
        Point3D normal = getLookAtVector().multiply(-1);
        Point3D newPosition = position.add(normal.normalize().multiply(d));
        Point3D diff = getDiff3D(target, newPosition);
        double diffLen = diff.magnitude();
        if(diffLen > MIN_ZOOM || diffLen < MAX_ZOOM) {
            return;
        }
        position = newPosition;
        updatePosition();
    }

    public void panCamera(Point2D targetPosition) {
        Point3D diff = getDiff3D(targetPosition, previousMousePosition);
        diff = diff.multiply(PAN_SPEED);

        Matrix3D zRotation = Matrix3DFactory.getZRotationMatrix(-alpha);
        Matrix3D xRotation = Matrix3DFactory.getXRotationMatrix(theta);

        diff = Point3DUtils.applyMatrix(diff, zRotation.multiply(xRotation));

        position = position.add(diff);
        target = target.add(diff);

        updatePosition();
        previousMousePosition = targetPosition;
    }

    public void rotateCamera(Point2D targetPosition) {
        Point2D diff = getDiff2D(targetPosition, previousMousePosition);
        alpha += diff.getX() * ROTATE_SPEED;
        theta += diff.getY() * ROTATE_SPEED;
        if(theta > Math.PI/2) theta = Math.PI/2;
        if(theta < -Math.PI/2) theta = -Math.PI/2;
        double dist = target.distance(position);

        double z = Math.sin(theta) * dist;
        double xyDist = Math.cos(theta) * dist;

        double x = Math.sin(alpha) * xyDist;
        double y = Math.cos(alpha) * xyDist;

        camera.setRotationX(Math.toDegrees(-theta));
        camera.setRotationY(Math.toDegrees(alpha) + 180);
        position = target.add(x,y,z);
        updatePosition();

        previousMousePosition = targetPosition;
    }

    public Point3D getLookAtVector() {
        Point3D normal = Point3DUtils.copy(position);
        normal = normal.subtract(target);
        normal = normal.normalize();
        return normal;
    }

    public void setTarget(Point3D target) {
        this.target = target;
        lookAtTarget();
    }

    public void setCameraPosition(Point3D position) {
        this.position = position;
        updatePosition();
    }

    public static Point3D getDiff3D(Point2D a, Point2D b) {
        return new Point3D(a.getX() - b.getX(), 0, (a.getY() - b.getY()));
    }

    public static Point3D getDiff3D(Point3D a, Point3D b) {
        return new Point3D(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
    }

    public static Point2D getDiff2D(Point3D a, Point3D b) {
        return new Point2D(a.getX() - b.getX(), a.getY() - b.getY());
    }

    public static Point2D getDiff2D(Point2D a, Point2D b) {
        return new Point2D(a.getX() - b.getX(), a.getY() - b.getY());
    }
}
