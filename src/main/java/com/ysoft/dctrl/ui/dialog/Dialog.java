package com.ysoft.dctrl.ui.dialog;

import com.ysoft.dctrl.ui.control.DialogPane;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Created by pilar on 23.5.2017.
 */
public class Dialog {
    private final StackPane wrapper;
    private final DialogPane root;

    public Dialog(DialogPane root) {
        this.root = root;
        this.wrapper = new StackPane();

        AnchorPane.setLeftAnchor(wrapper, 0.0);
        AnchorPane.setRightAnchor(wrapper, 0.0);
        AnchorPane.setTopAnchor(wrapper, 0.0);
        AnchorPane.setBottomAnchor(wrapper, 0.0);
        wrapper.getChildren().addAll(root);
        wrapper.setVisible(false);

        this.root.setOnCloseAction((e) -> {
            wrapper.setVisible(false);
            e.consume();
        });
    }

    public void show() {
        this.wrapper.setVisible(true);
    }

    public Region getNode() {
        return wrapper;
    }
}
