package com.ysoft.dctrl.editor.mesh;

import com.ysoft.dctrl.math.BoundingBox;
import com.ysoft.dctrl.utils.ColorUtils;

import javafx.scene.image.Image;

/**
 * Created by pilar on 25.8.2017.
 */
public class PrinterVolume extends BoundingBox {
    private static Image DEFAULT_COLOR = ColorUtils.getColorImage("#cacbcc");
    private static Image INVALID_COLOR = ColorUtils.getColorImage("#ff0000");


    @Override
    protected void init() {
        super.init();
        super.setColor(DEFAULT_COLOR);
    }

    public void setDefaultColor() {
        super.setColor(DEFAULT_COLOR);
    }

    public void setInvalidColor() {
        super.setColor(INVALID_COLOR);
    }

    @Override
    public void setColor(Image color) {
        throw new UnsupportedOperationException();
    }
}
