package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.command.BungeeGuiCommand;
import com.danifoldi.bungeegui.gui.GuiGrid;
import com.danifoldi.bungeegui.gui.GuiItem;
import com.danifoldi.bungeegui.gui.GuiSound;
import com.danifoldi.bungeegui.util.Message;
import com.danifoldi.bungeegui.util.Pair;
import com.danifoldi.bungeegui.util.SlotUtil;
import com.danifoldi.bungeegui.util.SoundUtil;
import com.danifoldi.bungeegui.util.StringUtil;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.EnumGetMethod;
import dagger.Module;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import de.exceptionflug.protocolize.world.SoundCategory;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;
import net.querz.nbt.tag.ByteTag;
import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.IntTag;
import net.querz.nbt.tag.ListTag;
import net.querz.nbt.tag.ShortTag;
import net.querz.nbt.tag.StringTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class GuiHandler {
    private final @NotNull Map<String, GuiGrid> menus = new HashMap<>();
    private final @NotNull Map<UUID, Pair<String, String>> openGuis = new HashMap<>();
    private final @NotNull Map<String, BungeeGuiCommand> commandHandlers = new HashMap<>();
    private final @NotNull Logger logger;
    private final @NotNull PluginManager pluginManager;
    private final @NotNull BungeeGuiPlugin plugin;

    @Inject
    GuiHandler(final @NotNull Logger logger,
               final @NotNull PluginManager pluginManager,
               final @NotNull BungeeGuiPlugin plugin) {
        this.logger = logger;
        this.pluginManager = pluginManager;
        this.plugin = plugin;
    }

    @NotNull GuiGrid getGui(final @NotNull String name) {
        return this.menus.get(name);
    }

    void load(@NotNull Config config) {
        final @NotNull Config guis = config.get("guis");

        for (final @NotNull Config.Entry gui: guis.entrySet()) {
            final @NotNull String name = gui.getKey();
            final @NotNull Config guiData = gui.getValue();
            final @NotNull Config guiItems = guiData.getOrElse("items", Config.inMemory());
            final @NotNull Map<Integer, GuiItem> itemMap = new HashMap<>();
            final int size = guiData.getIntOrElse("size", 54);

            try {
                for (final @NotNull Config.Entry guiItem : guiItems.entrySet()) {
                    final @NotNull Set<Integer> slots = SlotUtil.getSlots(guiItem.getKey(), SlotUtil.getInventorySize(SlotUtil.getInventoryType(size)));
                    final @NotNull Config itemData = guiItem.getValue();

                    @Nullable GuiSound clickSound = null;

                    if (itemData.contains("clickSound")) {
                        clickSound = GuiSound.builder()
                                .soundName(itemData.getOrElse("clickSound.sound", "entity_villager_no"))
                                .soundCategory(itemData.getEnumOrElse("clickSound.soundCategory", SoundCategory.MASTER, EnumGetMethod.NAME_IGNORECASE))
                                .volume(itemData.getOrElse("clickSound.volume", 1.0d).floatValue())
                                .pitch(itemData.getOrElse("clickSound.pitch", 1.0d).floatValue())
                                .build();
                    }

                    final @NotNull GuiItem item = GuiItem.builder()
                            .type(itemData.getEnumOrElse("type", ItemType.STONE, EnumGetMethod.NAME_IGNORECASE))
                            .amount(itemData.getOrElse("count", 1))
                            .title(itemData.getOrElse("name", ""))
                            .lore(itemData.getOrElse("lore", Collections.emptyList()))
                            .data(itemData.getOrElse("data", ""))
                            .commands(itemData.getOrElse("commands", Collections.emptyList()))
                            .enchanted(itemData.getOrElse("enchanted", false))
                            .clickSound(clickSound)
                            .build();

                    for (final int slot : slots) {
                        itemMap.put(slot, item.copy());
                    }
                }

                @Nullable GuiSound openSound = null;

                if (guiData.contains("openSound")) {
                    openSound = GuiSound.builder()
                            .soundName(guiData.getOrElse("openSound.sound", "entity_villager_no"))
                            .soundCategory(guiData.getEnumOrElse("openSound.soundCategory", SoundCategory.MASTER, EnumGetMethod.NAME_IGNORECASE))
                            .volume(guiData.getOrElse("openSound.volume", 1.0d).floatValue())
                            .pitch(guiData.getOrElse("openSound.pitch", 1.0d).floatValue())
                            .build();
                }

                final @NotNull GuiGrid grid = GuiGrid.builder()
                        .items(itemMap)
                        .targeted(guiData.getOrElse("targeted", false))
                        .commands(guiData.getOrElse("aliases", List.of(name.toLowerCase(Locale.ROOT))).stream().map(String::toLowerCase).collect(Collectors.toList()))
                        .permission(guiData.getOrElse("permission", "bungeegui.gui." + name.toLowerCase(Locale.ROOT).replace("{", "").replace("}", "").replace(" ", "")))
                        .size(size)
                        .title(guiData.getOrElse("title", "GUI " + name.toLowerCase(Locale.ROOT)))
                        .selfTarget(guiData.getOrElse("selfTarget", true))
                        .ignoreVanished(guiData.getOrElse("ignoreVanished", true))
                        .requireOnlineTarget(guiData.getOrElse("requireOnlineTarget", false))
                        .whitelistServers(guiData.getOrElse("whitelist", List.of("*")))
                        .blacklistServers(guiData.getOrElse("blacklist", Collections.emptyList()))
                        .placeholdersTarget(guiData.getOrElse("placeholdersTarget", false))
                        .openSound(openSound)
                        .targetBypass(guiData.getOrElse("targetBypass", false))
                        .closeable(guiData.getOrElse("closeable", true))
                        .notifyTarget(guiData.getOrElse("notifyTarget", ""))
                        .build();

                addGui(name, grid);
            } catch (final @NotNull Exception e) {
                logger.warning("Could not load gui " + name);
                logger.warning(e.getClass().getName() + ":  " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    void addGui(final @NotNull String name, final @NotNull GuiGrid gui) {
        if (menus.containsKey(name)) {
            return;
        }
        menus.put(name, gui);
        commandHandlers.put(name, new BungeeGuiCommand(name));
        pluginManager.registerCommand(plugin, commandHandlers.get(name));
    }

    void removeGui(final @NotNull String name) {
        openGuis.entrySet().stream().filter(v -> v.getValue().getFirst().equals(name)).map(Map.Entry::getKey).collect(Collectors.toList()).forEach(p -> close(ProxyServer.getInstance().getPlayer(p), true));
        pluginManager.unregisterCommand(commandHandlers.get(name));
        commandHandlers.remove(name);
        menus.remove(name);
    }

    void open(final @NotNull String name, final @NotNull ProxiedPlayer player, final @Nullable String target) {
        logger.info("Opening gui " + name + " for player " + player.getName() + " (target: " + target + ")");

        final ProxiedPlayer placeholderTarget = menus.get(name).isRequireOnlineTarget() && menus.get(name).isPlaceholdersTarget() && ProxyServer.getInstance().getPlayer(target) != null ? ProxyServer.getInstance().getPlayer(target) : player;
        final GuiGrid gui = menus.get(name);
        final Inventory inventory = new Inventory(SlotUtil.getInventoryType(gui.getGuiSize()), Message.toComponent(placeholderTarget, gui.getTitle(), Pair.of("player", player.getName()), Pair.of("target", target)));

        if (gui.getOpenSound() != null) {
            if (SoundUtil.isValidSound(gui.getOpenSound().getSoundName())) {
                logger.warning("Sound " + gui.getOpenSound().getSoundName() + " is probably invalid");
            }
            gui.getOpenSound().playFor(player);
        }

        if (ProxyServer.getInstance().getPlayer(target) != null && !gui.getNotifyTarget().equals("")) {
            ProxyServer.getInstance().getPlayer(target).sendMessage(Message.toComponent(ProxyServer.getInstance().getPlayer(target), gui.getNotifyTarget(), Pair.of("player", player.getName()), Pair.of("target", target)));
        }

        for (final @NotNull Map.Entry<Integer, GuiItem> guiItem: gui.getItems().entrySet()) {
            if (guiItem.getKey() < 0 || guiItem.getKey() >= SlotUtil.getInventorySize(SlotUtil.getInventoryType(gui.getGuiSize()))) {
                logger.warning("GUI " + name + " contains an item at slot " + guiItem.getKey() + " which is outside of the Inventory");
                continue;
            }

            final @NotNull ItemStack item = new ItemStack(guiItem.getValue().getType());
            item.setAmount((byte)guiItem.getValue().getAmount());
            item.setDisplayName(Message.toComponent(placeholderTarget, guiItem.getValue().getName(), Pair.of("player", player.getName()), Pair.of("target", target)));
            item.setLoreComponents(guiItem.getValue().getLore().stream().map(l -> Message.toComponent(placeholderTarget, l, Pair.of("player", player.getName()), Pair.of("target", target))).collect(Collectors.toList()));

            if (item.isPlayerSkull()) {
                final Pair<String, String> data = StringUtil.get(guiItem.getValue().getData());
                if (data.getFirst().equalsIgnoreCase("owner")) {
                    item.setSkullOwner(Message.replace(data.getSecond(), Pair.of("player", player.getName()), Pair.of("target", target)));
                } else if (data.getFirst().equalsIgnoreCase("texture")) {
                    item.setSkullTexture(Message.replace(data.getSecond(), Pair.of("player", player.getName()), Pair.of("target", target)));
                }
            }

            final @NotNull CompoundTag tag = (CompoundTag)item.getNBTTag();

            if (guiItem.getValue().isEnchanted()) {
                final @NotNull ListTag<CompoundTag> enchantments = new ListTag<>(CompoundTag.class);
                final @NotNull CompoundTag enchantment = new CompoundTag();
                enchantment.put("id", new StringTag("minecraft:unbreaking"));
                enchantment.put("lvl", new ShortTag((short)1));
                enchantments.add(enchantment);
                tag.put("Enchantments", enchantments);
            }

            tag.put("HideFlags", new IntTag(99));
            tag.put("overrideMeta", new ByteTag((byte)1));

            inventory.setItem(guiItem.getKey(), item);
        }

        InventoryModule.sendInventory(player, inventory);
        openGuis.put(player.getUniqueId(), Pair.of(name, target));
    }

    void runCommand(final @NotNull ProxiedPlayer player, final @NotNull GuiGrid openGui, final int slot, final @NotNull String target) {
        logger.info("Running commands for player " + player.getName() + " slot " + slot + " with target " + target);

        final @Nullable GuiItem item = openGui.getItems().get(slot);
        if (item == null) {
            return;
        }

        for (final @NotNull String command: item.getCommands()) {
            if (command.equals("")) {
                continue;
            }

            final @NotNull Pair<String, String> commandData = StringUtil.get(command);
            if (commandData.getFirst().equalsIgnoreCase("console")) {
                pluginManager.dispatchCommand(ProxyServer.getInstance().getConsole(), Message.replace(command, Pair.of("player", player.getName()), Pair.of("target", target)));
                continue;
            }

            pluginManager.dispatchCommand(player, Message.replace(command, Pair.of("player", player.getName()), Pair.of("target", target)));
        }
    }

    void close(final @NotNull ProxiedPlayer player, final boolean didClick) {
        if (!openGuis.containsKey(player.getUniqueId())) {
            return;
        }

        if (!menus.get(openGuis.get(player.getUniqueId()).getFirst()).isCloseable() && !didClick) {
            open(openGuis.get(player.getUniqueId()).getFirst(), player, openGuis.get(player.getUniqueId()).getSecond());
            return;
        }

        logger.info("Removing gui from cache for " + player.getName());

        openGuis.remove(player.getUniqueId());
        InventoryModule.closeAllInventories(player);
    }

    @Nullable GuiGrid getOpenGui(final @NotNull UUID uuid) {
        if (!openGuis.containsKey(uuid)) {
            return null;
        }

        return menus.get(openGuis.get(uuid).getFirst());
    }
    @Nullable String getGuiTarget(final @NotNull UUID uuid) {
        return openGuis.get(uuid).getSecond();
    }

    @NotNull String getGuiName(final @NotNull GuiGrid gui) {
        return menus.entrySet().stream().filter(m -> m.getValue().equals(gui)).map(Map.Entry::getKey).findFirst().orElse("");
    }

    @NotNull List<String> getGuis() {
        return new ArrayList<>(menus.keySet());
    }
}
