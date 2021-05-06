package com.danifoldi.bungeegui.gui;

import de.exceptionflug.protocolize.items.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class GuiItem {
    private final ItemType type;
    private final int amount;
    private final String name;
    private final List<String> lore;
    private final String data;
    private final Set<String> commands;
    private final Map<String, Integer> enchantments;

    protected GuiItem(final @NotNull ItemType type,
                    final int amount,
                    final @NotNull String name,
                    final @NotNull List<String> lore,
                    final @NotNull String data,
                    final @NotNull Set<String> commands,
                    final @NotNull Map<String, Integer> enchantments) {
        this.type = type;
        this.amount = amount;
        this.name = name;
        this.lore = lore;
        this.data = data;
        this.commands = commands;
        this.enchantments = enchantments;
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

    public @NotNull Set<String> getCommands() {
        return Set.copyOf(this.commands);
    }

    // TODO add enchantments to items
    public @NotNull Map<String, Integer> getEnchantments() {
        return this.enchantments;
    }

    @Override
    public String toString() {
        return "GuiItem{type=" + this.type
                + ", amount=" + this.amount
                + ", name=" + this.name
                + ", lore=" + this.lore
                + ", data=" + this.data
                + ", commands=" + this.commands
                + ", enchantments=" + this.enchantments
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
                && Objects.equals(this.enchantments, guiItem.enchantments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.amount, this.name, this.lore, this.data, this.commands, this.enchantments);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private ItemType type;
        private int amount;
        private String name;
        private List<String> lore;
        private String data;
        private Set<String> commands;
        private Map<String, Integer> enchantments;

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

        public @NotNull Builder commands(final @NotNull Set<String> commands) {
            this.commands = commands;
            return this;
        }

        public @NotNull Builder enchantments(final @NotNull Map<String, Integer> enchantments) {
            this.enchantments = enchantments;
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
                    this.commands == null ? Set.of() : this.commands,
                    requireNonNull(this.enchantments)
            );
        }
    }
}