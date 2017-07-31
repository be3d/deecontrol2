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

import javafx.scene.shape.TriangleMesh;
import javafx.scene.shape.VertexFormat;

/**
 * Created by pilar on 28.3.2017.
 */
public class StlImporter extends AbstractModelImporter {
    private static final String ASCII_START = "solid ";

    private static final String FLOAT_FORMAT = "([+-]?[0-9]+\\.?[0-9]*([eE][+-]?[0-9]+)?)";
    private static final String VERTEX_FORMAT = FLOAT_FORMAT + "\\s+" + FLOAT_FORMAT + "\\s+" + FLOAT_FORMAT;
    private static final Matcher FACET_MATCHER = Pattern.compile("facet([\\s\\S]*?)endface", Pattern.MULTILINE).matcher("");
    private static final Matcher NORMAL_MATCHER = Pattern.compile("normal\\s+" + VERTEX_FORMAT, Pattern.MULTILINE).matcher("");
    private static final Matcher VERTEX_MATCHER = Pattern.compile("vertex\\s+" + VERTEX_FORMAT, Pattern.MULTILINE).matcher("");

    private TriangleMesh mesh;
    private Map<String, Integer> vertexMap;
    private Integer nextVertexIndex;

    public StlImporter() {
        super();
        reset();
    }

    private void reset() {
        mesh = new TriangleMesh();
        mesh.setVertexFormat(VertexFormat.POINT_NORMAL_TEXCOORD);
        vertexMap = new HashMap<>();
        nextVertexIndex = 0;
    }

    @Override
    public TriangleMesh load(InputStream stream) throws IOException, IllegalArgumentException {
        try (BufferedInputStream bis = new BufferedInputStream(stream)){
            byte[] data = new byte[100];
            bis.mark(100);
            bis.read(data, 0, data.length);
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
        return head.startsWith(ASCII_START) && isAsciiString(head);
    }

    private boolean isAsciiString(String s) {
        return StandardCharsets.US_ASCII.newEncoder().canEncode(s);
    }

    private TriangleMesh loadBinary(BufferedInputStream stream) throws IllegalArgumentException, IOException {
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
        float[] norm = new float[3];
        float[] vertexes = new float[9];
        NORMAL_MATCHER.reset(faceData);
        if(NORMAL_MATCHER.find()) {
            for(int i = 0; i < 3; i++) { norm[i] = Float.parseFloat(NORMAL_MATCHER.group(2*i + 1)); }
        }

        VERTEX_MATCHER.reset(faceData);
        for(int i = 0; i < 3; i++) {
            if(VERTEX_MATCHER.find()) {
                for (int j = 0; j < 3; j++) {
                    vertexes[i * 3 + j] = Float.parseFloat(VERTEX_MATCHER.group(2*j + 1));
                }
            }
        }

        int normalIndex = addNormal(norm[0], norm[1], norm[2]);
        int v1 = addVertex(vertexes[0], vertexes[1], vertexes[2]);
        int v2 = addVertex(vertexes[3], vertexes[4], vertexes[5]);
        int v3 = addVertex(vertexes[6], vertexes[7], vertexes[8]);
        addFace(v1, v2, v3, normalIndex);
    }

    private void loadFace(byte[] faceData) {
        int normalIndex = addNormal(getFloat(faceData, 0, 4), getFloat(faceData, 4, 4), getFloat(faceData, 8, 4));
        int v1 = addVertex(getFloat(faceData, 12, 4), getFloat(faceData, 16, 4), getFloat(faceData, 20, 4));
        int v2 = addVertex(getFloat(faceData, 24, 4), getFloat(faceData, 28, 4), getFloat(faceData, 32, 4));
        int v3 = addVertex(getFloat(faceData, 36, 4), getFloat(faceData, 40, 4), getFloat(faceData, 44, 4));
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
