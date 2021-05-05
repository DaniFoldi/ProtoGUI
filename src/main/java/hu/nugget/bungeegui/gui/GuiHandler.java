package hu.nugget.bungeegui.gui;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryModule;
import de.exceptionflug.protocolize.inventory.InventoryType;
import de.exceptionflug.protocolize.items.ItemStack;
import de.exceptionflug.protocolize.items.ItemType;
import hu.nugget.bungeegui.BungeeGuiCommand;
import hu.nugget.bungeegui.BungeeGuiPlugin;
import hu.nugget.bungeegui.util.Message;
import hu.nugget.bungeegui.util.Pair;
import hu.nugget.bungeegui.util.StringUtil;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.file.Path;
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
    private final Map<String, GuiGrid> menus = new HashMap<>();
    private final Map<UUID, Pair<String, String>> openGuis = new HashMap<>();
    private final Logger logger;
    private final PluginManager pluginManager;
    private final BungeeGuiPlugin plugin;

    @Inject
    public GuiHandler(final @NotNull Logger logger,
                      final @NotNull PluginManager pluginManager,
                      final @NotNull BungeeGuiPlugin plugin) {
        this.logger = logger;
        this.pluginManager = pluginManager;
        this.plugin = plugin;
    }

    @NotNull
    public GuiGrid getGui(final @NotNull String name) {
        if (!this.menus.containsKey(name)) {
            throw new IllegalArgumentException();
        }

        return this.menus.get(name);
    }

    public Config load(Path file) {
        menus.clear();

        final FileConfig config = FileConfig.of(file);
        config.load();
        final Config guis = config.get("guis");

        for (Config.Entry gui: guis.entrySet()) {
            final String name = gui.getKey();
            final Config guiData = gui.getValue();
            final Config guiItems = guiData.getOrElse("items", Config.inMemory());
            final Map<Integer, GuiItem> itemMap = new HashMap<>();

            for (Config.Entry guiItem: guiItems.entrySet()) {
                final int slot = Integer.parseInt(guiItem.getKey());
                final Config itemData = guiItem.getValue();

                Map<String, Integer> enchantments = new HashMap<>();
                if (itemData.contains("enchantments")) {
                    for (Config.Entry enchantment : ((Config) itemData.get("enchantments")).entrySet()) {
                        enchantments.put(enchantment.getKey(), enchantment.getIntOrElse(1));
                    }
                }

                GuiItem item = new GuiItem(ItemType.valueOf(itemData.getOrElse("type", "stone").toUpperCase(Locale.ROOT)),
                        itemData.getOrElse("count", 1),
                        itemData.getOrElse("name", ""),
                        itemData.getOrElse("lore", List.of()),
                        itemData.getOrElse("data", ""),
                        itemData.getOrElse("command", Set.of()),
                        enchantments
                        );

                itemMap.put(slot, item);
            }

            final GuiGrid grid = new GuiGrid(itemMap,
                    guiData.getOrElse("targeted", false),
                    guiData.getOrElse("aliases", List.of(name.toLowerCase(Locale.ROOT))).stream().map(String::toLowerCase).collect(Collectors.toList()),
                    guiData.getOrElse("permission", "bungeegui.gui." + name.toLowerCase(Locale.ROOT)),
                    guiData.getIntOrElse("size", 54),
                    guiData.getOrElse("title", "GUI " + name.toLowerCase(Locale.ROOT)));

            menus.put(name, grid);
        }

        return config;
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

    public void registerCommands() {
        for (String name: menus.keySet()) {
            pluginManager.registerCommand(plugin, new BungeeGuiCommand(name, this));
        }
    }

    public void open(String name, ProxiedPlayer player, @Nullable String target) {
        logger.info("Opening gui " + name + " for player " + player.getName() + " (target: " + target + ")");

        final GuiGrid gui = menus.get(name);
        final Inventory inventory = new Inventory(getInventoryType(gui.getGuiSize()), Message.toComponent(gui.getTitle(), Pair.of("player", player.getName()), Pair.of("target", target)));

        for (Map.Entry<Integer, GuiItem> guiItem: gui.getItems().entrySet()) {
            final ItemStack item = new ItemStack(guiItem.getValue().getType());
            item.setAmount((byte)guiItem.getValue().getAmount());
            item.setDisplayName(Message.toComponent(guiItem.getValue().getName(), Pair.of("player", player.getName()), Pair.of("target", target)));
            item.setLoreComponents(guiItem.getValue().getLore().stream().map(l -> Message.toComponent(l, Pair.of("player", player.getName()), Pair.of("target", target))).collect(Collectors.toList()));

            if (item.isPlayerSkull()) {
                final Pair<String, String> data = StringUtil.get(guiItem.getValue().getData());
                if (data.getFirst().equalsIgnoreCase("owner")) {
                    item.setSkullOwner(data.getSecond());
                } else if (data.getFirst().equalsIgnoreCase("texture")) {
                    item.setSkullTexture(data.getSecond());
                }
            }

            inventory.setItem(guiItem.getKey(), item);
        }

        InventoryModule.sendInventory(player, inventory);
        openGuis.put(player.getUniqueId(), Pair.of(name, target));
    }

    public void runCommand(ProxiedPlayer player, GuiGrid openGui, int slot, String target) {
        logger.info("Running commands for player " + player.getName() + " slot " + slot + " with target " + target);

        final GuiItem item = openGui.getItems().get(slot);
        if (item == null) {
            return;
        }

        final Set<String> commands = item.getCommands();
        for (String command: commands) {
            player.chat(Message.replace(command, Pair.of("player", player.getName()), Pair.of("target", target)));
        }
    }

    public void close(ProxiedPlayer player) {
        if (!openGuis.containsKey(player.getUniqueId())) {
            return;
        }

        logger.info("Removing gui from cache for " + player.getName());

        openGuis.remove(player.getUniqueId());
        InventoryModule.closeAllInventories(player);
    }

    public GuiGrid getOpenGui(UUID uuid) {
        return menus.get(openGuis.get(uuid).getFirst());
    }
    public String getGuiTarget(UUID uuid) {
        return openGuis.get(uuid).getSecond();
    }

    public String getGuiName(GuiGrid gui) {
        for (Map.Entry<String, GuiGrid> menu: menus.entrySet()) {
            if (menu.getValue() != gui) {
                continue;
            }

            return menu.getKey();
        }

        return "";
    }
}
