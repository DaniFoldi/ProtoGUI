package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.command.PluginCommand;
import com.danifoldi.bungeegui.util.ConfigUtil;
import com.danifoldi.bungeegui.util.FileUtil;
import com.danifoldi.bungeegui.util.Message;
import com.danifoldi.bungeegui.util.StringUtil;
import com.danifoldi.bungeegui.util.UpdateUtil;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.file.FileConfig;
import net.md_5.bungee.api.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BungeeGuiLoader {

    private final @NotNull GuiHandler guiHandler;
    private final @NotNull BungeeGuiPlugin plugin;
    private final @NotNull Logger logger;
    private final @NotNull PluginManager pluginManager;
    private final @NotNull Path datafolder;
    private final @NotNull PlaceholderHandler placeholderHandler;
    private final @NotNull PluginCommand command;
    private final @NotNull BungeeGuiListener listener;
    private final @NotNull ExecutorService threadPool;

    @SuppressWarnings("unused")
    private enum LogLevel {
        OFF(Level.OFF), SEVERE(Level.SEVERE), WARNING(Level.WARNING), INFO(Level.INFO), CONFIG(Level.CONFIG), FINE(Level.FINE), FINER(Level.FINER), FINEST(Level.FINEST), ALL(Level.ALL);

        private final @NotNull Level level;

        LogLevel(final @NotNull Level level) {
            this.level = level;
        }
    }

    @Inject
    public BungeeGuiLoader(final @NotNull GuiHandler guiHandler,
                           final @NotNull BungeeGuiPlugin plugin,
                           final @NotNull Logger logger,
                           final @NotNull PluginManager pluginManager,
                           final @NotNull Path datafolder,
                           final @NotNull PlaceholderHandler placeholderHandler,
                           final @NotNull PluginCommand command,
                           final @NotNull BungeeGuiListener listener,
                           final @NotNull ExecutorService threadPool) {
        this.guiHandler = guiHandler;
        this.plugin = plugin;
        this.logger = logger;
        this.pluginManager = pluginManager;
        this.datafolder = datafolder;
        this.placeholderHandler = placeholderHandler;
        this.command = command;
        this.listener = listener;
        this.threadPool = threadPool;
    }

    void load() {
        StringUtil.blockPrint(logger::info, "Loading %s version %s".formatted(plugin.getDescription().getName(), plugin.getDescription().getVersion()));

        pluginManager.registerCommand(plugin, command);
        BungeeGuiAPI.setInstance(new BungeeGuiAPI(guiHandler, this, placeholderHandler));
        placeholderHandler.registerBuiltins();

        try {
            final @NotNull FileConfig config = FileUtil.ensureConfigFile(datafolder, "config.yml");
            config.load();

            logger.setFilter(record -> config.getEnumOrElse("logLevel", LogLevel.ALL, EnumGetMethod.NAME_IGNORECASE).level.intValue() >= record.getLevel().intValue());

            Message.setMessageProvider(config);
            if (config.getIntOrElse("configVersion", 0) < ConfigUtil.LATEST) {
                StringUtil.blockPrint(logger::warning, "BungeeGUI config.yml is built with an older version. Please see the plugin page for changes. Attempting automatic upgrade (backup saved as %s)".formatted(ConfigUtil.backupAndUpgrade(config)));
            }

            if (config.getIntOrElse("configVersion", 0) > ConfigUtil.LATEST) {
                StringUtil.blockPrint(logger::warning, "BungeeGUI config.yml is built with a newer version. Compatibility is not guaranteed.");
            }

            guiHandler.load(config);
            pluginManager.registerListener(plugin, listener);
        } catch (IOException e) {
            StringUtil.blockPrint(logger::severe, "Could not enable plugin, please see the error below");
            logger.severe(e.getMessage());
            e.printStackTrace();
        }

        UpdateUtil.getNewest(threadPool).thenAccept(newest -> {
           if (newest.equals("")) {
               logger.warning("Could not check for updates");
           }
           if (!newest.equals(plugin.getDescription().getVersion())) {
               StringUtil.blockPrint(logger::warning, "A new release is available for BungeeGUI. Please update for bugfixes and new features.");
               logger.warning("Your current version: %s, newest: %s".formatted(plugin.getDescription().getVersion(), newest));
           }
        });
    }

    void unload() {
        StringUtil.blockPrint(logger::info, "Unloading %s version %s".formatted(plugin.getDescription().getName(), plugin.getDescription().getVersion()));

        guiHandler.getGuis().forEach(guiHandler::removeGui);
        placeholderHandler.unregisterAll();
        BungeeGuiAPI.setInstance(null);
        pluginManager.unregisterCommands(plugin);
        pluginManager.unregisterListeners(plugin);
    }
}