package com.danifoldi.protogui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@SuppressWarnings("ClassCanBeRecord")
public class GuiGrid {
    private final @NotNull Map<Integer, GuiItem> items;
    private final boolean isTargeted;
    private final @NotNull List<String> commandAliases;
    private final @NotNull String permission;
    private final int guiSize;
    private final @NotNull String title;
    private final boolean selfTarget;
    private final boolean ignoreVanished;
    private final boolean requireOnlineTarget;
    private final @NotNull List<String> whitelistServers;
    private final @NotNull List<String> blacklistServers;
    private final boolean placeholdersTarget;
    private final @Nullable GuiSound openSound;
    private final boolean targetBypass;
    private final boolean closeable;
    private final @NotNull String notifyTarget;

    private GuiGrid(final @NotNull Map<Integer, GuiItem> items,
                    final boolean isTargeted,
                    final @NotNull List<String> commandAliases,
                    final @NotNull String permission,
                    final int guiSize,
                    final @NotNull String title,
                    final boolean selfTarget,
                    final boolean ignoreVanished,
                    final boolean requireOnlineTarget,
                    final @NotNull List<String> whitelistServers,
                    final @NotNull List<String> blacklistServers,
                    final boolean placeholdersTarget,
                    final @Nullable GuiSound openSound,
                    final boolean targetBypass,
                    final boolean closeable,
                    final @NotNull String notifyTarget) {
        this.items = items.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().copy()));
        this.isTargeted = isTargeted;
        this.commandAliases = List.copyOf(commandAliases);
        this.permission = permission;
        this.guiSize = guiSize;
        this.title = title;
        this.selfTarget = selfTarget;
        this.ignoreVanished = ignoreVanished;
        this.requireOnlineTarget = requireOnlineTarget;
        this.whitelistServers = List.copyOf(whitelistServers);
        this.blacklistServers = List.copyOf(blacklistServers);
        this.placeholdersTarget = placeholdersTarget;
        this.openSound = openSound;
        this.targetBypass = targetBypass;
        this.closeable = closeable;
        this.notifyTarget = notifyTarget;
    }

    public @NotNull Map<Integer, GuiItem> getItems() {
        return Map.copyOf(this.items);
    }

    public boolean isTargeted() {
        return this.isTargeted;
    }

    public @NotNull List<String> getCommandAliases() {
        return List.copyOf(this.commandAliases);
    }

    public @NotNull String getPermission() {
        return this.permission;
    }

    public int getGuiSize() {
        return this.guiSize;
    }

    public @NotNull String getTitle() {
        return this.title;
    }

    public boolean isSelfTarget() {
        return this.selfTarget;
    }

    public boolean isIgnoreVanished() {
        return this.ignoreVanished;
    }

    public boolean isRequireOnlineTarget() {
        return this.requireOnlineTarget;
    }

    public @NotNull List<String> getWhitelistServers() {
        return List.copyOf(this.whitelistServers);
    }

    public @NotNull List<String> getBlacklistServers() {
        return List.copyOf(this.blacklistServers);
    }

    public boolean isPlaceholdersTarget() {
        return this.placeholdersTarget;
    }

    public @Nullable GuiSound getOpenSound() {
        return this.openSound;
    }

    public boolean isTargetBypass() {
        return this.targetBypass;
    }

    public boolean isCloseable() {
        return this.closeable;
    }

    public @NotNull String getNotifyTarget() {
        return this.notifyTarget;
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private @Nullable Map<Integer, GuiItem> items;
        private boolean isTargeted;
        private @Nullable List<String> commandAliases;
        private @Nullable String permission;
        private int guiSize;
        private @Nullable String title;
        private boolean selfTarget;
        private boolean ignoreVanished;
        private boolean requireOnlineTarget;
        private @Nullable List<String> whitelistServers;
        private @Nullable List<String> blacklistServers;
        private boolean placeholdersTarget;
        private @Nullable GuiSound openSound;
        private boolean targetBypass;
        private boolean closeable;
        private @Nullable String notifyTarget;

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

        public @NotNull Builder permission(final @NotNull String permission) {
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

        public @NotNull Builder selfTarget(final boolean selfTarget) {
            this.selfTarget = selfTarget;
            return this;
        }

        public @NotNull Builder ignoreVanished(final boolean ignoreVanished) {
            this.ignoreVanished = ignoreVanished;
            return this;
        }

        public @NotNull Builder requireOnlineTarget(final boolean requireOnlineTarget) {
            this.requireOnlineTarget = requireOnlineTarget;
            return this;
        }

        public @NotNull Builder whitelistServers(final @NotNull List<String> whitelistServers) {
            this.whitelistServers = whitelistServers;
            return this;
        }

        public @NotNull Builder blacklistServers(final @NotNull List<String> blacklistServers) {
            this.blacklistServers = blacklistServers;
            return this;
        }

        public @NotNull Builder placeholdersTarget(final boolean placeholdersTarget) {
            this.placeholdersTarget = placeholdersTarget;
            return this;
        }

        public @NotNull Builder openSound(final @Nullable GuiSound openSound) {
            this.openSound = openSound;
            return this;
        }

        public @NotNull Builder targetBypass(final boolean targetBypass) {
            this.targetBypass = targetBypass;
            return this;
        }

        public @NotNull Builder closeable(final boolean closeable) {
            this.closeable = closeable;
            return this;
        }

        public @NotNull Builder notifyTarget(final @NotNull String notifyTarget) {
            this.notifyTarget = notifyTarget;
            return this;
        }

        public @NotNull GuiGrid build() {
            return new GuiGrid(Objects.requireNonNull(items),
                    isTargeted,
                    Objects.requireNonNull(commandAliases),
                    Objects.requireNonNull(permission),
                    guiSize,
                    Objects.requireNonNull(title),
                    selfTarget,
                    ignoreVanished,
                    requireOnlineTarget,
                    Objects.requireNonNull(whitelistServers),
                    Objects.requireNonNull(blacklistServers),
                    placeholdersTarget,
                    openSound,
                    targetBypass,
                    closeable,
                    Objects.requireNonNull(notifyTarget)
            );
        }
    }

    @Override
    public String toString() {
        return "GuiGrid{" +
                "items=" + items +
                ", isTargeted=" + isTargeted +
                ", commandAliases=" + commandAliases +
                ", permission='" + permission + '\'' +
                ", guiSize=" + guiSize +
                ", title='" + title + '\'' +
                ", selfTarget=" + selfTarget +
                ", ignoreVanished=" + ignoreVanished +
                ", requireOnlineTarget=" + requireOnlineTarget +
                ", whitelistServers=" + whitelistServers +
                ", blacklistServers=" + blacklistServers +
                ", placeholdersTarget=" + placeholdersTarget +
                ", openSound=" + openSound +
                ", targetBypass=" + targetBypass +
                ", closeable=" + closeable +
                ", notifyTarget='" + notifyTarget + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuiGrid guiGrid = (GuiGrid) o;
        return isTargeted == guiGrid.isTargeted && guiSize == guiGrid.guiSize && selfTarget == guiGrid.selfTarget && ignoreVanished == guiGrid.ignoreVanished && requireOnlineTarget == guiGrid.requireOnlineTarget && placeholdersTarget == guiGrid.placeholdersTarget && targetBypass == guiGrid.targetBypass && closeable == guiGrid.closeable && items.equals(guiGrid.items) && commandAliases.equals(guiGrid.commandAliases) && permission.equals(guiGrid.permission) && title.equals(guiGrid.title) && whitelistServers.equals(guiGrid.whitelistServers) && blacklistServers.equals(guiGrid.blacklistServers) && Objects.equals(openSound, guiGrid.openSound) && notifyTarget.equals(guiGrid.notifyTarget);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, isTargeted, commandAliases, permission, guiSize, title, selfTarget, ignoreVanished, requireOnlineTarget, whitelistServers, blacklistServers, placeholdersTarget, openSound, targetBypass, closeable, notifyTarget);
    }

    public GuiGrid copy() {
        return new GuiGrid(items, isTargeted, commandAliases, permission, guiSize, title, selfTarget, ignoreVanished, requireOnlineTarget, whitelistServers, blacklistServers, placeholdersTarget, openSound, targetBypass, closeable, notifyTarget);
    }
}