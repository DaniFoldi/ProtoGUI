package com.danifoldi.bungeegui.gui;

import com.danifoldi.protogui.main.ProtoGuiAPI;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

@Deprecated(forRemoval = true)
public class GuiItem {
    private final @NotNull com.danifoldi.protogui.gui.GuiItem guiItem;

    private GuiItem(final @NotNull com.danifoldi.protogui.gui.GuiItem guiItem) {
        this.guiItem = guiItem;
    }

    @Deprecated(forRemoval = true)
    public @NotNull ItemType getType() {
        return guiItem.getType();
    }

    @Deprecated(forRemoval = true)
    public int getAmount() {
        return guiItem.getAmount();
    }

    @Deprecated(forRemoval = true)
    public @NotNull String getName() {
        return guiItem.getName();
    }

    @Deprecated(forRemoval = true)
    public @NotNull List<String> getLore() {
        return guiItem.getLore();
    }

    @Deprecated(forRemoval = true)
    public @NotNull String getData() {
        return guiItem.getData();
    }

    @Deprecated(forRemoval = true)
    public @NotNull List<String> getCommands() {
        return guiItem.getCommands();
    }

    @Deprecated(forRemoval = true)
    public boolean isEnchanted() {
        return guiItem.isEnchanted();
    }

    @Deprecated(forRemoval = true)
    public @Nullable GuiSound getClickSound() {
        return GuiSound.fromNew(guiItem.getClickSound());
    }

    @Deprecated(forRemoval = true)
    public @NotNull ItemStack toItemStack(ProxiedPlayer placeholderTarget, String player, String target) {
        return guiItem.toItemStack(ProtoGuiAPI.getInstance().getPlatform().getPlayer(placeholderTarget.getUniqueId()), player, target);
    }

    @Override
    @Deprecated(forRemoval = true)
    public String toString() {
        return "GuiItem{" +
                "guiItem=" + guiItem +
                '}';
    }

    @Override
    @Deprecated(forRemoval = true)
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuiItem guiItem1 = (GuiItem) o;
        return guiItem.equals(guiItem1.guiItem);
    }

    @Override
    @Deprecated(forRemoval = true)
    public int hashCode() {
        return Objects.hash(guiItem);
    }

    @Deprecated(forRemoval = true)
    public static @NotNull Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private @NotNull com.danifoldi.protogui.gui.GuiItem.Builder builder;

        private Builder() {
            builder = com.danifoldi.protogui.gui.GuiItem.builder();
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder type(final @NotNull ItemType type) {
            builder.type(type);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder amount(final int amount) {
            builder.amount(amount);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder title(final @NotNull String name) {
            builder.title(name);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder lore(final @NotNull List<String> lore) {
            builder.lore(lore);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder data(final @NotNull String data) {
            builder.data(data);
            return this;
        }

        public @NotNull Builder commands(final @NotNull List<String> commands) {
            builder.commands(commands);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder enchanted(final boolean enchanted) {
            builder.enchanted(enchanted);
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull Builder clickSound(final @Nullable GuiSound clickSound) {
            builder.clickSound(GuiSound.toNew(clickSound));
            return this;
        }

        @Deprecated(forRemoval = true)
        public @NotNull GuiItem build() {
            return new GuiItem(builder.build());
        }
    }

    @Deprecated(forRemoval = true)
    public @NotNull GuiItem copy() {
        return new GuiItem(guiItem);
    }

    @Deprecated(forRemoval = true)
    public static com.danifoldi.protogui.gui.GuiItem toNew(GuiItem old) {
        return old.guiItem.copy();
    }

    @Deprecated(forRemoval = true)
    public static GuiItem fromNew(com.danifoldi.protogui.gui.GuiItem n) {
        return new GuiItem(n.copy());
    }
}