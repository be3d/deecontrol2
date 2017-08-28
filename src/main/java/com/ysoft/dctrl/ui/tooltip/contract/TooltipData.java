package com.ysoft.dctrl.ui.tooltip.contract;

import javafx.scene.Node;

/**
 * Created by kuhn on 8/25/2017.
 */
public class TooltipData {
    private final String title;
    private final String imagePath;
    private final String description;
    private Node target;

    public TooltipData(String title, String imagePath, String description) {
        this.title = title;
        this.imagePath = imagePath;
        this.description = description;
    }

    public TooltipData(String title, String imagePath, String description, Node target) {
        this.title = title;
        this.imagePath = imagePath;
        this.description = description;
        this.target = target;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDescription() {
        return description;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target){ this.target = target; }
}
