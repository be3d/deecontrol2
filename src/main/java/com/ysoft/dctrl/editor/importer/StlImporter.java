package com.ysoft.dctrl.editor.importer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ysoft.dctrl.utils.MemoryManager;
import com.ysoft.dctrl.utils.exceptions.RunningOutOfMemoryException;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

/**
 * Created by pilar on 28.3.2017.
 */
public class StlImporter extends AbstractModelImporter<TriangleMesh> {
    private static final String ASCII_START = "solid ";

    private static final String FLOAT_FORMAT = "([+-]?[0-9]+\\.?[0-9]*([eE][+-]?[0-9]+)?)";
    private static final String VERTEX_FORMAT = FLOAT_FORMAT + "\\s+" + FLOAT_FORMAT + "\\s+" + FLOAT_FORMAT;
    private static final Matcher FACET_MATCHER = Pattern.compile("facet([\\s\\S]*?)endface", Pattern.MULTILINE).matcher("");
    private static final Matcher NORMAL_MATCHER = Pattern.compile("normal\\s+" + VERTEX_FORMAT, Pattern.MULTILINE).matcher("");
    private static final Matcher VERTEX_MATCHER = Pattern.compile("vertex\\s+" + VERTEX_FORMAT, Pattern.MULTILINE).matcher("");

    private TriangleMesh mesh;
    private Map<String, Integer> vertexMap;
    private Integer nextVertexIndex;
    private Integer nextFaceIndex;
    private Integer nextNormalIndex;

    public StlImporter() {
        super();
        reset();
    }

    @Override
    public void reset() {
        mesh = new TriangleMesh();
        mesh.setVertexFormat(VertexFormat.POINT_NORMAL_TEXCOORD);
        vertexMap = new HashMap<>();
        nextVertexIndex = 0;
        nextFaceIndex = 0;
        nextNormalIndex = 0;
    }

    @Override
    public TriangleMesh load(InputStream stream) throws IOException, IllegalArgumentException, RunningOutOfMemoryException, OutOfMemoryError {
        try (BufferedInputStream bis = new BufferedInputStream(stream)){
            byte[] data = new byte[1024];
            bis.mark(100);
            int res = bis.read(data, 0, data.length);
            bis.reset();
            TriangleMesh mesh;
            if(isAscii(data)) {
                mesh = loadAscii(bis);
            } else {
                mesh = loadBinary(bis);
            }
            mesh.getTexCoords().addAll(0.0f, 0.0f);
            return mesh;
        }
    }

    private boolean isAscii(byte[] data) {
        String head = new String(data, StandardCharsets.UTF_8);
        return isAsciiHead(head);
    }

    private final static Matcher whitespaceMatcher = Pattern.compile("\\s").matcher("");
    private boolean isAsciiHead(String s) {
        if(!s.startsWith(ASCII_START)) { return false; }
        s = s.replace(ASCII_START, "");
        whitespaceMatcher.reset(s);
        if(!whitespaceMatcher.find()) { return false; }
        int i = whitespaceMatcher.start();
        return StandardCharsets.UTF_8.newEncoder().canEncode(s.substring(0, i)) &&
                StandardCharsets.US_ASCII.newEncoder().canEncode(s.substring(i+1));
    }

