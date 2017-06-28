package com.ysoft.dctrl.editor.exporter;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ysoft.dctrl.editor.SceneGraph;
import com.ysoft.dctrl.editor.mesh.ExtendedMesh;
import com.ysoft.dctrl.editor.mesh.SceneMesh;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.utils.DeeControlContext;

import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 11.4.2017.
 */

@Component
public class SceneExporter {
    private final EventBus eventBus;
    private final DeeControlContext deeControlContext;

    private BufferedOutputStream outputStream;

    @Autowired
    public SceneExporter(EventBus eventBus, DeeControlContext deeControlContext) {
        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
        outputStream = null;
    }

    public void exportScene(SceneGraph sceneGraph, String outputPath) {
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(outputPath))) {
            outputStream = os;
            LinkedList<SceneMesh> meshes = sceneGraph.getSceneMeshes();
            int facesNumber = 0;
            for(SceneMesh m : meshes) {
                if(m instanceof ExtendedMesh) {
                    facesNumber += ((TriangleMesh) ((MeshView) m.getNode()).getMesh()).getFaces().size()/9;
                }
            }
            ByteBuffer bb = ByteBuffer.allocate(84);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            bb.position(80);
            bb.putInt(facesNumber);
            writeToOutput(bb.array(), 0, bb.position());
            meshes.forEach((SceneMesh m) -> {
                if(m instanceof ExtendedMesh) {
                    MeshConverter converter = new MeshConverter((ExtendedMesh) m);
                    byte[] converted = converter.convertToStl();
                    try {
                        writeToOutput(converted, 84, converted.length - 84);
                    } catch (IOException e) {
                        System.err.println("fuck");
                        e.printStackTrace();
                    }
                }
            });

            eventBus.publish(new Event(EventType.SCENE_EXPORTED.name(), outputPath));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToOutput(byte[] data, int offset, int len) throws IOException {
        outputStream.write(data, offset, len);
    }
}
