package com.ysoft.dctrl.editor.importer;

import com.ysoft.dctrl.editor.mesh.*;
import com.ysoft.dctrl.event.Event;
import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.event.EventType;
import com.ysoft.dctrl.math.LineSegment;
import com.ysoft.dctrl.utils.GCodeContext;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.shape.TriangleMesh;

import java.io.*;
import java.util.ArrayList;
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
    private ArrayList<GCodeLayer> layers = new ArrayList<>();
    private GCodeContext gCodeContext = new GCodeContext();
    private GCodeMeshGenerator gCodeMeshGenerator = new GCodeMeshGenerator(new GCodeMeshProperties());

    public GCodeImporter(EventBus eventBus){
        super();
        this.eventBus = eventBus;
    }

    @Override
    public TriangleMesh load(InputStream stream) throws IOException, IllegalArgumentException {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {

                if (LAYER_NUMBER_PATTERN.matcher(line).matches()) {
                    handleNewLayer(line);
                    continue;
                }

                if (TRAVEL_MOVE_TYPE_PATTERN.matcher(line).matches()) {
                    handleMoveTypeChange(line);
                    continue;
                }

                if (TRAVEL_MOVE_PATTERN.matcher(line).matches()) {
                    handleTravelMove(line);
                    continue;
                }

                if (LINEAR_MOVE_PATTERN.matcher(line).matches()) {
                    handlePrintMove(line);
                    continue;
                }
            }

            // Append last layer
            finalizeLayer(gCodeLayer);

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

    private void finalizeLayer(GCodeLayer layer){
        if (layer != null){
            layer.finalizeSegment();
            layer.finalizeLayer();
            layers.add(layer);
            yield(layer);
        }
    }

    private void handleNewLayer(String line){
        finalizeLayer(gCodeLayer);

        int layerNumber = Integer.parseInt(line.substring(7));
        gCodeContext.setLayer(layerNumber);
        gCodeLayer = new GCodeLayer(layerNumber);
    }

    private void handleMoveTypeChange(String line){
        GCodeMoveType moveType = GCodeMoveType.getValueOf(line.substring(6));
        if (gCodeContext.setMoveType(moveType)) {
            gCodeLayer.finalizeSegment();
        }
    }

    private void handleTravelMove(String line){
        if (gCodeContext.setTravelMove(true)) {
            gCodeLayer.finalizeSegment();
        }

        gCodeContext.setX(extractGCodeParam(line, "X"));
        gCodeContext.setY(extractGCodeParam(line, "Y"));
        if (gCodeContext.setZ(extractGCodeParam(line, "Z"))){
            // Move along z is not displayed
            gCodeLayer.finalizeSegment();
        }

        try {
            gCodeLayer.processCmd(GCodeMoveType.TRAVEL,
                    gCodeContext.getX(), gCodeContext.getY(), gCodeContext.getZ());
        } catch (NumberFormatException e) {
            System.out.println("Gcode line corrupt: " + line);
        }
    }

    private void handlePrintMove(String line){
        if (gCodeContext.setTravelMove(false)) {
            gCodeLayer.finalizeSegment();
        }

        gCodeContext.setX(extractGCodeParam(line, "X"));
        gCodeContext.setY(extractGCodeParam(line, "Y"));
        if (gCodeContext.setZ(extractGCodeParam(line, "Z"))){
            gCodeLayer.finalizeSegment();
        }

        try {
            gCodeLayer.processCmd(gCodeContext.getMoveType(),
                    gCodeContext.getX(), gCodeContext.getY(), gCodeContext.getZ());
        } catch (NumberFormatException e) {
            System.out.println("Gcode line corrupt: " + line);
        }

    }

}
