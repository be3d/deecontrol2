package com.ysoft.dctrl.math;

import javafx.geometry.Point3D;

import static java.lang.Math.*;

/**
 * Created by kuhn on 5/23/2017.
 */
public class LineSegment {
    private Point3D startPoint;
    private Point3D endPoint;
    private double length;
    private double alfa; // angle between line segment and positive x axis
    private double beta; // angle between this line segment and the next line segment


    public LineSegment(){

    }

    public LineSegment(Point3D a,Point3D b) {
        startPoint = a;
        endPoint = b;
        init();
    }

    public LineSegment(Point3D a,Point3D b, Point3D c) {
        this(a,b);
        double sideA = sqrt(pow(b.getX()-a.getX(),2) + pow(b.getY()-a.getY(),2));
        double sideB = sqrt(pow(c.getX()-b.getX(),2) + pow(c.getY()-b.getY(),2));
        double sideC = sqrt(pow(c.getX()-a.getX(),2) + pow(c.getY()-a.getY(),2));
        beta = acos( (pow(sideA,2)+pow(sideB,2)-pow(sideC,2)) / (2*sideA*sideB) );
    }

    private void init(){
        length = startPoint.distance(endPoint);
        alfa = atan2(endPoint.getY()-startPoint.getY(), endPoint.getX()-startPoint.getX());
    }

    public Point3D getStartPoint(){
        return startPoint;
    }

    public Point3D getEndPoint() {
        return endPoint;
    }

    public double getAlfa() {
        return alfa;
    }

    public double getLength() { return length;  }

    public double getBeta() {
        return beta;
    }

}
