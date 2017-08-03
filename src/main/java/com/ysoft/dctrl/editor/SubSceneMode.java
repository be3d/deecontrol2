package com.ysoft.dctrl.editor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by pilar on 20.7.2017.
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface SubSceneMode {
    SceneMode value();
}
