package com.danifoldi.protogui.main;

import com.danifoldi.protogui.command.GuiCommand;
import com.danifoldi.protogui.gui.GuiAction;
import com.danifoldi.protogui.gui.GuiGrid;
import com.danifoldi.protogui.gui.GuiItem;
import com.danifoldi.protogui.gui.GuiSound;
import com.danifoldi.protogui.util.ListUtil;
import com.danifoldi.protogui.util.Message;
import com.danifoldi.protogui.util.Pair;
import com.danifoldi.protogui.util.SlotUtil;
import com.danifoldi.protogui.util.SoundUtil;
import com.danifoldi.protogui.util.StringUtil;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.file.FileConfig;
import dev.simplix.protocolize.api.ClickType;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class GuiHandler {
    private final @NotNull Map<String, GuiGrid> templates = new ConcurrentHashMap<>();
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

    void load(@NotNull Path datafolder) throws IOException {
        try (Stream<Path> stream = Files.list(datafolder.resolve("actions"))) {
            stream
                    .filter(p -> !Files.isDirectory(p))
                    .filter(p -> p.getFileName().endsWith(".yml"))
                    .forEach(p -> {
                        try {
                            FileConfig f = FileConfig.of(p);
                            f.load();
                            String actionName = p.getFileName().toString().replace(".yml", "");
                            logger.info("Loading action %s".formatted(actionName));
                            guiActions.add(loadAction(f));
                        } catch (Exception e) {
                            logger.warning("Failed to load action %s".formatted(p.getFileName()));
                        }
                    });
        }

        try (Stream<Path> stream = Files.list(datafolder.resolve("templates"))) {
            stream
                    .filter(p -> !Files.isDirectory(p))
                    .filter(p -> p.getFileName().endsWith(".yml"))
                    .forEach(p -> {
                        try {
                            FileConfig f = FileConfig.of(p);
                            f.load();
                            String templateName = p.getFileName().toString().replace(".yml", "");
                            logger.info("Loading template %s".formatted(templateName));
                            templates.put(templateName, loadGuiGrid(f, templateName));
                        } catch (Exception e) {
                            logger.warning("Failed to load template %s".formatted(p.getFileName()));
                        }
                    });
        }

        try (Stream<Path> stream = Files.list(datafolder.resolve("guis"))) {
            stream
                    .filter(p -> !Files.isDirectory(p))
                    .filter(p -> p.getFileName().endsWith(".yml") || p.getFileName().endsWith(".yaml"))
                    .forEach(p -> {
                        try {
                            FileConfig f = FileConfig.of(p);
                            f.load();
                            String guiName = p.getFileName().toString().replace(".yml", "");
                            logger.info("Loading gui %s".formatted(guiName));
                            GuiGrid templateGui = templates.get(f.getOrElse("template", ""));
                            addGui(guiName, templateGui == null ? loadGuiGrid(f, guiName) : loadGuiGrid(f, guiName, templateGui));
                        } catch (Exception e) {
                            logger.warning("Failed to load gui %s".formatted(p.getFileName()));
                        }
                    });
        }
    }

    void unload() {
        guiActions.clear();
        templates.clear();
        for (String gui: getGuis()) {
            removeGui(gui);
        }
    }

    Map<Integer, GuiItem> loadItemMap(Config guiItems, int size) {
        Map<Integer, GuiItem> itemMap = new ConcurrentHashMap<>();

        for (final @NotNull Config.Entry guiItem : guiItems.entrySet()) {
            final @NotNull Set<Integer> slots = SlotUtil.getSlots(guiItem.getKey(), SlotUtil.getInventorySize(SlotUtil.getInventoryType(size)));
            final @NotNull Config itemData = guiItem.getValue();

            GuiItem item = loadItem(itemData);
            for (final int slot : slots) {
                itemMap.put(slot, item.copy());
            }
        }

        return itemMap;
    }

    Map<Integer, GuiItem> loadItemMap(Config guiItems, int size, Map<Integer, GuiItem> fallback) {
        Map<Integer, GuiItem> itemMap = loadItemMap(guiItems, size);

        fallback.forEach(itemMap::putIfAbsent);

        return itemMap;
    }

    GuiGrid loadGuiGrid(Config gui, String name) {
        int size = gui.getIntOrElse("size", 54);

        return GuiGrid.builder()
                .items(loadItemMap(gui.getOrElse("items", Config.inMemory()), size))
                .targeted(gui.getOrElse("targeted", false))
                .commands(gui.getOrElse("aliases", List.of(name.toLowerCase(Locale.ROOT))).stream().map(String::toLowerCase).collect(Collectors.toList()))
                .permission(gui.getOrElse("permission", "bungeegui.gui." + name.toLowerCase(Locale.ROOT).replace("{", "").replace("}", "").replace(" ", "")))
                .size(size)
                .title(gui.getOrElse("title", "GUI " + name.toLowerCase(Locale.ROOT)))
                .selfTarget(gui.getOrElse("selfTarget", true))
                .ignoreVanished(gui.getOrElse("ignoreVanished", true))
                .requireOnlineTarget(gui.getOrElse("requireOnlineTarget", false))
                .whitelistServers(gui.getOrElse("whitelist", List.of("*")))
                .blacklistServers(gui.getOrElse("blacklist", Collections.emptyList()))
                .placeholdersTarget(gui.getOrElse("placeholdersTarget", false))
                .openSound(loadOpenSound(gui))
                .targetBypass(gui.getOrElse("targetBypass", false))
                .closeable(gui.getOrElse("closeable", true))
                .notifyTarget(gui.getOrElse("notifyTarget", ""))
                .build();
    }

    GuiGrid loadGuiGrid(Config gui, String name, GuiGrid template) {
        int size = gui.getIntOrElse("size", template.getGuiSize());

        return GuiGrid.builder()
                .items(loadItemMap(gui.get("items"), size, template.getItems()))
                .targeted(gui.getOrElse("targeted", template.isTargeted()))
                .commands(gui.getOrElse("aliases", template.getCommandAliases()))
                .permission(gui.getOrElse("permission", template.getPermission()))
                .size(size)
                .title(gui.getOrElse("title", template.getTitle()))
                .selfTarget(gui.getOrElse("selfTarget", template.isSelfTarget()))
                .ignoreVanished(gui.getOrElse("ignoreVanished", template.isIgnoreVanished()))
                .requireOnlineTarget(gui.getOrElse("requireOnlineTarget", template.isRequireOnlineTarget()))
                .whitelistServers(gui.getOrElse("whitelist", template.getWhitelistServers()))
                .blacklistServers(gui.getOrElse("blacklist", template.getBlacklistServers()))
                .placeholdersTarget(gui.getOrElse("placeholdersTarget", template.isPlaceholdersTarget()))
                .openSound(loadOpenSound(gui, template.getOpenSound()))
                .targetBypass(gui.getOrElse("targetBypass", template.isTargetBypass()))
                .closeable(gui.getOrElse("closeable", template.isCloseable()))
                .notifyTarget(gui.getOrElse("notifyTarget", template.getNotifyTarget()))
                .build();
    }

    GuiSound loadOpenSound(Config gui) {
        @Nullable GuiSound openSound = null;

        if (gui.contains("openSound")) {
            openSound = GuiSound.builder()
                    .soundName(gui.getOrElse("openSound.sound", "entity_villager_no"))
                    .soundCategory(gui.getEnumOrElse("openSound.soundCategory", SoundCategory.MASTER, EnumGetMethod.NAME_IGNORECASE))
                    .volume(gui.getOrElse("openSound.volume", 1.0d).floatValue())
                    .pitch(gui.getOrElse("openSound.pitch", 1.0d).floatValue())
                    .build();
        }

        return openSound;
    }

    GuiSound loadOpenSound(Config gui, GuiSound fallback) {
        GuiSound sound = loadOpenSound(gui);
        return sound == null ? fallback : sound;
    }

    GuiSound loadClickSound(Config item) {
        @Nullable GuiSound clickSound = null;

        if (item.contains("clickSound")) {
            clickSound = GuiSound.builder()
                    .soundName(item.getOrElse("clickSound.sound", "entity_villager_no"))
                    .soundCategory(item.getEnumOrElse("clickSound.soundCategory", SoundCategory.MASTER, EnumGetMethod.NAME_IGNORECASE))
                    .volume(item.getOrElse("clickSound.volume", 1.0d).floatValue())
                    .pitch(item.getOrElse("clickSound.pitch", 1.0d).floatValue())
                    .build();
        }

        return clickSound;
    }

    GuiItem loadItem(Config item) {
        return GuiItem.builder()
                .type(item.getEnumOrElse("type", ItemType.STONE, EnumGetMethod.NAME_IGNORECASE))
                .amount(item.getOrElse("count", 1))
                .title(item.getOrElse("name", ""))
                .lore(item.getOrElse("lore", Collections.emptyList()))
                .data(item.getOrElse("data", ""))
                .commands(item.getOrElse("commands", Collections.emptyList()))
                .rightCommands(item.getOrElse("rightCommands", Collections.emptyList()))
                .leftCommands(item.getOrElse("leftCommands", Collections.emptyList()))
                .enchanted(item.getOrElse("enchanted", false))
                .clickSound(loadClickSound(item))
                .build();
    }

    GuiAction loadAction(Config action) {
        return GuiAction.builder()
                .server(action.getOrElse("server", ""))
                .slot(action.getIntOrElse("slot", 1))
                .guiItem(loadItem(action.getOrElse("item", Config.inMemory())))
                .gui(action.getOrElse("gui", ""))
                .build();
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
            final @Nullable GuiGrid openGui = getOpenGui(player.getUniqueId());
            if (openGui == null) {
                return;
            }

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

            GuiSound clickSound = openGui.getItems().get(slot).getClickSound();
            if (clickSound != null) {
                if (SoundUtil.isValidSound(clickSound.getSoundName())) {
                    logger.warning("Sound " + clickSound + " is probably invalid");
                }
                clickSound.playFor(player);
            }

            if (openGui.getItems().get(slot).getCommands().isEmpty()) {
                return;
            }

            runCommand(player,openGui, slot, target, event.clickType());
            close(player, true);
        });
        inventory.onClose(event -> close(event.player().handle(), false));

        Protocolize.playerProvider().player(player.getUniqueId()).openInventory(inventory);
        openGuis.put(player.getUniqueId(), Pair.of(name, target));
    }

    private final Pattern permissionPattern = Pattern.compile("^perm<(?<node>[\\w.]+)>");
    private final Pattern noPermissionPattern = Pattern.compile("^noperm<(?<node>[\\w.]+)>");

    void runCommand(final @NotNull ProxiedPlayer player, final @NotNull GuiGrid openGui, final int slot, final @NotNull String target, final @NotNull ClickType clickType) {
        logger.info("Running " + clickType.name() + " commands for player " + player.getName() + " slot " + slot + " with target " + target);

        final @Nullable GuiItem item = openGui.getItems().get(slot);
        if (item == null) {
            return;
        }

        List<String> commands = clickType == ClickType.LEFT_CLICK ? ListUtil.concat(item.getLeftCommands(), item.getCommands()) :
                                clickType == ClickType.RIGHT_CLICK ? ListUtil.concat(item.getRightCommands(), item.getCommands()) :
                                item.getCommands();

        for (final @NotNull String command: commands) {
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
