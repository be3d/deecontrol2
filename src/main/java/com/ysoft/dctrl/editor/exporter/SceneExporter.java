package com.ysoft.dctrl.editor.exporter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.EditSceneGraph;
import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.mesh.ExtendedMesh;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.math.TransformMatrix;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.geometry.Point3D;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.util.Duration;

/**
 * Created by pilar on 11.4.2017.
 */

@Component
public class SceneExporter {
    private final EventBus eventBus;
    private final DeeControlContext deeControlContext;
    private final EditSceneGraph sceneGraph;

    @Autowired
    public SceneExporter(EventBus eventBus, DeeControlContext deeControlContext, EditSceneGraph sceneGraph) {
        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
        this.sceneGraph = sceneGraph;
    }

    public void exportScene(String outputPath) {
        ExportTask exportTask = new ExportTask(sceneGraph, outputPath, deeControlContext.getCurrentProject().getPrinterTransformMatrix());

        exportTask.setOnSucceeded((e) -> {
            eventBus.publish(new Event(EventType.SCENE_EXPORTED.name(), outputPath));
        });

        exportTask.setOnFailed((e) -> {
            System.err.println("ou snap, something went wrong");
            exportTask.getException().printStackTrace();
        });

        new Thread(exportTask).start();
    }

    private class ExportTask extends Task<Void> {
        BufferedOutputStream outputStream;
        EditSceneGraph sceneGraph;
        TransformMatrix printerTransformMatrix;
        String outputPath;
        volatile MeshConverter currentMeshConverter;
        volatile int totalMeshes;
        volatile int currentMesh;

        ExportTask(EditSceneGraph sceneGraph, String outputPath, TransformMatrix printerTransformMatrix) {
            this.sceneGraph = sceneGraph;
            this.outputPath = outputPath;
            this.printerTransformMatrix = printerTransformMatrix;
        }

        @Override
        protected Void call() throws Exception {
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), (e) -> {
                eventBus.publish(new Event(EventType.SCENE_EXPORT_PROGRESS.name(), getExportProgress()));
            }));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

            try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(outputPath))) {
                outputStream = os;
                currentMesh = 0;
                totalMeshes = 0;
                currentMeshConverter = null;
                LinkedList<SceneMesh> meshes = sceneGraph.getSceneMeshes();
                int facesNumber = 0;
                for(SceneMesh m : meshes) {
                    if(m instanceof ExtendedMesh) {
                        totalMeshes++;
                        facesNumber += ((TriangleMesh) ((MeshView) m.getNode()).getMesh()).getFaces().size()/9;
                    }
                }
                ByteBuffer bb = ByteBuffer.allocate(84);
                bb.order(ByteOrder.LITTLE_ENDIAN);
                bb.position(80);
                bb.putInt(facesNumber);
                writeToOutput(bb.array(), 0, bb.position());
                for(SceneMesh m : meshes) {
                    if(m instanceof ExtendedMesh) {
                        currentMesh++;
                        ExtendedMesh mesh = (ExtendedMesh) m;
                        TransformMatrix a = mesh.getTransformMatrix();
                        TransformMatrix p = new TransformMatrix().applyTranslate(new Point3D(75,75,0));
                        TransformMatrix s = TransformMatrix.getRotationAxis(new Point3D(0,0,1), Math.PI);

                        TransformMatrix res = new TransformMatrix();
                        res.multiply(s);
                        res.multiply(p);
                        res.multiply(a);
                        res.multiplyTranslation(new Point3D(-1,-1,1));
                        currentMeshConverter = new MeshConverter(mesh, res);
                        byte[] converted = currentMeshConverter.convertToStl();
                        try {
                            writeToOutput(converted, 84, converted.length - 84);
                        } catch (IOException e) {
                            System.err.println("fuck");
                            e.printStackTrace();
                        }
                    }
                }

                return null;
            } finally {
                timeline.stop();
            }
        }

        private void writeToOutput(byte[] data, int offset, int len) throws IOException {
            outputStream.write(data, offset, len);

        }

        public double getExportProgress() {
            return (currentMesh - 1 + currentMeshConverter.getProgress())/totalMeshes;
        }
    }
}