    private TriangleMesh loadBinary(BufferedInputStream stream) throws IllegalArgumentException, IOException, RunningOutOfMemoryException, OutOfMemoryError {
        byte[] data = new byte[80];
        if(stream.read(data) != 80) { throw new IllegalArgumentException("Not a valid stl file"); }
        addBytesRead(80);
        data = new byte[4];
        if(stream.read(data) != 4) { throw new IllegalArgumentException("Not a valid stl file"); }
        addBytesRead(4);
        int facesCount = ByteBuffer.wrap(data, 0, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
        int facesRead = 0;
        data = new byte[50];
        while (stream.read(data) == 50) {
            MemoryManager.checkMemory();
            addBytesRead(50);
            loadFace(data);
            facesRead++;
        }

        if(facesCount != facesRead) { throw new IllegalArgumentException("Not a valid stl file"); }

        return mesh;
    }

    private TriangleMesh loadAscii(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[8192];
        int read = 0;
        while((read = reader.read(buffer)) != -1) {
            addBytesRead(read);
            builder.append(buffer, 0, read);
            FACET_MATCHER.reset(builder);
            while(FACET_MATCHER.find()) {
                loadFace(FACET_MATCHER.group());
                builder.delete(0, FACET_MATCHER.end());
                FACET_MATCHER.reset(builder);
            }
        }
        return mesh;
    }

    private void loadFace(String faceData) {
        //float[] norm = new float[3];
        float[] vertices = new float[9];
        //NORMAL_MATCHER.reset(faceData);
        //if(NORMAL_MATCHER.find()) {
        //    for(int i = 0; i < 3; i++) { norm[i] = Float.parseFloat(NORMAL_MATCHER.group(2*i + 1)); }
        //}

        VERTEX_MATCHER.reset(faceData);
        for(int i = 0; i < 3; i++) {
            if(VERTEX_MATCHER.find()) {
                for (int j = 0; j < 3; j++) {
                    vertices[i * 3 + j] = Float.parseFloat(VERTEX_MATCHER.group(2*j + 1));
                }
            }
        }
        float[] normal = computeNormalVector(vertices);
        int normalIndex = addNormal(normal[0], normal[1], normal[2]);
        int v1 = addVertex(vertices[0], vertices[1], vertices[2]);
        int v2 = addVertex(vertices[3], vertices[4], vertices[5]);
        int v3 = addVertex(vertices[6], vertices[7], vertices[8]);
        addFace(v1, v2, v3, normalIndex);
    }

    private void loadFace(byte[] faceData) {
        float[] vertices = new float[] {
                getFloat(faceData, 12, 4), getFloat(faceData, 16, 4), getFloat(faceData, 20, 4),
                getFloat(faceData, 24, 4), getFloat(faceData, 28, 4), getFloat(faceData, 32, 4),
                getFloat(faceData, 36, 4), getFloat(faceData, 40, 4), getFloat(faceData, 44, 4)
        };
        float[] normal = computeNormalVector(vertices);
        int normalIndex = addNormal(normal[0], normal[1], normal[2]);
        int v1 = addVertex(vertices[0], vertices[1], vertices[2]);
        int v2 = addVertex(vertices[3], vertices[4], vertices[5]);
        int v3 = addVertex(vertices[6], vertices[7], vertices[8]);
        addFace(v1, v2, v3, normalIndex);
    }

    private int addVertex(float x, float y, float z) {
        String vertexHash = x + "_" + y + "_" + z;
        if(vertexMap.containsKey(vertexHash)) {
            return vertexMap.get(vertexHash);
        }

        mesh.getPoints().addAll(x, y, z);
        vertexMap.put(vertexHash, nextVertexIndex);
        return nextVertexIndex++;
    }

    private float[] computeNormalVector(float[] vertices) {
        if(vertices.length != 9) { throw new IllegalArgumentException("Not triangle vertices"); }
        return computeNormalVector(
                vertices[0], vertices[1], vertices[2],
                vertices[3], vertices[4], vertices[5],
                vertices[6], vertices[7], vertices[8]
        );
    }

    private float[] computeNormalVector(float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2) {
        float[] normal = new float[3];
        float[] u = new float[]{x1 - x0, y1 - y0, z1 - z0};
        float[] v = new float[]{x2 - x0, y2 - y0, z2 - z0};
        normal[0] = (u[1]*v[2]) - (u[2]*v[1]);
        normal[1] = (u[2]*v[0]) - (u[0]*v[2]);
        normal[2] = (u[0]*v[1]) - (u[1]*v[0]);
        return normal;
    }

    private int addNormal(float[] normal) {
        if(normal.length != 3) { throw new IllegalArgumentException("Not a normal vector"); }
        return addNormal(normal[0], normal[1], normal[2]);
    }

    private int addNormal(float x, float y, float z) {
        mesh.getNormals().addAll(x, y, z);
        return mesh.getNormals().size()/3 - 1;
    }

    private void addFace(int v1, int v2, int v3, int normalIndex) {
        mesh.getFaces().addAll(v1, normalIndex, 0, v2, normalIndex, 0, v3, normalIndex, 0);
    }

    private float getFloat(byte[] data, int offset, int length) {
        return ByteBuffer.wrap(data, offset, length).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
}
