package com.ysoft.dctrl.utils;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Created by pilar on 21.8.2017.
 */
public class ColorUtils {
    private static final Map<String, Image> COLOR_IMAGE_MAP = new HashMap<>();
    private static final Map<String, Image> TEXTURE_IMAGE_MAP = new HashMap<>();
    private ColorUtils() {}

    public static Image getColorImage(String color) {
        if(!COLOR_IMAGE_MAP.containsKey(color)) {
            WritableImage image = new WritableImage(1,1);
            image.getPixelWriter().setColor(0,0, Color.web(color));
            COLOR_IMAGE_MAP.put(color, image);
        }
        return COLOR_IMAGE_MAP.get(color);
    }

    public static Image getColorImage(String color, double opacity) {
        String k = color + "*" + opacity;
        if(!COLOR_IMAGE_MAP.containsKey(k)) {
            WritableImage image = new WritableImage(1,1);
            image.getPixelWriter().setColor(0,0, Color.web(color, opacity));
            COLOR_IMAGE_MAP.put(k, image);
        }
        return COLOR_IMAGE_MAP.get(k);
    }

    public static Image getTexture(String texture) {
        return new Image("/img/texture/" + texture + ".png");
    }
}
