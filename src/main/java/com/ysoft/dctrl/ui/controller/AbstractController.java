package com.ysoft.dctrl.ui.controller;

import com.ysoft.dctrl.event.EventBus;
import com.ysoft.dctrl.utils.DeeControlContext;

/**
 * Created by pilar on 31.3.2017.
 */
public abstract class AbstractController {
    protected final EventBus eventBus;
    protected final DeeControlContext deeControlContext;

    public AbstractController(EventBus eventBus, DeeControlContext deeControlContext) {
        this.eventBus = eventBus;
        this.deeControlContext = deeControlContext;
    }
}
