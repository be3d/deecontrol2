package com.ysoft.dctrl.editor.control;

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
public class TrackBallCameraControls {
    private static final double ROTATE_SPEED = 0.01;
    private static final double ZOOM_SCROLL_SPEED = 10;
    private static final double ZOOM_KEY_SPEED = 50;
    private static final double PAN_SPEED = 0.2;

    private static final double MAX_ZOOM = 20;
    private static final double MIN_ZOOM = 800;

    private static final Point3D DEFAULT_LOOK_AT = new Point3D(0,1,0);
    private static final Point2D DEFAULT_LOOK_AXIS = new Point2D(0, 1);

    private final CameraGroup camGroup;

    private final Point3D initialCameraPosition;
    //private Point3D position;

    private double alpha;
    private double theta;

    private Point2D previousMousePosition;

    private State currentState;
    private State previousState;

    private boolean changed;

    public TrackBallCameraControls(CameraGroup camera) {
        this(camera, new Point3D(0, -100, 0));
    }

    public TrackBallCameraControls(CameraGroup cameraGroup, Point3D initialPosition) {
        this.camGroup = cameraGroup;
        this.initialCameraPosition = initialPosition;

        //target = new Point3D(0,0,0);
       // position = new Point3D(0,0,0);

        alpha = Math.PI;
        theta = Math.toRadians(90 - initialPosition.angle(0,0,1));
        previousMousePosition = new Point2D(0,0);
        currentState = State.ENABLED;
        previousState = State.ENABLED;

        //setCameraPosition(new Point3D(initialPosition.getX(), initialPosition.getY(), initialPosition.getZ()));

        setCameraPosition(new Point3D(initialPosition.getX(), initialPosition.getY(), initialPosition.getZ()));

        cameraGroup.setRotationX(Math.toDegrees(-theta));
        cameraGroup.setRotationY(Math.toDegrees(alpha) + 180);
        changed = false;
    }

    private enum State {
        ENABLED, ROTATE, ZOOM, PAN, DISABLED
    }

    private void setState(State state) {
        previousState = currentState;
        currentState = state;
    }

    private void lookAtTarget() {
        double zDiff = camGroup.getPosition().getZ() - camGroup.getTarget().getZ();
        double dist = camGroup.getPosition().distance(camGroup.getTarget());
        double xAngle = Math.toDegrees(Math.acos(zDiff/dist));

        double yAngle = Math.toDegrees(Math.acos(DEFAULT_LOOK_AXIS.dotProduct(getDiff2D(camGroup.getTarget(), camGroup.getPosition()).normalize())));

        camGroup.setRotationX(xAngle);
        camGroup.setRotationY(yAngle);
    }

    public void resetCamera() {
        alpha = Math.PI;
        theta = Math.toRadians(90 - initialCameraPosition.angle(0,0,1));
        camGroup.setRotationX(Math.toDegrees(-theta));
        camGroup.setRotationY(Math.toDegrees(alpha) + 180);
        setCameraPosition(Point3DUtils.copy(initialCameraPosition));
        //target = new Point3D(0,0,0);
        camGroup.setTarget(new Point3D(0,0,0));
    }

    public void setTopView() {
        //double z = target.distance(position);
        double z = getTarget().distance(getPosition());
        setCameraPosition(new Point3D(getTarget().getX(), getTarget().getY(), getTarget().getZ() + z));
        alpha = Math.PI;
        theta = Math.toRadians(90);
        camGroup.setRotationX(Math.toDegrees(-theta));
        camGroup.setRotationY(Math.toDegrees(alpha) + 180);
    }

    public void setLeftView() {

    }

    public void onMousePressed(MouseEvent event) {
        if(currentState != State.ENABLED) { return; }
        changed = false;
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
            case ENABLED:
            case DISABLED:
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
        changed = true;
    }

    public void onMouseReleased(MouseEvent event) {
        setState(State.ENABLED);
        if(changed) { event.consume(); }
    }

    public void onScroll(ScrollEvent event) {
        double d = Math.signum(event.getDeltaY()) * ZOOM_SCROLL_SPEED;
        zoomCamera(d);
    }

    public void zoomInCamera(){
        zoomCamera(ZOOM_KEY_SPEED);
    }

    public void zoomOutCamera(){
        zoomCamera(-ZOOM_KEY_SPEED);
    }

    public void zoomCamera(double d){
        Point3D normal = camGroup.getLookAtVector().multiply(-1);
        Point3D newPosition = camGroup.getPosition().add(normal.normalize().multiply(d));
        //Point3D diff = getDiff3D(target, newPosition);
        Point3D diff = getDiff3D(camGroup.getTarget(), newPosition);
        double diffLen = diff.magnitude();
        if(diffLen > MIN_ZOOM || diffLen < MAX_ZOOM) {
            return;
        }
        //position = newPosition;
        camGroup.setPosition(newPosition);
//        updatePosition();

    }

    public void panCamera(Point2D targetPosition) {
        Point3D diff = getDiff3D(targetPosition, previousMousePosition);
        diff = diff.multiply(PAN_SPEED);

        Matrix3D zRotation = Matrix3DFactory.getZRotationMatrix(-alpha);
        Matrix3D xRotation = Matrix3DFactory.getXRotationMatrix(theta);

        diff = Point3DUtils.applyMatrix(diff, zRotation.multiply(xRotation));

        //position = position.add(diff);
        //target = target.add(diff);

        camGroup.setTarget(camGroup.getTarget().add(diff));
        camGroup.setPosition(camGroup.getPosition().add(diff));

        //updatePosition();
        previousMousePosition = targetPosition;
    }

    public void rotateCamera(Point2D targetPosition) {

        System.out.println("Theta "+Math.toDegrees(theta) + " alpha: "+ Math.toDegrees(alpha));

        Point2D diff = getDiff2D(targetPosition, previousMousePosition);
        alpha += diff.getX() * ROTATE_SPEED;
        theta += diff.getY() * ROTATE_SPEED;
        //alpha += diff.getY() * ROTATE_SPEED;

        if(theta > Math.PI/2) theta = Math.PI/2;
        if(theta < -Math.PI/2) theta = -Math.PI/2;
        double dist = camGroup.getTarget().distance(camGroup.getPosition());
        //double dist = target.distance(new Point3D(-550,600,0));

        double z = Math.sin(theta) * dist;
        double xyDist = Math.cos(theta) * dist;

        double xmove = diff.getX()*1;
        double x = Math.sin(alpha) * xyDist;
        double y = Math.cos(alpha) * xyDist;

        camGroup.setRotationX(Math.toDegrees(-theta));
        camGroup.setRotationY(Math.toDegrees(alpha) + 180);
        //position = target.add(x,y,z);
        camGroup.setPosition(camGroup.getTarget().add(x,y,z));

        //updatePosition();

        previousMousePosition = targetPosition;
    }

    public void setCameraPosition(Point3D position) {
        camGroup.setPosition(position);
    }

    private Point3D getTarget(){
        return camGroup.getTarget();
    }

    private Point3D getPosition(){
        return camGroup.getPosition();
    }

    public void enable() {
        this.currentState = State.ENABLED;
    }

    public void disable() {
        this.currentState = State.DISABLED;
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
