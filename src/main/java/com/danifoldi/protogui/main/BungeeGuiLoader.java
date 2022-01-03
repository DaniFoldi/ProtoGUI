package com.danifoldi.protogui.main;

import com.danifoldi.protogui.command.CommandManager;
import com.danifoldi.protogui.util.ConfigUtil;
import com.danifoldi.protogui.util.FileUtil;
import com.danifoldi.protogui.util.Message;
import com.danifoldi.protogui.util.StringUtil;
import com.danifoldi.protogui.util.UpdateUtil;
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
    private final @NotNull BungeeGuiListener listener;
    private final @NotNull CommandManager commandManager;
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
                           final @NotNull BungeeGuiListener listener,
                           final @NotNull CommandManager commandManager,
                           final @NotNull ExecutorService threadPool) {
        this.guiHandler = guiHandler;
        this.plugin = plugin;
        this.logger = logger;
        this.pluginManager = pluginManager;
        this.datafolder = datafolder;
        this.placeholderHandler = placeholderHandler;
        this.listener = listener;
        this.commandManager = commandManager;
        this.threadPool = threadPool;
    }

    void load() {
        StringUtil.blockPrint(logger::info, "Loading %s version %s".formatted(plugin.getDescription().getName(), plugin.getDescription().getVersion()));

        commandManager.setup();
        BungeeGuiAPI.setInstance(new BungeeGuiAPI(guiHandler, this, placeholderHandler));
        placeholderHandler.registerBuiltins();

        try {
            FileUtil.ensureFolder(datafolder);
            final @NotNull FileConfig config = FileUtil.ensureConfigFile(datafolder, "config.yml");
            final @NotNull FileConfig messages = FileUtil.ensureConfigFile(datafolder, "messages.yml");
            FileUtil.ensureFolder(datafolder.resolve("actions"));
            FileUtil.ensureFolder(datafolder.resolve("templates"));
            FileUtil.ensureFolder(datafolder.resolve("guis"));
            config.load();
            messages.load();

            logger.setFilter(record -> config.getEnumOrElse("logLevel", LogLevel.ALL, EnumGetMethod.NAME_IGNORECASE).level.intValue() >= record.getLevel().intValue());

            Message.setMessageProvider(messages);
            if (config.getIntOrElse("configVersion", 0) < ConfigUtil.LATEST) {
                StringUtil.blockPrint(logger::warning, "%s config is built with an older version. Please see the plugin page for changes. Attempting automatic upgrade (backup saved as %s)".formatted(plugin.getDescription().getName(), ConfigUtil.backupAndUpgrade(config)));
            }

            if (config.getIntOrElse("configVersion", 0) > ConfigUtil.LATEST) {
                StringUtil.blockPrint(logger::warning, "%s config is built with a newer version. Compatibility is not guaranteed.".formatted(plugin.getDescription().getName()));
            }

            guiHandler.load(datafolder);
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
               StringUtil.blockPrint(logger::warning, "A new release is available for %s. Please update for bugfixes and new features.".formatted(plugin.getDescription().getName()));
               logger.warning("Your current version: %s, newest: %s".formatted(plugin.getDescription().getVersion(), newest));
           }
        });
    }

    void unload() {
        StringUtil.blockPrint(logger::info, "Unloading %s version %s".formatted(plugin.getDescription().getName(), plugin.getDescription().getVersion()));

        guiHandler.unload();
        placeholderHandler.unregisterAll();
        BungeeGuiAPI.setInstance(null);
        pluginManager.unregisterCommands(plugin);
        pluginManager.unregisterListeners(plugin);
    }
}