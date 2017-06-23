package com.ysoft.dctrl.editor.mesh;

import com.ysoft.dctrl.math.LineSegment;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.TriangleMesh;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedList;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Created by kuhn on 5/24/2017.
 */
public class GCodeMeshGenerator {

    private final GCodeMeshProperties gCodeMeshProperties;

    public GCodeMeshGenerator(GCodeMeshProperties gCodeMeshProperties) {
        this.gCodeMeshProperties = gCodeMeshProperties;
    }

    /**
     * Simple version with not connected segments.
     * @param moves
     * @return
     */
    public GCodeMeshData run(LinkedList<GCodeMove> moves){

        if (moves.size() < 1){
            return null;
        }

        float d = gCodeMeshProperties.getLineWidth(moves.getLast().getType()); // thickness
        float h = gCodeMeshProperties.getLineHeight(moves.getLast().getType()); // layer height


        float[] meshPoints = new float[3 * 8 * (moves.size()-1)];
        int[] meshFaces = new int[6 * 8 * (moves.size()-1)];

        int i = 0;
        for(GCodeMove move : moves){

            // First pseudo-move does not have start
            if (move.getStart() == null){
                continue;
            }

            LineSegment segment = new LineSegment(move.getStart(), move.getFinish());

            // 12 o'clock (perpendicular cut)
            int n = 0; // nth point of the cut
            int m = 24; // number of values per move [ 8 points x 3 coords]
            int o = m/2; // number of meshpoints values per point [cut has 4 points]
            int p = 0; // 0 - startPoint, 1 - endPoint

            p = 0;
            n = 0;
            meshPoints[m * i + p*o + 3 * n] = (float) segment.getStartPoint().getX();           //x
            meshPoints[m * i + p*o + 3 * n + 1] = (float) segment.getStartPoint().getY();       //y
            meshPoints[m * i + p*o + 3 * n + 2] = (float) segment.getStartPoint().getZ() + h;   //z

            n = 1;
            meshPoints[m * i + p*o + 3 * n] = (float) (segment.getStartPoint().getX() - d*0.5*cos(segment.getAlfa()+Math.PI/2));
            meshPoints[m * i + p*o + 3 * n + 1] = (float) (segment.getStartPoint().getY() - d*0.5*sin(segment.getAlfa()+Math.PI/2));
            meshPoints[m * i + p*o + 3 * n + 2] = (float) segment.getStartPoint().getZ();   //z

            n = 2;
            meshPoints[m * i + p*o + 3 * n] = (float) segment.getStartPoint().getX();
            meshPoints[m * i + p*o + 3 * n + 1] = (float) segment.getStartPoint().getY();
            meshPoints[m * i + p*o + 3 * n + 2] = (float) segment.getStartPoint().getZ() - h;

            n = 3;
            meshPoints[m * i + p*o + 3 * n] = (float) (segment.getStartPoint().getX() + d*0.5*cos(segment.getAlfa()+Math.PI/2));
            meshPoints[m * i + p*o + 3 * n + 1] = (float) (segment.getStartPoint().getY() + d*0.5*sin(segment.getAlfa()+Math.PI/2));
            meshPoints[m * i + p*o + 3 * n + 2] = (float) segment.getStartPoint().getZ();   //z


            p = 1;
            n = 0;
            meshPoints[m * i + p*o + 3 * n] = (float) segment.getEndPoint().getX();           //x
            meshPoints[m * i + p*o + 3 * n + 1] = (float) segment.getEndPoint().getY();       //y
            meshPoints[m * i + p*o + 3 * n + 2] = (float) segment.getEndPoint().getZ() + h;   //z

            // 3 o'clock
            n = 1;
            meshPoints[m * i + p*o + 3 * n] = (float) (segment.getEndPoint().getX() - d*0.5*cos(segment.getAlfa()+Math.PI/2));;
            meshPoints[m * i + p*o + 3 * n + 1] = (float) (segment.getEndPoint().getY() - d*0.5*sin(segment.getAlfa()+Math.PI/2));
            meshPoints[m * i + p*o + 3 * n + 2] = (float) segment.getEndPoint().getZ();

            // 6 o'clock
            n = 2;
            meshPoints[m * i + p*o + 3 * n] = (float) segment.getEndPoint().getX();
            meshPoints[m * i + p*o + 3 * n + 1] = (float) segment.getEndPoint().getY();
            meshPoints[m * i + p*o + 3 * n + 2] = (float) segment.getEndPoint().getZ() - h;

            // 9 o'clock
            n = 3;
            meshPoints[m * i + p*o + 3 * n] = (float) (segment.getEndPoint().getX() + d*0.5*cos(segment.getAlfa()+Math.PI/2));;
            meshPoints[m * i + p*o + 3 * n + 1] = (float) (segment.getEndPoint().getY() + d*0.5*sin(segment.getAlfa()+Math.PI/2));
            meshPoints[m * i + p*o + 3 * n + 2] = (float) segment.getEndPoint().getZ();   //z


            // Generation of faces
            n = 0;
            meshFaces[48 * i + 6 * n + 0] = 8 * i + 0;    // odd values are texture coords.
            meshFaces[48 * i + 6 * n + 2] = 8 * i + 5;
            meshFaces[48 * i + 6 * n + 4] = 8 * i + 4;

            n = 1;
            meshFaces[48 * i + 6 * n + 0] = 8 * i + 0;
            meshFaces[48 * i + 6 * n + 2] = 8 * i + 1;
            meshFaces[48 * i + 6 * n + 4] = 8 * i + 5;

            n = 2;
            meshFaces[48 * i + 6 * n + 0] = 8 * i + 1;
            meshFaces[48 * i + 6 * n + 2] = 8 * i + 6;
            meshFaces[48 * i + 6 * n + 4] = 8 * i + 5;

            n = 3;
            meshFaces[48 * i + 6 * n + 0] = 8 * i + 1;
            meshFaces[48 * i + 6 * n + 2] = 8 * i + 2;
            meshFaces[48 * i + 6 * n + 4] = 8 * i + 6;

            n = 4;
            meshFaces[48 * i + 6 * n + 0] = 8 * i + 2;
            meshFaces[48 * i + 6 * n + 2] = 8 * i + 7;
            meshFaces[48 * i + 6 * n + 4] = 8 * i + 6;

            n = 5;
            meshFaces[48 * i + 6 * n + 0] = 8 * i + 2;
            meshFaces[48 * i + 6 * n + 2] = 8 * i + 3;
            meshFaces[48 * i + 6 * n + 4] = 8 * i + 7;

            n = 6;
            meshFaces[48 * i + 6 * n + 0] = 8 * i + 3;
            meshFaces[48 * i + 6 * n + 2] = 8 * i + 4;
            meshFaces[48 * i + 6 * n + 4] = 8 * i + 7;

            n = 7;
            meshFaces[48 * i + 6 * n + 0] = 8 * i + 3;
            meshFaces[48 * i + 6 * n + 2] = 8 * i + 0;
            meshFaces[48 * i + 6 * n + 4] = 8 * i + 4;

            i++;
        }

        return new GCodeMeshData(meshPoints, meshFaces, moves.getLast().getType());
    }
}
