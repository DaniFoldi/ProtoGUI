package com.danifoldi.protogui.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListUtil {
    public static boolean containsIgnoreCase(final @NotNull String element,
                                             final @NotNull Collection<String> elements) {
        return elements.stream().anyMatch(element::equalsIgnoreCase);
    }

    public static<T> List<T> mutableCopyOf(List<T> list) {
        return new ArrayList<>(list);
    }

    public static<T> List<T> concat(List<T> list, List<T> other) {
        List<T> first = mutableCopyOf(list);
        first.addAll(other);
        return first;
    }

    private ListUtil() {
        throw new UnsupportedOperationException();
    }
}