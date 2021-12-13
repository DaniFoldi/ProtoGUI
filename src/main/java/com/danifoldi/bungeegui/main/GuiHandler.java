package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.command.GuiCommand;
import com.danifoldi.bungeegui.gui.GuiAction;
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
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.SoundCategory;
import dev.simplix.protocolize.api.inventory.Inventory;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Singleton
public class GuiHandler {
    private final @NotNull Map<String, GuiGrid> menus = new ConcurrentHashMap<>();
    private final @NotNull Map<UUID, Pair<String, String>> openGuis = new ConcurrentHashMap<>();
    private final @NotNull Map<String, GuiCommand> commandHandlers = new ConcurrentHashMap<>();
    private final @NotNull List<GuiAction> guiActions = Collections.synchronizedList(new ArrayList<>());
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
        final @NotNull Config actions = config.get("actions");

        for (final @NotNull Config.Entry action: actions.entrySet()) {
            final @NotNull Config actionData = action.getValue();
            final @NotNull Config itemData = actionData.getOrElse("item", Config.inMemory());

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

            final @NotNull GuiAction guiAction = GuiAction.builder()
                    .server(actionData.getOrElse("server", ""))
                    .slot(actionData.getIntOrElse("slot", 1))
                    .guiItem(item)
                    .gui(actionData.getOrElse("gui", ""))
                    .build();

            guiActions.add(guiAction);
        }

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
            } catch (Exception e) {
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
        if (!gui.getCommandAliases().isEmpty()) {
            commandHandlers.put(name, new GuiCommand(name));
            pluginManager.registerCommand(plugin, commandHandlers.get(name));
        }
    }

    void removeGui(final @NotNull String name) {
        openGuis.entrySet().stream().filter(v -> v.getValue().getFirst().equals(name)).map(Map.Entry::getKey).collect(Collectors.toList()).forEach(p -> close(ProxyServer.getInstance().getPlayer(p), true));
        if (commandHandlers.get(name) != null) {
            pluginManager.unregisterCommand(commandHandlers.get(name));
            commandHandlers.remove(name);
        }
        menus.remove(name);
    }

    void actions(final @NotNull ProxiedPlayer player) {
        /*PlayerInventory inventory = InventoryManager.getInventory(player.getUniqueId());

        guiActions
                .stream()
                .filter(a -> a.getServer().equalsIgnoreCase(player.getServer().getInfo().getName()))
                .forEach(a -> inventory.setItem(a.getSlot(), a.getGuiItem().toItemStack(player, player.getName(), "")));

        inventory.update();*/
    }

    void interact(final @NotNull ProxiedPlayer player, final int slot) {
        /*Optional<GuiAction> action = guiActions
                .stream()
                .filter(a -> a.getServer().equalsIgnoreCase(player.getServer().getInfo().getName()))
                .filter(a -> a.getSlot() == slot)
                .findFirst();

        action.ifPresent(guiAction -> {
            if (guiAction.getGuiItem().getClickSound() != null) {
                guiAction.getGuiItem().getClickSound().playFor(player);
            }
            open(guiAction.getGui(), player, null);
        });*/
    }

    void open(final @NotNull String name, final @NotNull ProxiedPlayer player, final @Nullable String target) {
        logger.info("Opening gui " + name + " for player " + player.getName() + " (target: " + target + ")");

        final ProxiedPlayer placeholderTarget = menus.get(name).isRequireOnlineTarget() && menus.get(name).isPlaceholdersTarget() && ProxyServer.getInstance().getPlayer(target) != null ? ProxyServer.getInstance().getPlayer(target) : player;
        final GuiGrid gui = menus.get(name);
        final Inventory inventory = new Inventory(SlotUtil.getInventoryType(gui.getGuiSize())).title(Message.toComponent(placeholderTarget, gui.getTitle(), Pair.of("player", player.getName()), Pair.of("target", target)));

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

            inventory.item(guiItem.getKey(), guiItem.getValue().toItemStack(placeholderTarget, player.getName(), target));
        }

        inventory.onClick(event -> {
            //final @NotNull ProxiedPlayer player = event.player().handle();

            final @Nullable GuiGrid openGui = getOpenGui(player.getUniqueId());
            if (openGui == null) {
                return;
            }

            //final @NotNull Inventory inventory = event.inventory();
            final int slot = event.slot();

            if (inventory.type().equals(InventoryType.PLAYER)) {
                return;
            }
            if (slot == -999) {
                return;
            }

            if (inventory.item(slot) == null) {
                return;
            }

            if (openGui.getItems().get(slot).getClickSound() != null) {
                if (SoundUtil.isValidSound(openGui.getItems().get(slot).getClickSound().getSoundName())) {
                    logger.warning("Sound " + openGui.getItems().get(slot).getClickSound().getSoundName() + " is probably invalid");
                }
                openGui.getItems().get(slot).getClickSound().playFor(player);
            }

            if (openGui.getItems().get(slot).getCommands().isEmpty()) {
                return;
            }

            //final @NotNull String target = guiHandler.getGuiTarget(player.getUniqueId());

            runCommand(player,openGui, slot, target);
            close(player, true);
        });
        inventory.onClose(event -> close(event.player().handle(), false));

        Protocolize.playerProvider().player(player.getUniqueId()).openInventory(inventory);
        openGuis.put(player.getUniqueId(), Pair.of(name, target));
    }

    private final Pattern permissionPattern = Pattern.compile("^perm<(?<node>[\\w.]+)>");
    private final Pattern noPermissionPattern = Pattern.compile("^noperm<(?<node>[\\w.]+)>");

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

            @NotNull Pair<String, String> commandData = StringUtil.get(command);

            if (permissionPattern.matcher(commandData.getFirst()).matches()) {
                String node = permissionPattern.matcher(commandData.getFirst()).group("node");
                if (!player.hasPermission(node)) {
                    continue;
                }

                commandData = StringUtil.get(commandData.getSecond());
            } else if (noPermissionPattern.matcher(commandData.getFirst()).matches()) {
                String node = noPermissionPattern.matcher(commandData.getFirst()).group("node");
                if (player.hasPermission(node)) {
                    continue;
                }

                commandData = StringUtil.get(commandData.getSecond());
            }

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
        Protocolize.playerProvider().player(player.getUniqueId()).closeInventory();
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
