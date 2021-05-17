package com.danifoldi.bungeegui.util;

import de.exceptionflug.protocolize.inventory.InventoryType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SlotUtil {

    public static Set<Integer> getSlots(String slots, int size) {
        Set<Integer> slotList = new HashSet<>();

        for (String term: Arrays.stream(slots.split(",")).map(String::trim).map(s -> s.toLowerCase(Locale.ROOT)).collect(Collectors.toList())) {
            Consumer<Integer> action = slotList::add;
            if (term.startsWith("-")) {
                action = slotList::remove;
                term = term.replace("-", "");
            }

            if (term.startsWith("row")) {
                int row = Integer.parseInt(term, 3, 4, 10);

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
                int column = Integer.parseInt(term, 6, 7, 10);
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

            int slot = Integer.parseInt(term);
            action.accept(slot);
        }

        return slotList.stream().filter(s -> s >= 0 && s < size).collect(Collectors.toSet());
    }

    public static InventoryType getInventoryType(int size) {
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

    public static int getInventorySize(InventoryType inventoryType) {
        switch (inventoryType) {
            case GENERIC_9X1:
                return 9;
            case GENERIC_9X2:
                return 18;
            case GENERIC_9X3:
                return 27;
            case GENERIC_9X4:
                return 36;
            case GENERIC_9X5:
                return 45;
            case GENERIC_9X6:
                return 54;
        }

        return 0;
    }

    private SlotUtil() {
        throw new UnsupportedOperationException();
    }
}
