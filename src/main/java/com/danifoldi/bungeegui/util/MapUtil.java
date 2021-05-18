package com.danifoldi.bungeegui.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapUtil {
    public static Map<String, String> convertToMap(String... input) {
        Map<String, String> map = new HashMap<>();

        Iterator<String> iterator = Arrays.stream(input).iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();
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
