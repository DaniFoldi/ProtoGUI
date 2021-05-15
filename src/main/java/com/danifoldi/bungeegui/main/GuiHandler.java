package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.command.BungeeGuiCommand;
import com.danifoldi.bungeegui.gui.GuiGrid;
import com.danifoldi.bungeegui.gui.GuiItem;
import com.danifoldi.bungeegui.gui.GuiSound;
import com.danifoldi.bungeegui.util.Message;
import com.danifoldi.bungeegui.util.Pair;
import com.danifoldi.bungeegui.util.StringUtil;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.EnumGetMethod;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.InventoryType;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import de.exceptionflug.protocolize.world.Sound;
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
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Singleton
public class GuiHandler {
    private final Map<String, GuiGrid> menus = new HashMap<>();
    private final Map<UUID, Pair<String, String>> openGuis = new HashMap<>();
    private final Logger logger;
    private final PluginManager pluginManager;
    private final BungeeGuiPlugin plugin;

    @Inject
    GuiHandler(final @NotNull Logger logger,
                      final @NotNull PluginManager pluginManager,
                      final @NotNull BungeeGuiPlugin plugin) {
        this.logger = logger;
        this.pluginManager = pluginManager;
        this.plugin = plugin;
    }

    @NotNull
    GuiGrid getGui(final @NotNull String name) {
        return this.menus.get(name);
    }

    void load(Config config) {
        menus.clear();

        final Config guis = config.get("guis");

        for (Config.Entry gui: guis.entrySet()) {
            final String name = gui.getKey();
            final Config guiData = gui.getValue();
            final Config guiItems = guiData.getOrElse("items", Config.inMemory());
            final Map<Integer, GuiItem> itemMap = new HashMap<>();

            for (Config.Entry guiItem: guiItems.entrySet()) {
                final int slot = Integer.parseInt(guiItem.getKey());
                final Config itemData = guiItem.getValue();

                GuiSound clickSound = null;

                if (guiData.contains("clickSound")) {
                    clickSound = GuiSound.builder()
                            .sound(guiData.getEnumOrElse("openSound.sound", Sound.ENTITY_VILLAGER_NO, EnumGetMethod.NAME_IGNORECASE))
                            .soundCategory(guiData.getEnumOrElse("openSound.soundCategory", SoundCategory.MASTER, EnumGetMethod.NAME_IGNORECASE))
                            .volume(guiData.getOrElse("openSound.volume", 1.0f))
                            .pitch(guiData.getOrElse("openSound.pitch", 1.0f))
                            .build();
                }

                GuiItem item = GuiItem.builder()
                        .type(itemData.getEnumOrElse("type", ItemType.STONE, EnumGetMethod.NAME_IGNORECASE))
                        .amount(itemData.getOrElse("count", 1))
                        .title(itemData.getOrElse("name", ""))
                        .lore(itemData.getOrElse("lore", Collections.emptyList()))
                        .data(itemData.getOrElse("data", ""))
                        .commands(itemData.getOrElse("commands", Collections.emptyList()))
                        .enchanted(itemData.getOrElse("enchanted", false))
                        .clickSound(clickSound)
                        .build();

                itemMap.put(slot, item);
            }

            GuiSound openSound = null;

            if (guiData.contains("openSound")) {
                openSound = GuiSound.builder()
                        .sound(guiData.getEnumOrElse("openSound.sound", Sound.ENTITY_VILLAGER_NO, EnumGetMethod.NAME_IGNORECASE))
                        .soundCategory(guiData.getEnumOrElse("openSound.soundCategory", SoundCategory.MASTER, EnumGetMethod.NAME_IGNORECASE))
                        .volume(guiData.getOrElse("openSound.volume", 1.0f))
                        .pitch(guiData.getOrElse("openSound.pitch", 1.0f))
                        .build();
            }

            final GuiGrid grid = GuiGrid.builder()
                    .items(itemMap)
                    .targeted(guiData.getOrElse("targeted", false))
                    .commands(guiData.getOrElse("aliases", List.of(name.toLowerCase(Locale.ROOT))).stream().map(String::toLowerCase).collect(Collectors.toList()))
                    .permssion(guiData.getOrElse("permission", "bungeegui.gui." + name.toLowerCase(Locale.ROOT).replace("{", "").replace("}", "").replace(" ", "")))
                    .size(guiData.getIntOrElse("size", 54))
                    .title(guiData.getOrElse("title", "GUI " + name.toLowerCase(Locale.ROOT)))
                    .selfTarget(guiData.getOrElse("selfTarget", true))
                    .ignoreVanished(guiData.getOrElse("ignoreVanished", true))
                    .requireOnlineTarget(guiData.getOrElse("requireOnlineTarget", false))
                    .whitelistServers(guiData.getOrElse("whitelist", List.of("*")))
                    .blacklistServers(guiData.getOrElse("blacklist", Collections.emptyList()))
                    .placeholdersTarget(guiData.getOrElse("placeholdersTarget", false))
                    .openSound(openSound)
                    .build();

            menus.put(name, grid);
        }
    }

