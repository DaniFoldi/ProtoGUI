package com.danifoldi.protogui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("ClassCanBeRecord")
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

    @Override
    public String toString() {
        return "GuiAction{" +
                "server='" + server + '\'' +
                ", slot=" + slot +
                ", guiItem=" + guiItem +
                ", gui='" + gui + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuiAction guiAction = (GuiAction) o;
        return slot == guiAction.slot && server.equals(guiAction.server) && guiItem.equals(guiAction.guiItem) && gui.equals(guiAction.gui);
    }

    @Override
    public int hashCode() {
        return Objects.hash(server, slot, guiItem, gui);
    }

    public GuiAction copy() {
        return new GuiAction(server, slot, guiItem, gui);
    }
}
