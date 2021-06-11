package com.danifoldi.bungeegui.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
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

    public static void blockPrint(Consumer<String> action, String value) {
        final String mid = " ".repeat(6) + value + " ".repeat(6);
        List<String> lines = List.of("-".repeat(mid.length()), " ".repeat(mid.length()), mid, " ".repeat(mid.length()), "-".repeat(mid.length()));

        lines.forEach(action);
    }

    private StringUtil() {
        throw new UnsupportedOperationException();
    }
}
