package com.ysoft.dctrl.editor.importer;

import com.ysoft.dctrl.editor.mesh.GCodeLayer;
import com.ysoft.dctrl.editor.mesh.GCodeMoveType;
import com.ysoft.dctrl.math.LineSegment;
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
public class GCodeImporter extends AbstractModelImporter {

    private final Pattern TRAVEL_MOVE_PATTERN = Pattern.compile("G0\\s.+");
    private final Pattern LINEAR_MOVE_PATTERN = Pattern.compile("G1\\s.+");
    private final Pattern TRAVEL_MOVE_TYPE_PATTERN = Pattern.compile(";TYPE:.+");
    private final Pattern LAYER_NUMBER_PATTERN = Pattern.compile(";LAYER:[0-9]+");
    private final Pattern COMMENTS1 = Pattern.compile("\\(.*\\)"); // Comment between ()
    private final Pattern COMMENTS2 = Pattern.compile("\\;.*"); // comment after ;

    private GCodeLayer gCodeLayer = new GCodeLayer(-1); // in CURA it can start with negative layers !! change this
    private LinkedList<GCodeLayer>  layers = new LinkedList<>();

    private static class GCodeContext {

        public GCodeMoveType moveType = GCodeMoveType.NONE;
        private double x,y,z;
        private int layer;


        public void setX(Double x) { if (x != null) this.x = x; }
        public void setY(Double y) { if (y != null) this.y = y; }
        public void setZ(Double z) { if (z!= null) this.z = z; }
        public void setLayer(int layer) { this.layer = layer; }

        public double getX() { return x; }
        public double getY() { return y; }
        public double getZ() { return z; }
        public int getLayer() { return layer; }


        public void setMoveType(GCodeMoveType moveType) {this.moveType = moveType;}
        public GCodeMoveType getMoveType() {return moveType;}
    }

    private GCodeContext gCodeContext = new GCodeContext();

    @Override
    TriangleMesh load(InputStream stream) throws IOException, IllegalArgumentException {

         try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))){
             String line;
             while ((line = br.readLine()) != null) {
                 System.out.println(line);

                 if(LAYER_NUMBER_PATTERN.matcher(line).matches()){

                    if (gCodeLayer != null) //emit message with layer to GCodePresenter
                        layers.add(gCodeLayer);

                     int layerNumber = Integer.parseInt(line.substring(7));
                     gCodeContext.setLayer(layerNumber);
                     gCodeLayer = new GCodeLayer(layerNumber);
                 }

                 if(TRAVEL_MOVE_TYPE_PATTERN.matcher(line).matches()){
                     GCodeMoveType moveType = GCodeMoveType.getValueOf(line.substring(6));
                     gCodeContext.setMoveType(moveType);
                 }

                 if(TRAVEL_MOVE_PATTERN.matcher(line). matches()){
                     gCodeContext.setX(extractGCodeParam(line,"X"));
                     gCodeContext.setY(extractGCodeParam(line,"Y"));
                     gCodeContext.setZ(extractGCodeParam(line,"Z"));

                     try{
                         gCodeLayer.processCmd(GCodeMoveType.TRAVEL,
                                 gCodeContext.getX(), gCodeContext.getY(), gCodeContext.getZ());
                     }catch(NumberFormatException e){
                         System.out.println("Gcode line corrupt: " + line);
                     }
                }

                if(LINEAR_MOVE_PATTERN.matcher(line).matches()){
                    gCodeContext.setX(extractGCodeParam(line,"X"));
                    gCodeContext.setY(extractGCodeParam(line,"Y"));
                    gCodeContext.setZ(extractGCodeParam(line,"Z"));

                    try{
                        gCodeLayer.processCmd(gCodeContext.getMoveType(),
                                gCodeContext.getX(), gCodeContext.getY(), gCodeContext.getZ());
                    }catch(NumberFormatException e){
                        System.out.println("Gcode line corrupt: " + line);
                    }
                }
             }

             // Append last layer
             if (gCodeLayer != null )
                layers.add(gCodeLayer);
        }

        GCodeLayer layer = layers.get(layers.size()-1);
        TriangleMesh mesh = new TriangleMesh();
        float d = 0.4f; // thickness

        LineSegment[] segments = new LineSegment[layer.getMoves().size()-1];
        float[] meshPoints = new float[3*4*layer.getMoves().size()];
        int[] meshFaces = new int[8*6*(layer.getMoves().size()-1)];

