package com.ysoft.dctrl.utils;

/**
 * Created by pilar on 14.7.2017.
 */
public enum OSVersion {
    WIN, MAC;

    private static OSVersion current = System.getProperty("os.name").toLowerCase().contains("windows") ? WIN : MAC;
    public static boolean is(OSVersion version) {
        return version == current;
    }
}
