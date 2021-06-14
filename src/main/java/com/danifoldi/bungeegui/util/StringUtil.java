package com.danifoldi.bungeegui.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StringUtil {

    public static @NotNull String capitalize(@NotNull String value) {
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1).toLowerCase(Locale.ROOT);
    }
    public static @NotNull Pair<String, String> get(@NotNull String value) {
        if (!value.contains(":")) {
            return Pair.of("", value);
        }

        final @NotNull String a = Arrays.stream(value.split(":")).findFirst().orElse("");
        final @NotNull String b = Arrays.stream(value.split(":")).skip(1L).collect(Collectors.joining(":"));

        return Pair.of(a, b);
    }

    public static void blockPrint(final @NotNull Consumer<String> action, final @NotNull String value) {
        final @NotNull String mid = " ".repeat(6) + value + " ".repeat(6);
        final @NotNull List<String> lines = List.of("-".repeat(mid.length()), " ".repeat(mid.length()), mid, " ".repeat(mid.length()), "-".repeat(mid.length()));

        lines.forEach(action);
    }

    private StringUtil() {
        throw new UnsupportedOperationException();
    }
}
