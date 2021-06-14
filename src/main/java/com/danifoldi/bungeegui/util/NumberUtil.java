package com.danifoldi.bungeegui.util;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

public class NumberUtil {
    private static final @NotNull DecimalFormat format = new DecimalFormat("#.##");
    public static @NotNull String formatDecimal(final double value) {
        return format.format(value);
    }

    private NumberUtil() {
        throw new UnsupportedOperationException();
    }
}
