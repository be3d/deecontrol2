package com.ysoft.dctrl.ui.controller.controlMenu;

import javafx.beans.property.DoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Created by kuhn on 9/21/2017.
 */
public class RangeSliderVertical extends StackPane {

    private final Slider topSlider;
    private final Slider bottomSlider;
    private final Pane track;
    private final Pane trackBackground;

    private double minRange = 1.0;
    private double min = 1.0;
    private double max = 100.0;

    public RangeSliderVertical() {
        bottomSlider = createSlider();
        topSlider = createSlider();
        track = createPane("track");
        trackBackground = createPane("track-background");

        bottomSlider.setMin(min);
        bottomSlider.setMax(max);
        bottomSlider.setValue(min);
        topSlider.setMin(min);
        topSlider.setMax(max);
        topSlider.setValue(max);

        topSlider.valueProperty().addListener((obs, o, n) -> {
            if((double)n <= bottomSlider.getValue()){ bottomSlider.setValue((double)n-minRange); }
            updateTrack();

        });
        bottomSlider.valueProperty().addListener((obs, o, n) -> {
            if((double)n >= topSlider.getValue()){ topSlider.setValue((double)n+minRange); }
            updateTrack();
        });

        track.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("clicked");
            }
        });

        getChildren().addAll(trackBackground,track, bottomSlider, topSlider );
    }

    private Slider createSlider(){
        Slider s = new Slider();
        s.setOrientation(Orientation.VERTICAL);
        return s;
    }

    private Pane createPane(String className){
        Pane t = new Pane();
        t.getStyleClass().add(className);
        return t;
    }

    private void updateTrack(){
        double h = track.getHeight();
        double topInset = h - h/((max-min)/(topSlider.getValue()-min));
        double bottomInset = h/((max-min)/(bottomSlider.getValue()-min));
        String s = "-fx-background-insets: " + topInset + " 0 " + bottomInset + " 0px";
        track.setStyle(s);
    }

    private Node getChildByClass(Parent node, String subClass){
        for(Node n : node.getChildrenUnmodifiable()){
            if(n.getStyleClass().contains(subClass)){
                return n;
            }
        }
        return null;
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        topSlider.setPickOnBounds(false);
        bottomSlider.setPickOnBounds(false);
        getChildByClass(topSlider, "track").setVisible(false);
        getChildByClass(bottomSlider, "track").setVisible(false);

        // After GCode viewer ready, delete this line
        getChildByClass(bottomSlider, "thumb").setVisible(false);

    }

    public double getMinRange() {
        return minRange;
    }

    public void setMinRange(double minRange) {
        this.minRange = minRange;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
        bottomSlider.setMin(min);
        topSlider.setMin(min);
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
        bottomSlider.setMax(max);
        topSlider.setMax(max);
    }

    public void setTopValue(double value){
        topSlider.setValue(value);
    }

    public double getTopValue(){
        return topSlider.getValue();
    }

    public void setBottomValue(double value){
        bottomSlider.setValue(value);
    }

    public double getBottomValue(){
        return bottomSlider.getValue();
    }

    public DoubleProperty topValueProperty(){
        return topSlider.valueProperty();
    }

    public DoubleProperty bottomValueProperty(){
        return bottomSlider.valueProperty();
    }
}
