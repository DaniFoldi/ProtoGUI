package com.danifoldi.bungeegui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Deprecated(forRemoval = true)
public class GuiGrid {
    private final @NotNull com.danifoldi.protogui.gui.GuiGrid gui;

    private GuiGrid(final @NotNull com.danifoldi.protogui.gui.GuiGrid gui) {
        this.gui = gui;
    }

    @Deprecated(forRemoval = true)
    public @NotNull Map<Integer, GuiItem> getItems() {
        return gui.getItems().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> GuiItem.fromNew(e.getValue())));
    }

    @Deprecated(forRemoval = true)
    public boolean isTargeted() {
        return gui.isTargeted();
    }

    @Deprecated(forRemoval = true)
    public @NotNull List<String> getCommandAliases() {
        return gui.getCommandAliases();
    }

    @Deprecated(forRemoval = true)
    public @NotNull String getPermission() {
        return gui.getPermission();
    }

    @Deprecated(forRemoval = true)
    public int getGuiSize() {
        return gui.getGuiSize();
    }

    @Deprecated(forRemoval = true)
    public @NotNull String getTitle() {
        return gui.getTitle();
    }

    @Deprecated(forRemoval = true)
    public boolean isSelfTarget() {
        return gui.isSelfTarget();
    }

    @Deprecated(forRemoval = true)
    public boolean isIgnoreVanished() {
        return gui.isIgnoreVanished();
    }

    @Deprecated(forRemoval = true)
    public boolean isRequireOnlineTarget() {
        return gui.isRequireOnlineTarget();
    }

    @Deprecated(forRemoval = true)
    public @NotNull List<String> getWhitelistServers() {
        return gui.getWhitelistServers();
    }

    @Deprecated(forRemoval = true)
    public @NotNull List<String> getBlacklistServers() {
        return gui.getBlacklistServers();
    }

    @Deprecated(forRemoval = true)
    public boolean isPlaceholdersTarget() {
        return gui.isPlaceholdersTarget();
    }

    @Deprecated(forRemoval = true)
    public @Nullable GuiSound getOpenSound() {
        return GuiSound.fromNew(gui.getOpenSound());
    }

    @Deprecated(forRemoval = true)
    public boolean isTargetBypass() {
        return gui.isTargetBypass();
    }

    @Deprecated(forRemoval = true)
    public boolean isCloseable() {
        return gui.isCloseable();
    }

    @Deprecated(forRemoval = true)
    public @NotNull String getNotifyTarget() {
        return gui.getNotifyTarget();
    }

    @Override
    @Deprecated(forRemoval = true)
    public String toString() {
        return "GuiGrid{" +
                "gui=" + gui +
                '}';
    }

    @Override
    @Deprecated(forRemoval = true)
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuiGrid guiGrid = (GuiGrid) o;
        return gui.equals(guiGrid.gui);
    }

    @Override
    @Deprecated(forRemoval = true)
    public int hashCode() {
        return Objects.hash(gui);
    }

    @Deprecated(forRemoval = true)
    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private @NotNull com.danifoldi.protogui.gui.GuiGrid.Builder builder;

        private Builder() {
            builder = com.danifoldi.protogui.gui.GuiGrid.builder();
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder items(final @NotNull Map<Integer, GuiItem> items) {
            builder.items(items.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> GuiItem.toNew(e.getValue()))));
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder targeted(final boolean isTargeted) {
            builder.targeted(isTargeted);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder commands(final @NotNull List<String> commandAliases) {
            builder.commands(commandAliases);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder permission(final @NotNull String permission) {
            builder.permission(permission);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder size(final int guiSize) {
            builder.size(guiSize);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder title(final @NotNull String title) {
            builder.title(title);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder selfTarget(final boolean selfTarget) {
            builder.selfTarget(selfTarget);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder ignoreVanished(final boolean ignoreVanished) {
            builder.ignoreVanished(ignoreVanished);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder requireOnlineTarget(final boolean requireOnlineTarget) {
            builder.requireOnlineTarget(requireOnlineTarget);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder whitelistServers(final @NotNull List<String> whitelistServers) {
            builder.whitelistServers(whitelistServers);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder blacklistServers(final @NotNull List<String> blacklistServers) {
            builder.blacklistServers(blacklistServers);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder placeholdersTarget(final boolean placeholdersTarget) {
            builder.placeholdersTarget(placeholdersTarget);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder openSound(final @Nullable GuiSound openSound) {
            builder.openSound(GuiSound.toNew(openSound));
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder targetBypass(final boolean targetBypass) {
            builder.targetBypass(targetBypass);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder closeable(final boolean closeable) {
            builder.closeable(closeable);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder notifyTarget(final @NotNull String notifyTarget) {
            builder.notifyTarget(notifyTarget);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull GuiGrid build() {
            return new GuiGrid(builder.build());
        }
    }

    @Deprecated(forRemoval = true)
    public GuiGrid copy() {
        return new GuiGrid(gui);
    }

    @Deprecated(forRemoval = true)
    public static com.danifoldi.protogui.gui.GuiGrid toNew(GuiGrid old) {
        return old.gui.copy();
    }

    @Deprecated(forRemoval = true)
    public static GuiGrid fromNew(com.danifoldi.protogui.gui.GuiGrid n) {
        return new GuiGrid(n.copy());
    }
}