package com.ysoft.dctrl.editor.mesh;

import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import org.springframework.stereotype.Component;


/**
 * Created by kuhn on 6/21/2017.
 */
@Component
public class GCodeMeshProperties {

    private static final Material DEFAULT_MATERIAL = new PhongMaterial(Color.GOLD);
    private static final Material OUTER_WALL_MATERIAL = new PhongMaterial(Color.GREEN);
    private static final Material FILL_MATERIAL = new PhongMaterial(Color.WHITESMOKE);
    private static final Material TRAVEL_MATERIAL = new PhongMaterial(Color.RED);
    private static final Material SUPPORT_MATERIAL = new PhongMaterial(Color.DIMGRAY);
    private static final Material SKIRT_MATERIAL = new PhongMaterial(Color.BLUE);

    private static float nozzleDiameter = 0.4f;
    private static float layerHeight = 0.20f;


    public GCodeMeshProperties() {}

    public void setNozzleDiameter(float d){
        this.nozzleDiameter = d;
    }
    public void setLayerHeight(float h){
        this.layerHeight = h;
    }

    public float getNozzleDiameter() {
        return nozzleDiameter;
    }

    public float getLayerHeight() {
        return layerHeight;
    }

    public Material getMaterial(GCodeMoveType type){
        Material material = DEFAULT_MATERIAL;
        switch(type){
            case FILL: material = FILL_MATERIAL;
                break;
            case TRAVEL: material = TRAVEL_MATERIAL;
                break;
            case SUPPORT: material = SUPPORT_MATERIAL;
                break;
            case SKIRT: material = SKIRT_MATERIAL;
                break;
            case WALL_OUTER: material = OUTER_WALL_MATERIAL;
                break;
        }
        return material;
    }

    public float getLineWidth(GCodeMoveType type){
        float w = this.getNozzleDiameter();
        switch(type){
            case TRAVEL:
                w = 0.05f;
                break;
        }
        return w;
    }

    public float getLineHeight(GCodeMoveType type){
        float h = this.getLayerHeight();
        switch(type){
            case TRAVEL:
                h = 0.01f;
        }
        return h;
    }
}
