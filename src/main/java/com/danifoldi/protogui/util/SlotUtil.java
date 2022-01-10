package com.danifoldi.protogui.util;

import dev.simplix.protocolize.data.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SlotUtil {

    public static @NotNull Set<Integer> getSlots(final @NotNull String slots, final int size) {
        final @NotNull Set<Integer> slotList = new HashSet<>();
        List<String> terms = Arrays
                .stream(slots.replace("-", "-$").split("\\+"))
                .map(s -> s.split("-"))
                .flatMap(Arrays::stream)
                .map(String::trim)
                .map(s -> s.toLowerCase(Locale.ROOT))
                .toList();
        for (@NotNull String term: terms) {
            @NotNull Consumer<Integer> action = slotList::add;
            if (term.startsWith("$")) {
                action = slotList::remove;
                term = term.replace("$", "");
            }

            if (term.startsWith("row")) {
                final int row = Integer.parseInt(term, 3, 4, 10);

                if (term.endsWith("even")) {
                    for (int i = 0; i < 9; i++) {
                        if (i % 2 != 0) {
                            continue;
                        }

                        action.accept(row * 9 + i);
                    }
                } else if (term.endsWith("odd")) {
                    for (int i = 0; i < 9; i++) {
                        if (i % 2 == 0) {
                            continue;
                        }

                        action.accept(row * 9 + i);
                    }
                } else {
                    for (int i = 0; i < 9; i++) {
                        action.accept(row * 9 + i);
                    }
                }
                continue;
            }

            if (term.startsWith("column")) {
                final int column = Integer.parseInt(term, 6, 7, 10);
                if (term.endsWith("even")) {
                    for (int i = 0; i < 6; i++) {
                        if (i % 2 != 0) {
                            continue;
                        }

                        action.accept(column + 9 * i);
                    }
                } else if (term.endsWith("odd")) {
                    for (int i = 0; i < 6; i++) {
                        if (i % 2 == 0) {
                            continue;
                        }

                        action.accept(column + 9 * i);
                    }
                } else {
                    for (int i = 0; i < 6; i++) {
                        action.accept(column + 9 * i);
                    }
                }
                continue;
            }

            final int slot = Integer.parseInt(term);
            action.accept(slot);
        }

        return slotList.stream().filter(s -> s >= 0 && s < size).collect(Collectors.toSet());
    }

    public static @NotNull InventoryType getInventoryType(final int size) {
        if (size <= 9) {
            return InventoryType.GENERIC_9X1;
        } else if (size <= 2 * 9) {
            return InventoryType.GENERIC_9X2;
        } else if (size <= 3 * 9) {
            return InventoryType.GENERIC_9X3;
        } else if (size <= 4 * 9) {
            return InventoryType.GENERIC_9X4;
        } else if (size <= 5 * 9) {
            return InventoryType.GENERIC_9X5;
        } else {
            return InventoryType.GENERIC_9X6;
        }
    }

    public static int getInventorySize(final @NotNull InventoryType inventoryType) {
        return switch (inventoryType) {
            case GENERIC_9X1 -> 9;
            case GENERIC_9X2 -> 18;
            case GENERIC_9X3 -> 27;
            case GENERIC_9X4 -> 36;
            case GENERIC_9X5 -> 45;
            case GENERIC_9X6 -> 54;
            default -> 0;
        };

    }

    private SlotUtil() {
        throw new UnsupportedOperationException();
    }
}
