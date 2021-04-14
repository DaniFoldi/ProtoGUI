package hu.nugget.bungeegui.gui;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class GuiGrid {
    private final Map<Integer, GuiItem> items;
    private final Set<UUID> viewers = new HashSet<>();

    public GuiGrid(final @NotNull Map<Integer, GuiItem> items) {
        this.items = items;
    }

    public @NotNull Optional<GuiItem> getItem(final int slot) {
        return Optional.ofNullable(this.items.get(slot));
    }
}