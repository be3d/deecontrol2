package com.ysoft.dctrl.ui.controller.controlMenu;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * Created by kuhn on 5/9/2017.
 */
public class TextInput extends BaseCustomControl {

    @FXML
    private Label label;
    @FXML
    private TextField text;

    public TextInput(){
        super.init("/view/controlMenu/text_input.fxml");
    }

    public String getText() { return textProperty().get();}
    public void setText(String value) { textProperty().set(value);}
    public StringProperty textProperty() {return text.textProperty();}

    public void setTextPlaceHolder(String value){text.setPromptText(value);}
    public String getTextPlaceHolder(){return text.getPromptText();};

    public String getLabel() { return labelProperty().get();}
    public void setLabel(String value) { labelProperty().set(value);}
    public StringProperty labelProperty() {return label.textProperty();}

    public void addChangeListener(ChangeListener<? super String> listener){
        text.textProperty().addListener(listener);
    }

    public void addFocusChangedListener(ChangeListener<? super Boolean> listener){
        text.focusedProperty().addListener(listener);
    }
}
