package com.ysoft.dctrl.ui.controller.controlMenu;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by kuhn on 5/31/2017.
 */
public class Header extends BaseCustomControl {

    @FXML
    ImageView icon;

    private String imgUrl;

    public Header(){
        super.init("/view/controlMenu/section_header.fxml");
    }

    public String getIcon(){ return imgUrl; }

    public void setIcon(String path){
        if (path != null && !path.isEmpty()){
            imgUrl = path;
            Image image = new Image(path);
            icon.setImage(image);
        }
    }

}

