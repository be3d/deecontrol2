package com.ysoft.dctrl.ui.tooltip;

import javafx.scene.Node;

/**
 * Created by kuhn on 8/25/2017.
 */
public class Tooltip {

    private Node target;
    private final TooltipDefinition content;

    public Tooltip(TooltipDefinition content){
        this.content = content;
    }

    public String getTitle() {
        return content.getTitle();
    }

    public String[] getImgPaths() { return content.getImgPaths(); }

    public String[] getImgLabels() {
        return content.getImgLabels();
    }

    public String getDescription() {
        return content.getText();
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target){ this.target = target; }
}
