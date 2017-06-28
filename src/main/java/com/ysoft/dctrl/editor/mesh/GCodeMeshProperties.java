package com.ysoft.dctrl.editor.mesh;

import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;


/**
 * Created by kuhn on 6/21/2017.
 */
public class GCodeMeshProperties {

    private final Material defaultMaterial = new PhongMaterial(Color.GOLD);
    private float nozzleDiameter = 0.4f;
    private float layerHeight = 0.20f;


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
        Material material = defaultMaterial;

        switch(type){
            case FILL:{
                material = new PhongMaterial(Color.WHITESMOKE);
                break;
            }
            case TRAVEL:{
                material = new PhongMaterial(Color.RED);
                break;
            }
            case SUPPORT:{
                material = new PhongMaterial(Color.DIMGRAY);
                break;
            }
            case SKIRT: {
                material = new PhongMaterial(Color.BLUE);
                break;
            }
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
