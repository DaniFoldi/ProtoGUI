package com.danifoldi.protogui.util;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapUtil {
    public static @NotNull Map<String, String> convertToMap(final @NotNull String... input) {
        final @NotNull Map<String, String> map = new HashMap<>();

        final @NotNull Iterator<String> iterator = Arrays.stream(input).iterator();

        while (iterator.hasNext()) {
            final @NotNull String key = iterator.next();
            if (iterator.hasNext()) {
                map.put(key, iterator.next());
            }
        }

        return map;
    }

    private MapUtil() {
        throw new UnsupportedOperationException();
    }
}
