package com.danifoldi.bungeegui.gui;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GuiGrid {
    private final Map<Integer, GuiItem> items;
    private final boolean isTargeted;
    private final List<String> commandAliases;
    private final String permission;
    private final int guiSize;
    private final String title;
    private final boolean selfTarget;
    private final boolean ignoreVanished;
    private final boolean requireOnlineTarget;
    private final List<String> whitelistServers;
    private final List<String> blacklistServers;
    private final boolean placeholdersTarget;
    private final GuiSound openSound;

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
                    final @Nullable GuiSound openSound) {
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
    }

    public Map<Integer, GuiItem> getItems() {
        return Map.copyOf(this.items);
    }

    public boolean isTargeted() {
        return this.isTargeted;
    }

    public List<String> getCommandAliases() {
        return List.copyOf(this.commandAliases);
    }

    public String getPermission() {
        return this.permission;
    }

    public int getGuiSize() {
        return this.guiSize;
    }

    public String getTitle() {
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

    public List<String> getWhitelistServers() {
        return List.copyOf(this.whitelistServers);
    }

    public List<String> getBlacklistServers() {
        return List.copyOf(this.blacklistServers);
    }

    public boolean isPlaceholdersTarget() {
        return this.placeholdersTarget;
    }

    public GuiSound getOpenSound() {
        return this.openSound;
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
        private boolean selfTarget;
        private boolean ignoreVanished;
        private boolean requireOnlineTarget;
        private List<String> whitelistServers;
        private List<String> blacklistServers;
        private boolean placeholdersTarget;
        private GuiSound openSound;

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
                    openSound
            );
        }
    }
}