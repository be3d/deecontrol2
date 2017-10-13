package com.ysoft.dctrl.ui.dialog.contract;

import java.util.function.Consumer;

/**
 * Created by kuhn on 10/12/2017.
 */
public class TextInputDialogData {

    private String header;
    private String label;
    private String description;
    public Consumer<String> consumer;

    public TextInputDialogData() {
        header = "";
        label = "";
        description = "";
        consumer = null;
    }

    public TextInputDialogData(String header, String label, String description, Consumer<String> consumer) {
        this.header = header;
        this.label = label;
        this.description = description;
        this.consumer = consumer;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Consumer<String> getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer<String> consumer) {
        this.consumer = consumer;
    }
}
