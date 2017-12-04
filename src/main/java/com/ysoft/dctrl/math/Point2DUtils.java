package com.ysoft.dctrl.math;

import javafx.geometry.Point2D;

public class Point2DUtils {
    public static Point2D copy(Point2D p) {
        return new Point2D(p.getX(), p.getY());
    }

    public static Point2D setX(Point2D p, double x) {
        return new Point2D(x, p.getY());
    }

    public static Point2D setY(Point2D p, double y) {
        return new Point2D(p.getX(), y);
    }

    public static Point2D divideElements(Point2D a, Point2D b) {
        return new Point2D(a.getX()/b.getX(), a.getY()/b.getY());
    }
}
