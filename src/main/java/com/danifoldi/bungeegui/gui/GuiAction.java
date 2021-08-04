package com.danifoldi.bungeegui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

public class GuiAction {
    private final @NotNull String server;
    private final int slot;
    private final @NotNull GuiItem guiItem;
    private final @NotNull String gui;

    private GuiAction(final @NotNull String server,
                      final int slot,
                      final @NotNull GuiItem guiItem,
                      final @NotNull String gui) {
        this.server = server;
        this.slot = slot;
        this.guiItem = guiItem.copy();
        this.gui = gui;
    }

    public static Builder builder() {
        return new Builder();
    }

    public @NotNull String getServer() {
        return server;
    }

    public int getSlot() {
        return slot;
    }

    public @NotNull GuiItem getGuiItem() {
        return guiItem;
    }

    public @NotNull String getGui() {
        return gui;
    }

    public static final class Builder {
        private @Nullable String server;
        private int slot;
        private @Nullable GuiItem guiItem;
        private @Nullable String gui;

        private Builder() {}

        public @NotNull Builder server(final @NotNull String server) {
            this.server = server;
            return this;
        }

        public @NotNull Builder slot(final int slot) {
            this.slot = slot;
            return this;
        }

        public @NotNull Builder guiItem(final @NotNull GuiItem guiItem) {
            this.guiItem = guiItem;
            return this;
        }

        public @NotNull Builder gui(final @NotNull String gui) {
            this.gui = gui;
            return this;
        }

        public GuiAction build() {
            return new GuiAction(requireNonNull(server),
                    slot,
                    requireNonNull(guiItem),
                    requireNonNull(gui));
        }
    }
}
