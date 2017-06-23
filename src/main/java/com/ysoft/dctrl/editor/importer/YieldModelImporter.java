package com.ysoft.dctrl.editor.importer;

import java.util.function.Consumer;

/**
 * Created by pilar on 8.6.2017.
 */
public abstract class YieldModelImporter<T> extends AbstractModelImporter {
    private Consumer<T> onYield;

    public YieldModelImporter() {
        onYield = null;
    }

    protected void yield(T object) {
        if(onYield == null) return;
        onYield.accept(object);
    }

    public void setOnYield(Consumer<T> consumer) {
        onYield = consumer;
    }

}