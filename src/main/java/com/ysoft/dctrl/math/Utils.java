package com.ysoft.dctrl.math;

import java.util.function.BiFunction;

/**
 * Created by pilar on 5.7.2017.
 */
public class Utils {
    private Utils() {}

    public static double min(Double... values) {
        return extreme((a,b) -> b < a, Double.MAX_VALUE, values);
    }

    public static int min(Integer... values) {
        return extreme((a,b) -> b < a, Integer.MAX_VALUE, values);
    }

    public static float min(Float... values) {
        return extreme((a,b) -> b < a, Float.MAX_VALUE, values);
    }

    public static short min(Short... values) {
        return extreme((a,b) -> b < a, Short.MAX_VALUE, values);
    }

    public static double max(Double... values) {
        return extreme((a,b) -> b > a, -Double.MAX_VALUE, values);
    }

    public static int max(Integer... values) {
        return extreme((a,b) -> b > a, Integer.MIN_VALUE, values);
    }

    public static float max(Float... values) {
        return extreme((a,b) -> b > a, -Float.MAX_VALUE, values);
    }

    public static short max(Short... values) {
        return extreme((a,b) -> b > a, Short.MIN_VALUE, values);
    }

    private static <T> T extreme(BiFunction<T, T, Boolean> comparator, T defaultValue, T... values) {
        T res = defaultValue;
        for(T v : values) {
            res = comparator.apply(res, v) ? v : res;
        }

        return res;
    }

    public static double clamp(double value, double min, double max) {
        return (value < min) ? min : ((value > max) ? max : value);
    }
}
