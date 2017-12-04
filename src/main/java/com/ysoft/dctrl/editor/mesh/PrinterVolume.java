package com.ysoft.dctrl.editor.mesh;

import java.util.function.Consumer;

import com.ysoft.dctrl.math.BoundingBox;
import com.ysoft.dctrl.utils.ColorUtils;

import javafx.geometry.Point3D;
import javafx.scene.Node;
import javafx.scene.image.Image;

/**
 * Created by pilar on 25.8.2017.
 */
public class PrinterVolume extends BoundingBox {
    private static Image DEFAULT_COLOR = ColorUtils.getColorImage("#cacbcc");
    private static Image INVALID_COLOR = ColorUtils.getColorImage("#ff0000");

    private PrinterVolumeOffset volumeOffset;

    @Override
    protected void init() {
        volumeOffset = new PrinterVolumeOffset();
        super.init();
        super.setColor(DEFAULT_COLOR);
        setOnChange((bb) -> {
            volumeOffset.setSize(super.getSize());
        });
    }

    @Override
    public void reset() {
        super.reset();
        volumeOffset.setOffset(0);
    }



    @Override
    public boolean contains(Point3D point) {
        double offset = volumeOffset.getOffset();
        return compareWithDeviation(min.getX() + offset, point.getX()) && compareWithDeviation(point.getX(), max.getX() - offset) &&
                compareWithDeviation(min.getY() + offset, point.getY()) && compareWithDeviation(point.getY(), max.getY() - offset) &&
                compareWithDeviation(min.getZ(), point.getZ()) && compareWithDeviation(point.getZ(), max.getZ());
    }


    @Override
    public Point3D getSize() {
        Point3D size = super.getSize();
        return size.subtract(volumeOffset.getOffset()*2, volumeOffset.getOffset()*2, 0);
    }

    public void setDefaultColor() {
        super.setColor(DEFAULT_COLOR);
    }

    public void setInvalidColor() {
        super.setColor(INVALID_COLOR);
    }

    public void setPlatformOffset(double platformOffset) {
        volumeOffset.setOffset(platformOffset);
    }

    @Override
    public void setColor(Image color) {
        throw new UnsupportedOperationException();
    }

    public Node getOffsetNode() {
        return volumeOffset.getNode();
    }
}
