package com.danifoldi.protogui.gui;

import com.danifoldi.protogui.platform.PlatformInteraction;
import com.danifoldi.protogui.util.Message;
import com.danifoldi.protogui.util.Pair;
import com.danifoldi.protogui.util.StringUtil;
import dev.simplix.protocolize.api.item.ItemStack;
import dev.simplix.protocolize.data.ItemType;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.ShortTag;
import net.querz.nbt.tag.StringTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@SuppressWarnings("ClassCanBeRecord")
public class GuiItem {
    private final @NotNull ItemType type;
    private final int amount;
    private final @NotNull String name;
    private final @NotNull List<String> lore;
    private final @NotNull String data;
    private final @NotNull List<String> commands;
    private final @NotNull List<String> rightCommands;
    private final @NotNull List<String> leftCommands;
    private final boolean enchanted;
    private final @Nullable GuiSound clickSound;
    private final @Nullable String clickableIf;
    private final @Nullable String shownIf;

    private GuiItem(final @NotNull ItemType type,
                    final int amount,
                    final @NotNull String name,
                    final @NotNull List<String> lore,
                    final @NotNull String data,
                    final @NotNull List<String> commands,
                    final @NotNull List<String> rightCommands,
                    final @NotNull List<String> leftCommands,
                    final boolean enchanted,
                    final @Nullable GuiSound clickSound,
                    final @Nullable String clickableIf,
                    final @Nullable String shownIf) {
        this.type = type;
        this.amount = amount;
        this.name = name;
        this.lore = lore;
        this.data = data;
        this.commands = commands;
        this.rightCommands = rightCommands;
        this.leftCommands = leftCommands;
        this.enchanted = enchanted;
        this.clickSound = clickSound;
        this.clickableIf = clickableIf;
        this.shownIf = shownIf;
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

    public @NotNull List<String> getRightCommands() {
        return List.copyOf(this.rightCommands);
    }
    public @NotNull List<String> getLeftCommands() {
        return List.copyOf(this.leftCommands);
    }

    public boolean isEnchanted() {
        return this.enchanted;
    }

    public @Nullable GuiSound getClickSound() {
        return this.clickSound;
    }

    public @Nullable String getClickableIf() {
        return this.clickableIf;
    }

    public @Nullable String getShownIf() {
        return this.shownIf;
    }

    public @NotNull ItemStack toItemStack(PlatformInteraction.ProtoPlayer placeholderTarget, String player, String target) {
        final @NotNull ItemStack item = new ItemStack(this.getType());
        item
                .amount((byte)this.getAmount())
                .displayName(Message.process(placeholderTarget, this.getName(), Pair.of("player", player), Pair.of("target", target)))
                .lore(this.getLore().stream().map(l -> Message.process(placeholderTarget, l, Pair.of("player", player), Pair.of("target", target))).collect(Collectors.toList()), true);

        if (item.itemType().equals(ItemType.PLAYER_HEAD)) {
            final Pair<String, String> data = StringUtil.get(this.getData());
            if (data.getFirst().equalsIgnoreCase("owner")) {
                final @NotNull CompoundTag tag = item.nbtData();
                tag.put("SkullOwner", new StringTag(Message.replace(data.getSecond(), Pair.of("player", player), Pair.of("target", target))));
                item.nbtData(tag);
            } else if (data.getFirst().equalsIgnoreCase("texture")) {
                final @NotNull CompoundTag tag = item.nbtData();
                @Nullable CompoundTag skullOwnerTag = tag.getCompoundTag("SkullOwner");
                @Nullable CompoundTag propertiesTag = tag.getCompoundTag("Properties");
                final @NotNull ListTag<@NotNull CompoundTag> texturesTag = new ListTag<>(CompoundTag.class);
                final @NotNull CompoundTag textureTag = new CompoundTag();

                if (skullOwnerTag == null) {
                    skullOwnerTag = new CompoundTag();
                }
                if (propertiesTag == null) {
                    propertiesTag = new CompoundTag();
                }

                textureTag.put("Value", new StringTag(Message.replace(data.getSecond(), Pair.of("player", player), Pair.of("target", target))));
                texturesTag.add(textureTag);
                propertiesTag.put("textures", texturesTag);
                skullOwnerTag.put("Properties", propertiesTag);
                skullOwnerTag.put("Name", new StringTag(Message.replace(data.getSecond(), Pair.of("player", player), Pair.of("target", target))));

                tag.put("SkullOwner", skullOwnerTag);
                item.nbtData(tag);
            }
        }

        final @NotNull CompoundTag tag = item.nbtData();

        if (this.isEnchanted()) {
            final @NotNull ListTag<CompoundTag> enchantments = new ListTag<>(CompoundTag.class);
            final @NotNull CompoundTag enchantment = new CompoundTag();
            enchantment.put("id", new StringTag("minecraft:unbreaking"));
            enchantment.put("lvl", new ShortTag((short)1));
            enchantments.add(enchantment);
            tag.put("Enchantments", enchantments);
        }

        tag.put("HideFlags", new IntTag(99));
        tag.put("overrideMeta", new ByteTag((byte)1));

        return item;
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
        private @Nullable List<String> rightCommands;
        private @Nullable List<String> leftCommands;
        private boolean enchanted;
        private @Nullable GuiSound clickSound;
        private @Nullable String clickableIf;
        private @Nullable String shownIf;

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

        public @NotNull Builder rightCommands(final @NotNull List<String> rightCommands) {
            this.rightCommands = rightCommands;
            return this;
        }

        public @NotNull Builder leftCommands(final @NotNull List<String> leftCommands) {
            this.leftCommands = leftCommands;
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

        public @NotNull Builder clickableIf(final @Nullable String clickableIf) {
            this.clickableIf = clickableIf;
            return this;
        }

        public @NotNull Builder shownIf(final @Nullable String shownIf) {
            this.shownIf = shownIf;
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
                    requireNonNull(this.rightCommands),
                    requireNonNull(this.leftCommands),
                    this.enchanted,
                    clickSound,
                    clickableIf,
                    shownIf
            );
        }
    }

    @Override
    public String toString() {
        return "GuiItem{" +
                "type=" + type +
                ", amount=" + amount +
                ", name='" + name + '\'' +
                ", lore=" + lore +
                ", data='" + data + '\'' +
                ", commands=" + commands +
                ", rightCommands=" + rightCommands +
                ", leftCommands=" + leftCommands +
                ", enchanted=" + enchanted +
                ", clickSound=" + clickSound +
                ", clickableIf='" + clickableIf + '\'' +
                ", shownIf='" + shownIf + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GuiItem guiItem = (GuiItem) o;
        return amount == guiItem.amount && enchanted == guiItem.enchanted && type == guiItem.type && name.equals(guiItem.name) && lore.equals(guiItem.lore) && data.equals(guiItem.data) && commands.equals(guiItem.commands) && rightCommands.equals(guiItem.rightCommands) && leftCommands.equals(guiItem.leftCommands) && Objects.equals(clickSound, guiItem.clickSound) && Objects.equals(clickableIf, guiItem.clickableIf) && Objects.equals(shownIf, guiItem.shownIf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, amount, name, lore, data, commands, rightCommands, leftCommands, enchanted, clickSound, clickableIf, shownIf);
    }

    public @NotNull GuiItem copy() {
        return new GuiItem(type, amount, name, lore, data, commands, rightCommands, leftCommands, enchanted, clickSound, clickableIf, shownIf);
    }
}