//        TEST
//        Point3D pointA = new Point3D(0,0,0);
//        Point3D pointB = new Point3D(10,0,0);
//        Point3D pointC = new Point3D(15,5,0);
//        Point3D pointD = new Point3D(20,-5,0);
//        Point3D pointE = new Point3D(20,-8,0);
//        Point3D pointF = new Point3D(25,-8,0);
//        Point3D[] gCodePoints = new Point3D[6];
//        gCodePoints[0] = pointA;
//        gCodePoints[1] = pointB;
//        gCodePoints[2] = pointC;
//        gCodePoints[3] = pointD;
//        gCodePoints[4] = pointE;
//        gCodePoints[5] = pointF;


        // Do the initial element faces

        // ...

        for (int i = 1; i<layer.getMoves().size()-2; i++){

//            // omit travel moves for now
//            if (layer.getMoves().get(i).getType().equals(GCodeMoveType.TRAVEL))
//                continue;

//            segments[i] = new LineSegment(gCodePoints[i], gCodePoints[i+1], gCodePoints[i+2]);
            segments[i] = new LineSegment(layer.getMoves().get(i).getPoint(), layer.getMoves().get(i+1).getPoint(), layer.getMoves().get(i+2).getPoint() );

            //x
            double xx = segments[i].getEndPoint().getX() + d*cos(segments[i].getAlfa() + segments[i].getBeta()/2);
            double xy = segments[i].getEndPoint().getY() + d*sin(segments[i].getAlfa() + segments[i].getBeta()/2);

            //y
            double yx = segments[i].getEndPoint().getX() - d*cos(segments[i].getAlfa() + segments[i].getBeta()/2);
            double yy = segments[i].getEndPoint().getY() - d*sin(segments[i].getAlfa() + segments[i].getBeta()/2);


            // 12 o'clock (perpendicular cut)
            int n = 0; // nth point of the cut
            meshPoints[12*i+3*n] = (float)segments[i].getEndPoint().getX();         //x
            meshPoints[12*i+3*n+1] = (float)segments[i].getEndPoint().getY();       //y
            meshPoints[12*i+3*n+2] = (float)segments[i].getEndPoint().getZ() + d;   //z

            // 3 o'clock
            n = 1;
            meshPoints[12*i+3*n] = (float)yx;
            meshPoints[12*i+3*n+1] = (float)yy;
            meshPoints[12*i+3*n+2] = (float)segments[i].getEndPoint().getZ();

            // 6 o'clock
            n = 2;
            meshPoints[12*i+3*n] = (float)segments[i].getEndPoint().getX();
            meshPoints[12*i+3*n+1] = (float)segments[i].getEndPoint().getY();
            meshPoints[12*i+3*n+2] = (float)segments[i].getEndPoint().getZ() - d;

            // 9 o'clock
            n = 3;
            meshPoints[12*i+3*n] = (float)xx;
            meshPoints[12*i+3*n+1] = (float)xy;
            meshPoints[12*i+3*n+2] = (float)segments[i].getEndPoint().getZ();   //z
        }


        // TODO refactor to MeshGenerator
        for (int i = 1; i<layer.getMoves().size()-3; i++) {

            int n = 0; // nth polygon
            int m = 8; // faces per element

            boolean flipNormals = false;
            if (flipNormals){

                meshFaces[48*i+6*n+0] = 4*i + 0;    // odd values are texture coords.
                meshFaces[48*i+6*n+2] = 4*i + 4;
                meshFaces[48*i+6*n+4] = 4*i + 5;


                n = 1;
                meshFaces[48*i+6*n+0] = 4*i + 0;
                meshFaces[48*i+6*n+2] = 4*i + 5;
                meshFaces[48*i+6*n+4] = 4*i + 1;

                n = 2;
                meshFaces[48*i+6*n+0] = 4*i + 1;
                meshFaces[48*i+6*n+2] = 4*i + 5;
                meshFaces[48*i+6*n+4] = 4*i + 6;

                n = 3;
                meshFaces[48*i+6*n+0] = 4*i + 1;
                meshFaces[48*i+6*n+2] = 4*i + 6;
                meshFaces[48*i+6*n+4] = 4*i + 2;

                n = 4;
                meshFaces[48*i+6*n+0] = 4*i + 2;
                meshFaces[48*i+6*n+2] = 4*i + 6;
                meshFaces[48*i+6*n+4] = 4*i + 7;

                n = 5;
                meshFaces[48*i+6*n+0] = 4*i + 2;
                meshFaces[48*i+6*n+2] = 4*i + 7;
                meshFaces[48*i+6*n+4] = 4*i + 3;

                n = 6;
                meshFaces[48*i+6*n+0] = 4*i + 3;
                meshFaces[48*i+6*n+2] = 4*i + 7;
                meshFaces[48*i+6*n+4] = 4*i + 4;

                n = 7;
                meshFaces[48*i+6*n+0] = 4*i + 3;
                meshFaces[48*i+6*n+2] = 4*i + 4;
                meshFaces[48*i+6*n+4] = 4*i + 0;
            }
            else {
                meshFaces[48*i+6*n+0] = 4*i + 0;    // odd values are texture coords.
                meshFaces[48*i+6*n+2] = 4*i + 5;
                meshFaces[48*i+6*n+4] = 4*i + 4;

                n = 1;
                meshFaces[48*i+6*n+0] = 4*i + 0;
                meshFaces[48*i+6*n+2] = 4*i + 1;
                meshFaces[48*i+6*n+4] = 4*i + 5;

                n = 2;
                meshFaces[48*i+6*n+0] = 4*i + 1;
                meshFaces[48*i+6*n+2] = 4*i + 6;
                meshFaces[48*i+6*n+4] = 4*i + 5;

                n = 3;
                meshFaces[48*i+6*n+0] = 4*i + 1;
                meshFaces[48*i+6*n+2] = 4*i + 2;
                meshFaces[48*i+6*n+4] = 4*i + 6;

                n = 4;
                meshFaces[48*i+6*n+0] = 4*i + 2;
                meshFaces[48*i+6*n+2] = 4*i + 7;
                meshFaces[48*i+6*n+4] = 4*i + 6;

                n = 5;
                meshFaces[48*i+6*n+0] = 4*i + 2;
                meshFaces[48*i+6*n+2] = 4*i + 3;
                meshFaces[48*i+6*n+4] = 4*i + 7;

                n = 6;
                meshFaces[48*i+6*n+0] = 4*i + 3;
                meshFaces[48*i+6*n+2] = 4*i + 4;
                meshFaces[48*i+6*n+4] = 4*i + 7;

                n = 7;
                meshFaces[48*i+6*n+0] = 4*i + 3;
                meshFaces[48*i+6*n+2] = 4*i + 0;
                meshFaces[48*i+6*n+4] = 4*i + 4;
            }
        }

        float[] texCoords = {
                0, 0,
                0, 0,
                0, 0,
                0, 0
        };

        mesh.getPoints().addAll(meshPoints);
        mesh.getTexCoords().addAll(texCoords);
        mesh.getFaces().addAll(meshFaces);
        return mesh;
    }

    private Double extractGCodeParam(String line, String paramTag) throws NumberFormatException {
        int pos = line.indexOf(paramTag);
        if (pos > -1){
            int endPos = line.indexOf(" ",pos);
            if (endPos > -1)
                return Double.parseDouble(line.substring(pos+1, endPos));
            else
                return Double.parseDouble(line.substring(pos+1));
        }
        return null;
    }

}
