package com.danifoldi.bungeegui.util;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ListUtil {
    public static boolean containsIgnoreCase(final @NotNull String element,
                                             final @NotNull Collection<String> elements) {
        return elements.stream().anyMatch(element::equalsIgnoreCase);
    }

    private ListUtil() {
        throw new UnsupportedOperationException();
    }
}