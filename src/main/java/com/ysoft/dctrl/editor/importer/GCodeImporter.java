package com.ysoft.dctrl.editor.importer;

import com.ysoft.dctrl.editor.mesh.GCodeLayer;
import com.ysoft.dctrl.editor.mesh.GCodeMesh;
import com.ysoft.dctrl.editor.mesh.GCodeMeshGenerator;
import com.ysoft.dctrl.editor.mesh.GCodeMoveType;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.math.LineSegment;
import com.ysoft.dctrl.utils.GCodeContext;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.shape.TriangleMesh;

import java.io.*;
import java.util.LinkedList;
import java.util.regex.Pattern;

import static java.lang.Math.*;

/**
 * Created by kuhn on 5/22/2017.
 */
public class GCodeImporter extends YieldModelImporter {

    private final EventBus eventBus;

    private final Pattern TRAVEL_MOVE_PATTERN = Pattern.compile("G0\\s.+");
    private final Pattern LINEAR_MOVE_PATTERN = Pattern.compile("G1\\s.+");
    private final Pattern TRAVEL_MOVE_TYPE_PATTERN = Pattern.compile(";TYPE:.+");
    private final Pattern LAYER_NUMBER_PATTERN = Pattern.compile(";LAYER:[0-9]+");

    private GCodeLayer gCodeLayer = new GCodeLayer(-1); // in CURA it can start with negative layers !! change this
    private LinkedList<GCodeLayer> layers = new LinkedList<>();
    private GCodeContext gCodeContext = new GCodeContext();
    private GCodeMeshGenerator gCodeMeshGenerator = new GCodeMeshGenerator();

    public GCodeImporter(EventBus eventBus){
        super();
        this.eventBus = eventBus;
    }

    @Override
    TriangleMesh load(InputStream stream) throws IOException, IllegalArgumentException {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                //System.out.println(line);

                if (LAYER_NUMBER_PATTERN.matcher(line).matches()) {

                    // todo refactor to functions
                    if (gCodeLayer != null) {
                        this.finalizeSegment(gCodeLayer);

                        layers.add(gCodeLayer);
                        yield(gCodeLayer);
                    }

                    // Init new layer
                    int layerNumber = Integer.parseInt(line.substring(7));
                    gCodeContext.setLayer(layerNumber);
                    gCodeLayer = new GCodeLayer(layerNumber);
                }

                if (TRAVEL_MOVE_TYPE_PATTERN.matcher(line).matches()) {
                    GCodeMoveType moveType = GCodeMoveType.getValueOf(line.substring(6));
                    if (gCodeContext.setMoveType(moveType)) {
                        this.finalizeSegment(gCodeLayer);
                    }
                }

                if (TRAVEL_MOVE_PATTERN.matcher(line).matches()) {
                    if (gCodeContext.setTravelMove(true)) {
                        this.finalizeSegment(gCodeLayer);
                    }

                    gCodeContext.setX(extractGCodeParam(line, "X"));
                    gCodeContext.setY(extractGCodeParam(line, "Y"));
                    gCodeContext.setZ(extractGCodeParam(line, "Z"));

                    try {
                        gCodeLayer.processCmd(GCodeMoveType.TRAVEL,
                                gCodeContext.getX(), gCodeContext.getY(), gCodeContext.getZ());
                    } catch (NumberFormatException e) {
                        System.out.println("Gcode line corrupt: " + line);
                    }
                }

                if (LINEAR_MOVE_PATTERN.matcher(line).matches()) {
                    if (gCodeContext.setTravelMove(false)) {
                        this.finalizeSegment(gCodeLayer);
                    }

                    gCodeContext.setX(extractGCodeParam(line, "X"));
                    gCodeContext.setY(extractGCodeParam(line, "Y"));
                    gCodeContext.setZ(extractGCodeParam(line, "Z"));
                    try {
                        gCodeLayer.processCmd(gCodeContext.getMoveType(),
                                gCodeContext.getX(), gCodeContext.getY(), gCodeContext.getZ());
                    } catch (NumberFormatException e) {
                        System.out.println("Gcode line corrupt: " + line);
                    }
                }
            }

            // Append last layer
            if (gCodeLayer != null)
                this.finalizeSegment(gCodeLayer);
                layers.add(gCodeLayer);
                yield(gCodeLayer);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        eventBus.publish(new Event(EventType.GCODE_IMPORT_COMPLETED.name(), layers));
        return null;
    }

    private Double extractGCodeParam(String line, String paramTag) throws NumberFormatException {
        int pos = line.indexOf(paramTag);
        if (pos > -1) {
            int endPos = line.indexOf(" ", pos);
            if (endPos > -1)
                return Double.parseDouble(line.substring(pos + 1, endPos));
            else
                return Double.parseDouble(line.substring(pos + 1));
        }
        return null;
    }

    private void finalizeSegment(GCodeLayer layer) {
        layer.addMesh(gCodeMeshGenerator.run(layer.getMoveBuffer()));
        layer.clearMoveBuffer();
    }

}
