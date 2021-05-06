package com.danifoldi.bungeegui.gui;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class GuiGrid {
    private final Map<Integer, GuiItem> items;
    private final boolean isTargeted;
    private final List<String> commandAliases;
    private final String permission;
    private final int guiSize;
    private final String title;

    public GuiGrid(final @NotNull Map<Integer, GuiItem> items,
                   final boolean isTargeted,
                   final @NotNull List<String> commandAliases,
                   final @NotNull String permission,
                   final int guiSize,
                   final @NotNull String title) {
        this.items = items;
        this.isTargeted = isTargeted;
        this.commandAliases = commandAliases;
        this.permission = permission;
        this.guiSize = guiSize;
        this.title = title;
    }

    public Map<Integer, GuiItem> getItems() {
        return Map.copyOf(items);
    }

    public boolean isTargeted() {
        return isTargeted;
    }

    public List<String> getCommandAliases() {
        return commandAliases;
    }

    public String getPermission() {
        return permission;
    }

    public int getGuiSize() {
        return guiSize;
    }

    public String getTitle() {
        return title;
    }
}