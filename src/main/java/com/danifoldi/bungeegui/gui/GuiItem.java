package com.danifoldi.bungeegui.gui;

import de.exceptionflug.protocolize.items.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class GuiItem {
    private final @NotNull ItemType type;
    private final int amount;
    private final @NotNull String name;
    private final @NotNull List<String> lore;
    private final String data;
    private final @NotNull List<String> commands;
    private final boolean enchanted;
    private final @Nullable GuiSound clickSound;

    private GuiItem(final @NotNull ItemType type,
                    final int amount,
                    final @NotNull String name,
                    final @NotNull List<String> lore,
                    final @NotNull String data,
                    final @NotNull List<String> commands,
                    final boolean enchanted,
                    final @Nullable GuiSound clickSound) {
        this.type = type;
        this.amount = amount;
        this.name = name;
        this.lore = lore;
        this.data = data;
        this.commands = commands;
        this.enchanted = enchanted;
        this.clickSound = clickSound;
    }

    public @NotNull ItemType getType() {
        return this.type;
    }

    public int getAmount() {
        return this.amount;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull List<String> getLore() {
        return List.copyOf(this.lore);
    }

    public @NotNull String getData() {
        return this.data;
    }

    public @NotNull List<String> getCommands() {
        return List.copyOf(this.commands);
    }

    public boolean isEnchanted() {
        return this.enchanted;
    }

    public @Nullable GuiSound getClickSound() {
        return this.clickSound;
    }

    @Override
    public @NotNull String toString() {
        return "GuiItem{type=" + this.type
                + ", amount=" + this.amount
                + ", name=" + this.name
                + ", lore=" + this.lore
                + ", data=" + this.data
                + ", commands=" + this.commands
                + ", enchanted=" + this.enchanted
                + ", clickSound=" + this.clickSound
                + '}';
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GuiItem guiItem = (GuiItem) o;
        return this.amount == guiItem.amount
                && this.type == guiItem.type
                && Objects.equals(this.name, guiItem.name)
                && Objects.equals(this.lore, guiItem.lore)
                && Objects.equals(this.data, guiItem.data)
                && Objects.equals(this.commands, guiItem.commands)
                && this.enchanted == guiItem.enchanted
                && Objects.equals(this.clickSound, guiItem.clickSound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.amount, this.name, this.lore, this.data, this.commands, this.enchanted, this.clickSound);
    }

    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private @Nullable ItemType type;
        private int amount;
        private @Nullable String name;
        private @Nullable List<String> lore;
        private @Nullable String data;
        private @Nullable List<String> commands;
        private boolean enchanted;
        private @Nullable GuiSound clickSound;

        private Builder() {}

        public @NotNull Builder type(final @NotNull ItemType type) {
            this.type = type;
            return this;
        }

        public @NotNull Builder amount(final int amount) {
            this.amount = amount;
            return this;
        }

        public @NotNull Builder title(final @NotNull String name) {
            this.name = name;
            return this;
        }

        public @NotNull Builder lore(final @NotNull List<String> lore) {
            this.lore = lore;
            return this;
        }

        public @NotNull Builder data(final @NotNull String data) {
            this.data = data;
            return this;
        }

        public @NotNull Builder commands(final @NotNull List<String> commands) {
            this.commands = commands;
            return this;
        }

        public @NotNull Builder enchanted(final boolean enchanted) {
            this.enchanted = enchanted;
            return this;
        }

        public @NotNull Builder clickSound(final @Nullable GuiSound clickSound) {
            this.clickSound = clickSound;
            return this;
        }

        public @NotNull GuiItem build() {
            if (this.amount < 0) {
                throw new IllegalArgumentException("Item amount must be greater than or equal to 0");
            }

            return new GuiItem(
                    requireNonNull(this.type),
                    this.amount,
                    requireNonNull(this.name),
                    requireNonNull(this.lore),
                    requireNonNull(this.data),
                    requireNonNull(this.commands),
                    this.enchanted,
                    clickSound
            );
        }
    }

    public @NotNull GuiItem copy() {
        return new GuiItem(type,
                amount,
                name,
                List.copyOf(lore),
                data,
                List.copyOf(commands),
                enchanted,
                clickSound == null ? null : clickSound.copy());
    }
}