    private InventoryType getInventoryType(int size) {
        if (size <= 9) {
            return InventoryType.GENERIC_9X1;
        } else if (size <= 2 * 9) {
            return InventoryType.GENERIC_9X2;
        } else if (size <= 3 * 9) {
            return InventoryType.GENERIC_9X3;
        } else if (size <= 4 * 9) {
            return InventoryType.GENERIC_9X4;
        } else if (size <= 5 * 9) {
            return InventoryType.GENERIC_9X5;
        } else {
            return InventoryType.GENERIC_9X6;
        }
    }

    void registerCommands() {
        for (String name: menus.keySet()) {
            pluginManager.registerCommand(plugin, new BungeeGuiCommand(name));
        }
    }

    void open(String name, ProxiedPlayer player, @Nullable String target) {
        logger.info("Opening gui " + name + " for player " + player.getName() + " (target: " + target + ")");

        final ProxiedPlayer placeholderTarget = menus.get(name).isRequireOnlineTarget() && menus.get(name).isPlaceholdersTarget() && ProxyServer.getInstance().getPlayer(target) != null ? ProxyServer.getInstance().getPlayer(target) : player;
        final GuiGrid gui = menus.get(name);
        final Inventory inventory = new Inventory(getInventoryType(gui.getGuiSize()), Message.toComponent(placeholderTarget, gui.getTitle(), Pair.of("player", player.getName()), Pair.of("target", target)));

        if (gui.getOpenSound() != null) {
            gui.getOpenSound().playFor(player);
        }

        for (Map.Entry<Integer, GuiItem> guiItem: gui.getItems().entrySet()) {
            final ItemStack item = new ItemStack(guiItem.getValue().getType());
            item.setAmount((byte)guiItem.getValue().getAmount());
            item.setDisplayName(Message.toComponent(placeholderTarget, guiItem.getValue().getName(), Pair.of("player", player.getName()), Pair.of("target", target)));
            item.setLoreComponents(guiItem.getValue().getLore().stream().map(l -> Message.toComponent(placeholderTarget, l, Pair.of("player", player.getName()), Pair.of("target", target))).collect(Collectors.toList()));

            if (item.isPlayerSkull()) {
                final Pair<String, String> data = StringUtil.get(guiItem.getValue().getData());
                if (data.getFirst().equalsIgnoreCase("owner")) {
                    item.setSkullOwner(data.getSecond());
                } else if (data.getFirst().equalsIgnoreCase("texture")) {
                    item.setSkullTexture(data.getSecond());
                }
            }

            CompoundTag tag = (CompoundTag)item.getNBTTag();

            if (guiItem.getValue().isEnchanted()) {
                ListTag<CompoundTag> enchantments = new ListTag<>(CompoundTag.class);
                CompoundTag enchantment = new CompoundTag();
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

    void runCommand(ProxiedPlayer player, GuiGrid openGui, int slot, String target) {
        logger.info("Running commands for player " + player.getName() + " slot " + slot + " with target " + target);

        final GuiItem item = openGui.getItems().get(slot);
        if (item == null) {
            return;
        }

        for (String command: item.getCommands()) {
            if (command.equals("")) {
                continue;
            }

            Pair<String, String> commandData = StringUtil.get(command);
            if (commandData.getFirst().equalsIgnoreCase("console")) {
                pluginManager.dispatchCommand(ProxyServer.getInstance().getConsole(), Message.replace(command, Pair.of("player", player.getName()), Pair.of("target", target)));
                continue;
            }

            pluginManager.dispatchCommand(player, Message.replace(command, Pair.of("player", player.getName()), Pair.of("target", target)));
        }
    }

    void close(ProxiedPlayer player) {
        if (!openGuis.containsKey(player.getUniqueId())) {
            return;
        }

        logger.info("Removing gui from cache for " + player.getName());

        openGuis.remove(player.getUniqueId());
        InventoryModule.closeAllInventories(player);
    }

    GuiGrid getOpenGui(UUID uuid) {
        if (!openGuis.containsKey(uuid)) {
            return null;
        }

        return menus.get(openGuis.get(uuid).getFirst());
    }
    String getGuiTarget(UUID uuid) {
        return openGuis.get(uuid).getSecond();
    }

    String getGuiName(GuiGrid gui) {
        // TODO this needs to be improved
        for (Map.Entry<String, GuiGrid> menu: menus.entrySet()) {
            if (menu.getValue() != gui) {
                continue;
            }

            return menu.getKey();
        }

        return "";
    }

    List<String> getGuis() {
        return new ArrayList<>(menus.keySet());
    }
}
