package com.danifoldi.bungeegui.util;

import java.text.DecimalFormat;

public class NumberUtil {
    private static final DecimalFormat format = new DecimalFormat("#.##");
    public static String formatDecimal(double value) {
        return format.format(value);
    }

    private NumberUtil() {
        throw new UnsupportedOperationException();
    }
}
