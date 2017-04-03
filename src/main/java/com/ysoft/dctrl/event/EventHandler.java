package com.ysoft.dctrl.event;

import java.util.function.Consumer;

/**
 * Created by pilar on 20.3.2017.
 */
@FunctionalInterface
public interface EventHandler extends Consumer<Event> {
}
