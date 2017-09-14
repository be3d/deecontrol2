package com.ysoft.dctrl.editor.control;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.action.Action;
import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.action.ModelTranslateAction;
import com.ysoft.dctrl.editor.mesh.ExtendedMesh;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;

import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;

/**
 * Created by pilar on 5.7.2017.
 */

@Component
public class MeshTransformControls {
    private final Box plane;

    private EventBus eventBus;
    private SceneGraph sceneGraph;

    private Mode mode;
    private SceneMesh selected;
    private Point3D offset;

    private Point3D oldPosition;

    @Autowired
    public MeshTransformControls(EventBus eventBus, SceneGraph sceneGraph) {
        this.sceneGraph = sceneGraph;
        this.eventBus = eventBus;
        this.mode = Mode.MOVE;
        this.selected = null;
        this.plane = new Box(Double.MAX_VALUE, Double.MAX_VALUE, 1);
        this.offset = Point3D.ZERO;
    }

    @PostConstruct
    public void init() {
        // These subscribes are prepared for future usages of mouse control during model transformation
        //eventBus.subscribe(EventType.CONTROL_MOVE_MODEL_CLICK.name(), (e) -> setMode(Mode.MOVE));
        //eventBus.subscribe(EventType.CONTROL_SCALE_MODEL_CLICK.name(), (e) -> setMode(Mode.SCALE));
        //eventBus.subscribe(EventType.CONTROL_ROTATE_MODEL_CLICK.name(), (e) -> setMode(Mode.ROTATE));

        sceneGraph.getSceneGroup().setOnMouseDragged(this::onMouseDragged);
        plane.setMaterial(new PhongMaterial(Color.BLUE));
        plane.setMouseTransparent(true);
    }

    private void setMode(Mode mode) {
        this.mode = mode;
    }

    public void onMousePressed(MouseEvent event) {
        if(event.getButton() != MouseButton.PRIMARY) { return; }
        switch (mode) {
            case MOVE:
                Node n = event.getPickResult().getIntersectedNode();
                if(!(n instanceof MeshView) || !(n.getUserData() instanceof ExtendedMesh)) { return; }
                ExtendedMesh em = (ExtendedMesh) n.getUserData();
                selected = em.getGroup() != null ? em.getGroup() : em;
                plane.setMouseTransparent(false);
                plane.setTranslateZ(selected.getPositionZ() - plane.getDepth()/2);
                sceneGraph.setSubSceneMouseTransparent(true);
                Point3D intersection = getPlaneIntersection(selected.getPosition().add(event.getPickResult().getIntersectedPoint()));
                oldPosition = selected.getPosition();
                offset = oldPosition.subtract(intersection);
                event.consume();
                break;
        }
    }

    public void onMouseDragged(MouseEvent event) {
        switch (mode) {
            case MOVE:
                if(selected == null || event.getButton() != MouseButton.PRIMARY) { return; }
                Point2D res = new Point2D(event.getX() + offset.getX(), event.getY() + offset.getY());
                if(event.isControlDown()) { res = roundTo(res, 5); }
                selected.setPosition(res);
                break;
        }
    }

    public void onMouseRelease(MouseEvent event) {
        if(selected == null) { return; }
        switch (mode) {
            case MOVE:
                plane.setMouseTransparent(true);
                sceneGraph.setSubSceneMouseTransparent(false);
                Point3D newPosition = selected.getPosition();
                if(!oldPosition.equals(newPosition)) {
                    Action action = new ModelTranslateAction(selected, oldPosition, newPosition);
                    eventBus.publish(new Event(EventType.ADD_ACTION.name(), action));
                }
                selected = null;
                event.consume();
                break;
        }
    }

    private Point2D roundTo(Point2D p, double step) {
        return new Point2D(step*(Math.round(p.getX()/step)), step*(Math.round(p.getY()/step)));
    }


    public Shape3D getPlane() {
        return plane;
    }

    public Point3D getPlaneIntersection(Point3D intersection) {
        Point3D origin = sceneGraph.getCamera().getPosition();
        Point3D dir = intersection.subtract(origin);
        double dot = dir.dotProduct(new Point3D(0,0,1));
        double d = new Point3D(0,0,plane.getTranslateZ()).subtract(origin).dotProduct(new Point3D(0,0,1))/dot;

        return dir.multiply(d).add(origin);
    }

    private enum Mode {
        ROTATE, SCALE, MOVE
    }
}
