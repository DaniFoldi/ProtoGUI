package com.danifoldi.bungeegui.gui;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Deprecated(forRemoval = true)
public class GuiAction {
    private final @NotNull com.danifoldi.protogui.gui.GuiAction guiAction;

    private GuiAction(final @NotNull com.danifoldi.protogui.gui.GuiAction action) {
        this.guiAction = action;
    }

    @Deprecated(forRemoval = true)
    public @NotNull String getServer() {
        return guiAction.getServer();
    }

    @Deprecated(forRemoval = true)
    public int getSlot() {
        return guiAction.getSlot();
    }

    @Deprecated(forRemoval = true)
    public @NotNull GuiItem getGuiItem() {
        return GuiItem.fromNew(guiAction.getGuiItem());
    }

    @Deprecated(forRemoval = true)
    public @NotNull String getGui() {
        return guiAction.getGui();
    }

    @Override
    @Deprecated(forRemoval = true)
    public String toString() {
        return "GuiAction{" +
                "action=" + guiAction +
                '}';
    }

    @Override
    @Deprecated(forRemoval = true)
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuiAction guiAction = (GuiAction) o;
        return this.guiAction.equals(guiAction.guiAction);
    }

    @Override
    @Deprecated(forRemoval = true)
    public int hashCode() {
        return Objects.hash(guiAction);
    }

    @Deprecated(forRemoval = true)
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private @NotNull com.danifoldi.protogui.gui.GuiAction.Builder builder;

        private Builder() {
            builder = com.danifoldi.protogui.gui.GuiAction.builder();
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder server(final @NotNull String server) {
            builder.server(server);
            return this;
        }
        @Deprecated(forRemoval = true)

        public @NotNull Builder slot(final int slot) {
            builder.slot(slot);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder guiItem(final @NotNull GuiItem guiItem) {
            builder.guiItem(GuiItem.toNew(guiItem));
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder gui(final @NotNull String gui) {
            builder.gui(gui);
            return this;
        }

        @Deprecated(forRemoval = true)
        public GuiAction build() {
            return new GuiAction(builder.build());
        }
    }

    @Deprecated(forRemoval = true)
    public GuiAction copy() {
        return new GuiAction(guiAction);
    }

    @Deprecated(forRemoval = true)
    public static com.danifoldi.protogui.gui.GuiAction toNew(GuiAction old) {
        return old.guiAction.copy();
    }

    @Deprecated(forRemoval = true)
    public static GuiAction fromNew(com.danifoldi.protogui.gui.GuiAction n) {
        return new GuiAction(n.copy());
    }
}