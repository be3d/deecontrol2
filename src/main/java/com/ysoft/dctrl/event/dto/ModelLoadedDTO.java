package com.ysoft.dctrl.event.dto;

import javafx.scene.shape.TriangleMesh;

/**
 * Created by pilar on 4.9.2017.
 */
public class ModelLoadedDTO {
    private TriangleMesh mesh;
    private String name;

    public ModelLoadedDTO(TriangleMesh mesh, String name) {
        this.mesh = mesh;
        this.name = name;
    }

    public TriangleMesh getMesh() {
        return mesh;
    }

    public String getName() {
        return name;
    }
}
