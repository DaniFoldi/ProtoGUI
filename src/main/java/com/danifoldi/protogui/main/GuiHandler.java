package com.danifoldi.protogui.main;

import com.danifoldi.protogui.gui.GuiAction;
import com.danifoldi.protogui.gui.GuiGrid;
import com.danifoldi.protogui.gui.GuiItem;
import com.danifoldi.protogui.gui.GuiSound;
import com.danifoldi.protogui.platform.PlatformInteraction;
import com.danifoldi.protogui.util.ConditionUtil;
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
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import dev.simplix.protocolize.data.ItemType;
import dev.simplix.protocolize.data.inventory.InventoryType;
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
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class GuiHandler {
    private final @NotNull Map<String, GuiGrid> templates = new ConcurrentHashMap<>();
    private final @NotNull Map<String, GuiGrid> menus = new ConcurrentHashMap<>();
    private final @NotNull Map<UUID, Pair<String, String>> openGuis = new ConcurrentHashMap<>();
    private final @NotNull List<GuiAction> guiActions = Collections.synchronizedList(new ArrayList<>());
    private final @NotNull Logger logger;

    @Inject
    GuiHandler(final @NotNull Logger logger) {
        this.logger = logger;
    }

    @NotNull GuiGrid getGui(final @NotNull String name) {
        return this.menus.get(name);
    }

    void load(@NotNull Path datafolder) throws IOException {
        try (Stream<Path> stream = Files.list(datafolder.resolve("actions"))) {
            stream
                    .filter(p -> !Files.isDirectory(p))
                    .filter(p -> p.getFileName().toString().endsWith(".yml"))
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
                    .filter(p -> p.getFileName().toString().endsWith(".yml"))
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
                    .filter(p -> p.getFileName().toString().endsWith(".yml"))
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
                            e.printStackTrace();
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
        Map<Integer, GuiItem> itemMap = new ConcurrentHashMap<>();

        for (final @NotNull Config.Entry guiItem : guiItems.entrySet()) {
            final @NotNull Set<Integer> slots = SlotUtil.getSlots(guiItem.getKey(), SlotUtil.getInventorySize(SlotUtil.getInventoryType(size)));
            final @NotNull Config itemData = guiItem.getValue();

            for (final int slot : slots) {
                GuiItem item = loadItem(itemData, fallback.get(slot));
                itemMap.put(slot, item.copy());
            }
        }

        fallback.forEach(itemMap::putIfAbsent);

        return itemMap;
    }

    GuiGrid loadGuiGrid(Config gui, String name) {
        int size = gui.getIntOrElse("size", 54);

        return GuiGrid.builder()
                .items(loadItemMap(gui.getOrElse("items", Config.inMemory()), size))
                .targeted(gui.getOrElse("targeted", false))
                .commands(gui.getOrElse("aliases", Collections.singletonList("<<#TEMPLATE#>>")))
                .permission(gui.getOrElse("permission", "<<#TEMPLATE#>>"))
                .size(size)
                .title(gui.getOrElse("title", "<<#TEMPLATE#>>"))
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
                .commands(gui.getOrElse("aliases", template.getCommandAliases().size() > 0 && Objects.equals(template.getCommandAliases().get(0), "<<#TEMPLATE#>>") ? Collections.singletonList(name.toLowerCase(Locale.ROOT)) : template.getCommandAliases()))
                .permission(gui.getOrElse("permission", template.getPermission().equals("<<#TEMPLATE#>>") ? "protogui.gui." + name.toLowerCase(Locale.ROOT).replace("{", "").replace("}", "").replace(" ", "") : template.getPermission()))
                .size(size)
                .title(gui.getOrElse("title", template.getTitle().equals("<<#TEMPLATE#>>") ? "GUI " + name.toLowerCase(Locale.ROOT) : template.getTitle()))
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

    GuiSound loadClickSound(Config item, GuiSound fallback) {
        if (item.contains("clickSound")) {
            return GuiSound.builder()
                    .soundName(item.getOrElse("clickSound.sound", fallback.getSoundName()))
                    .soundCategory(item.getEnumOrElse("clickSound.soundCategory", fallback.getSoundCategory(), EnumGetMethod.NAME_IGNORECASE))
                    .volume(item.getOrElse("clickSound.volume", fallback.getVolume()))
                    .pitch(item.getOrElse("clickSound.pitch", fallback.getPitch()))
                    .build();
        } else {
            return fallback;
        }
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
                .clickableIf(item.getOrElse("clickableIf", ""))
                .shownIf(item.getOrElse("shownIf", ""))
                .build();
    }

    GuiItem loadItem(Config item, GuiItem fallback) {
        if (fallback == null) {
            return loadItem(item);
        }

        return GuiItem.builder()
                .type(item.getEnumOrElse("type", fallback.getType(), EnumGetMethod.NAME_IGNORECASE))
                .amount(item.getOrElse("count", fallback.getAmount()))
                .title(item.getOrElse("name", fallback.getName()))
                .lore(item.getOrElse("lore", fallback.getLore()))
                .data(item.getOrElse("data", fallback.getData()))
                .commands(item.getOrElse("commands", fallback.getCommands()))
                .rightCommands(item.getOrElse("rightCommands", fallback.getRightCommands()))
                .leftCommands(item.getOrElse("leftCommands", fallback.getLeftCommands()))
                .enchanted(item.getOrElse("enchanted", fallback.isEnchanted()))
                .clickSound(loadClickSound(item, fallback.getClickSound()))
                .clickableIf(item.getOrElse("clickableIf", fallback.getClickableIf()))
                .shownIf(item.getOrElse("shownIf", fallback.getShownIf()))
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
            CommandHandler commandHandler = new CommandHandler(name, gui);
            ProtoGuiAPI.getInstance().getPlatform().registerCommand(gui.getCommandAliases(), gui.getPermission(), commandHandler::dispatch, commandHandler::suggest);
        }
    }

    void removeGui(final @NotNull String name) {
        openGuis.entrySet().stream().filter(v -> v.getValue().getFirst().equals(name)).map(Map.Entry::getKey).forEach(u -> close(u, true));
        menus.get(name).getCommandAliases().forEach(ProtoGuiAPI.getInstance().getPlatform()::unregisterCommand);

        menus.remove(name);
    }

    void updateActions(final @NotNull UUID uuid) {
        PlayerInventory inventory = Protocolize.playerProvider().player(uuid).proxyInventory();
        PlatformInteraction.ProtoPlayer player = ProtoGuiAPI.getInstance().getPlatform().getPlayer(uuid);

        guiActions
                .stream()
                .filter(a -> a.getServer().equalsIgnoreCase(player.connectedTo().name()))
                .forEach(a -> inventory.item(a.getSlot(), a.getGuiItem().toItemStack(player, player.name(), "")));

        inventory.update();
    }

    void handleActions(final @NotNull UUID uuid, final int slot) {
        guiActions
                .stream()
                .filter(a -> a.getServer().equalsIgnoreCase(ProtoGuiAPI.getInstance().getPlatform().getPlayer(uuid).connectedTo().name()))
                .filter(a -> a.getSlot() == slot)
                .findFirst()
                .ifPresent(guiAction -> {
                    if (guiAction.getGuiItem().getClickSound() != null) {
                        guiAction.getGuiItem().getClickSound().playFor(uuid);
                    }
                  open(guiAction.getGui(), uuid, "");
        });
    }

    void open(final @NotNull GuiGrid gui, final @NotNull UUID uuid, final @NotNull String target) {
        String name = getGuiName(gui);
        logger.info("Opening gui " + name + " for player " + uuid + " (target: " + target + ")");

        final PlatformInteraction.ProtoPlayer player = ProtoGuiAPI.getInstance().getPlatform().getPlayer(uuid);
        final PlatformInteraction.ProtoPlayer targetPlayer = ProtoGuiAPI.getInstance().getPlatform().getPlayer(target);
        final PlatformInteraction.ProtoPlayer placeholderPlayer = menus.get(name).isRequireOnlineTarget() && menus.get(name).isPlaceholdersTarget() && targetPlayer != null ? targetPlayer : player;
        final Inventory inventory = new Inventory(SlotUtil.getInventoryType(gui.getGuiSize())).title(Message.process(placeholderPlayer, gui.getTitle(), Pair.of("player", player.name()), Pair.of("target", targetPlayer != null ? targetPlayer.name() : target)));

        if (gui.getOpenSound() != null) {
            if (SoundUtil.isValidSound(gui.getOpenSound().getSoundName())) {
                logger.warning("Sound " + gui.getOpenSound().getSoundName() + " is probably invalid");
            }
            gui.getOpenSound().playFor(uuid);
        }

        if (targetPlayer != null && !gui.getNotifyTarget().equals("")) {
            targetPlayer.send(Message.process(targetPlayer, gui.getNotifyTarget(), Pair.of("player", player.name()), Pair.of("target", targetPlayer.name())));
        }

        for (final @NotNull Map.Entry<Integer, GuiItem> guiItem: gui.getItems().entrySet()) {
            if (guiItem.getKey() < 0 || guiItem.getKey() >= SlotUtil.getInventorySize(SlotUtil.getInventoryType(gui.getGuiSize()))) {
                logger.warning("GUI " + name + " contains an item at slot " + guiItem.getKey() + " which is outside of the Inventory");
                continue;
            }

            if (guiItem.getValue().getShownIf() != null && !ConditionUtil.holds(Message.process(placeholderPlayer, guiItem.getValue().getShownIf()), placeholderPlayer)) {
                continue;
            }

            inventory.item(guiItem.getKey(), guiItem.getValue().toItemStack(placeholderPlayer, player.name(), target));
        }

        inventory.onClick(event -> {
            final @Nullable GuiGrid openGui = getOpenGui(uuid);
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

            GuiItem guiItem = openGui.getItems().get(slot);

            if (guiItem.getClickableIf() != null && !ConditionUtil.holds(Message.process(placeholderPlayer, guiItem.getClickableIf()), placeholderPlayer)) {
                return;
            }

            GuiSound clickSound = openGui.getItems().get(slot).getClickSound();
            if (clickSound != null) {
                if (SoundUtil.isValidSound(clickSound.getSoundName())) {
                    logger.warning("Sound " + clickSound + " is probably invalid");
                }
                clickSound.playFor(uuid);
            }

            if (guiItem.getCommands().isEmpty()) {
                return;
            }

            runCommand(uuid, openGui, slot, target, event.clickType());
            close(uuid, true);
        });
        inventory.onClose(event -> close(event.player().uniqueId(), false));

        Protocolize.playerProvider().player(uuid).openInventory(inventory);
        openGuis.put(uuid, Pair.of(name, target));
    }

    void open(final @NotNull String name, final @NotNull UUID uuid, final @NotNull String target) {
        open(getGui(name), uuid, target);
    }

    void runCommand(final @NotNull UUID uuid, final @NotNull GuiGrid openGui, final int slot, final @NotNull String target, final @NotNull ClickType clickType) {
        logger.info("Running " + clickType.name() + " commands for player " + uuid + " slot " + slot + " with target " + target);

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
            PlatformInteraction.ProtoPlayer player = ProtoGuiAPI.getInstance().getPlatform().getPlayer(uuid);

            if (commandData.getFirst().equalsIgnoreCase("console")) {
                ProtoGuiAPI.getInstance().getPlatform().runConsoleCommand(Message.replace(commandData.getSecond(), Pair.of("player", player.name()), Pair.of("target", target)));
                continue;
            }

            player.run(Message.replace(command, Pair.of("player", player.name()), Pair.of("target", target)));
        }
    }

    void close(final @NotNull UUID uuid, final boolean didClick) {
        if (!openGuis.containsKey(uuid)) {
            return;
        }

        if (!menus.get(openGuis.get(uuid).getFirst()).isCloseable() && !didClick) {
            open(openGuis.get(uuid).getFirst(), uuid, openGuis.get(uuid).getSecond());
            return;
        }

        logger.info("Removing gui from cache for " + uuid);

        openGuis.remove(uuid);
        Protocolize.playerProvider().player(uuid).closeInventory();
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

    @NotNull Set<String> getGuis() {
        return Set.copyOf(menus.keySet());
    }
}
