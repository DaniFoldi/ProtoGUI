package com.danifoldi.bungeegui.gui;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GuiGrid {
    private final Map<Integer, GuiItem> items;
    private final boolean isTargeted;
    private final List<String> commandAliases;
    private final String permission;
    private final int guiSize;
    private final String title;

    private GuiGrid(final @NotNull Map<Integer, GuiItem> items,
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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Map<Integer, GuiItem> items;
        private boolean isTargeted;
        private List<String> commandAliases;
        private String permission;
        private int guiSize;
        private String title;

        private Builder() {}

        public @NotNull Builder items(final @NotNull Map<Integer, GuiItem> items) {
            this.items = items;
            return this;
        }

        public @NotNull Builder targeted(final boolean isTargeted) {
            this.isTargeted = isTargeted;
            return this;
        }

        public @NotNull Builder commands(final @NotNull List<String> commandAliases) {
            this.commandAliases = commandAliases;
            return this;
        }

        public @NotNull Builder permssion(final @NotNull String permission) {
            this.permission = permission;
            return this;
        }

        public @NotNull Builder size(final int guiSize) {
            this.guiSize = guiSize;
            return this;
        }

        public @NotNull Builder title(final @NotNull String title) {
            this.title = title;
            return this;
        }

        public @NotNull GuiGrid build() {
            return new GuiGrid(Objects.requireNonNull(items),
                                isTargeted,
                                Objects.requireNonNull(commandAliases),
                                Objects.requireNonNull(permission),
                                guiSize,
                                Objects.requireNonNull(title)
            );
        }
    }
}