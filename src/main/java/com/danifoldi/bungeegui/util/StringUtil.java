package com.danifoldi.bungeegui.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class StringUtil {

    public static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1).toLowerCase(Locale.ROOT);
    }
    public static Pair<String, String> get(String value) {
        if (!value.contains(":")) {
            return Pair.of("", value);
        }

        final String a = Arrays.stream(value.split(":")).findFirst().orElse("");
        final String b = Arrays.stream(value.split(":")).skip(1L).collect(Collectors.joining(":"));

        return Pair.of(a, b);
    }

    public static List<String> blockPrint(String value) {
        final String mid = " ".repeat(6) + value + " ".repeat(6);
        return List.of("-".repeat(mid.length()), " ".repeat(mid.length()), mid, " ".repeat(mid.length()), "-".repeat(mid.length()));
    }

    private StringUtil() {
        throw new UnsupportedOperationException();
    }
}
