package hu.nugget.bungeegui.gui;

import de.exceptionflug.protocolize.items.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class GuiItem {
    private final ItemType type;
    private final int amount;
    private final String title;
    private final List<String> lore;
    private final Set<String> commands;

    private GuiItem(final @NotNull ItemType type,
                    final int amount,
                    final @NotNull String title,
                    final @NotNull List<String> lore,
                    final @NotNull Set<String> commands) {
        this.type = type;
        this.amount = amount;
        this.title = title;
        this.lore = lore;
        this.commands = commands;
    }

    public @NotNull ItemType getType() {
        return this.type;
    }

    public int getAmount() {
        return this.amount;
    }

    public @NotNull String getTitle() {
        return this.title;
    }

    public @NotNull List<String> getLore() {
        return List.copyOf(this.lore);
    }

    public @NotNull Set<String> getCommands() {
        return Set.copyOf(this.commands);
    }

    @Override
    public String toString() {
        return "GuiItem{type=" + this.type
                + ", amount=" + this.amount
                + ", title=" + this.title
                + ", lore=" + this.lore
                + ", commands=" + this.commands
                + '}';
    }

    @Override
    public boolean equals(final @Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GuiItem guiItem = (GuiItem) o;
        return this.amount == guiItem.amount
                && this.type == guiItem.type
                && Objects.equals(this.title, guiItem.title)
                && Objects.equals(this.lore, guiItem.lore)
                && Objects.equals(this.commands, guiItem.commands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.type, this.amount, this.title, this.lore, this.commands);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private ItemType type;
        private int amount;
        private String title;
        private List<String> lore;
        private Set<String> commands;

        private Builder() {}

        public @NotNull Builder type(final @NotNull ItemType type) {
            this.type = type;
            return this;
        }

        public @NotNull Builder amount(final int amount) {
            this.amount = amount;
            return this;
        }

        public @NotNull Builder title(final @NotNull String title) {
            this.title = title;
            return this;
        }

        public @NotNull Builder lore(final @NotNull List<String> lore) {
            this.lore = lore;
            return this;
        }

        public @NotNull Builder commands(final @NotNull Set<String> commands) {
            this.commands = commands;
            return this;
        }

        public @NotNull GuiItem build() {
            if (this.amount < 0) {
                throw new IllegalArgumentException("Item amount must be greater than 0");
            }

            return new GuiItem(
                    requireNonNull(this.type),
                    this.amount,
                    requireNonNull(this.title),
                    requireNonNull(this.lore),
                    this.commands == null ? Set.of() : this.commands
            );
        }
    }
}