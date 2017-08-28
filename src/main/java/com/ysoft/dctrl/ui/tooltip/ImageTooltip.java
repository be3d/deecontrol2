package com.ysoft.dctrl.ui.tooltip;

import javafx.scene.Node;

/**
 * Created by kuhn on 8/21/2017.
 */
public class ImageTooltip {
    private final String title;
    private final String imagePath;
    private final String description;
    private final Node target;

    public ImageTooltip(Node target, String title, String imagePath, String description) {
        this.target = target;
        this.title = title;
        this.imagePath = imagePath;
        this.description = description;
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
}
