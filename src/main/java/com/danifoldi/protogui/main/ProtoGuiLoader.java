package com.danifoldi.protogui.main;

import com.danifoldi.protogui.command.CommandManager;
import com.danifoldi.protogui.platform.PlatformInteraction;
import com.danifoldi.protogui.util.ConfigUtil;
import com.danifoldi.protogui.util.FileUtil;
import com.danifoldi.protogui.util.Message;
import com.danifoldi.protogui.util.StringUtil;
import com.danifoldi.protogui.util.UpdateUtil;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.file.FileConfig;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProtoGuiLoader {

    private final @NotNull GuiHandler guiHandler;
    final @NotNull PlatformInteraction platform;
    private final @NotNull Logger logger;
    private final @NotNull Path datafolder;
    private final @NotNull PlaceholderHandler placeholderHandler;
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
    public ProtoGuiLoader(final @NotNull GuiHandler guiHandler,
                          final @NotNull PlatformInteraction platform,
                          final @NotNull Logger logger,
                          final @NotNull Path datafolder,
                          final @NotNull PlaceholderHandler placeholderHandler,
                          final @NotNull CommandManager commandManager,
                          final @NotNull ExecutorService threadPool) {
        this.guiHandler = guiHandler;
        this.platform = platform;
        this.logger = logger;
        this.datafolder = datafolder;
        this.placeholderHandler = placeholderHandler;
        this.commandManager = commandManager;
        this.threadPool = threadPool;
    }

    public void load() {
        StringUtil.blockPrint(logger::info, "Loading %s version %s".formatted(platform.pluginName(), platform.pluginVersion()));

        ProtoGuiAPI.setInstance(new ProtoGuiAPI(guiHandler, this, placeholderHandler));
        commandManager.setup();
        placeholderHandler.registerBuiltins();

        try {
            boolean newInstall = FileUtil.ensureFolder(datafolder);
            FileUtil.ensureFolder(datafolder.resolve("actions"));
            FileUtil.ensureFolder(datafolder.resolve("templates"));
            FileUtil.ensureFolder(datafolder.resolve("guis"));
            final @NotNull FileConfig config = FileUtil.ensureConfigFile(datafolder, "config.yml");
            config.load();
            int configVersion = config.getIntOrElse("configVersion", 0);
            config.close();

            if (configVersion < ConfigUtil.LATEST) {
                StringUtil.blockPrint(logger::warning, "%s config is built with an older version. Please see the https://github.com/DaniFoldi/ProtoGUI/releases page for changes. Attempting automatic upgrade (backup saved as %s)".formatted(platform.pluginName(), ConfigUtil.backupAndUpgrade(datafolder, configVersion)));
            }

            if (configVersion > ConfigUtil.LATEST) {
                StringUtil.blockPrint(logger::warning, "%s config is built with a newer version. Compatibility is not guaranteed.".formatted(platform.pluginName()));
            }

            final @NotNull FileConfig newConfig = FileUtil.ensureConfigFile(datafolder, "config.yml");
            newConfig.load();

            final @NotNull FileConfig messages = FileUtil.ensureConfigFile(datafolder, "messages.yml");
            messages.load();
            Message.setMessageProvider(messages);

            logger.setFilter(record -> newConfig.getEnumOrElse("logLevel", LogLevel.ALL, EnumGetMethod.NAME_IGNORECASE).level.intValue() <= record.getLevel().intValue());

            if (newInstall) {
                FileUtil.ensureConfigFile(datafolder.resolve("guis").resolve("authors.yml"), "authors.yml");
                FileUtil.ensureConfigFile(datafolder.resolve("guis").resolve("servermenu.yml"), "servermenu.yml");
                FileUtil.ensureConfigFile(datafolder.resolve("guis").resolve("sounds.yml"), "sounds.yml");
                FileUtil.ensureConfigFile(datafolder.resolve("guis").resolve("stats.yml"), "stats.yml");
            }

            guiHandler.load(datafolder);
            platform.setup();
        } catch (IOException e) {
            StringUtil.blockPrint(logger::severe, "Could not enable plugin, please see the error below");
            logger.severe(e.getMessage());
            e.printStackTrace();
        }

        UpdateUtil.getNewest(threadPool).thenAccept(newest -> {
           if (newest.equals("")) {
               logger.warning("Could not check for updates");
           }
           if (!UpdateUtil.isNewer(platform.pluginVersion(), newest)) {
               StringUtil.blockPrint(logger::warning, "A new release is available for %s. Please update for bugfixes and new features.".formatted(platform.pluginName()));
               logger.warning("Your current version: %s, newest: %s".formatted(platform.pluginVersion(), newest));
           }
        });
    }

    public void unload() {
        StringUtil.blockPrint(logger::info, "Unloading %s version %s".formatted(platform.pluginName(), platform.pluginVersion()));

        guiHandler.unload();
        placeholderHandler.unregisterAll();
        ProtoGuiAPI.setInstance(null);
        platform.teardown();
    }
}