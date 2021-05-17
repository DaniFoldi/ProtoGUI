package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.command.PluginCommand;
import com.danifoldi.bungeegui.util.ConfigUtil;
import com.danifoldi.bungeegui.util.FileUtil;
import com.danifoldi.bungeegui.util.Message;
import com.danifoldi.bungeegui.util.StringUtil;
import com.danifoldi.bungeegui.util.UpdateUtil;
import com.electronwill.nightconfig.core.EnumGetMethod;
import com.electronwill.nightconfig.core.file.FileConfig;
import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class BungeeGuiLoader {

    private final GuiHandler guiHandler;
    private final BungeeGuiPlugin plugin;
    private final Logger logger;
    private final PluginManager pluginManager;
    private final Path datafolder;
    private final PlaceholderHandler placeholderHandler;
    private final ProtocolSoundFixer soundFixer;

    private enum LogLevel {
        OFF(Level.OFF), SEVERE(Level.SEVERE), WARNING(Level.WARNING), INFO(Level.INFO), CONFIG(Level.CONFIG), FINE(Level.FINE), FINER(Level.FINER), FINEST(Level.FINEST), ALL(Level.ALL);

        private Level level;

        LogLevel(Level level) {
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
                           final @NotNull ProtocolSoundFixer soundFixer) {
        this.guiHandler = guiHandler;
        this.plugin = plugin;
        this.logger = logger;
        this.pluginManager = pluginManager;
        this.datafolder = datafolder;
        this.placeholderHandler = placeholderHandler;
        this.soundFixer = soundFixer;
    }

    void load() {
        StringUtil.blockPrint("Loading " + plugin.getDescription().getName() + " version " + plugin.getDescription().getVersion()).forEach(logger::info);

        pluginManager.registerCommand(plugin, new PluginCommand());
        BungeeGuiAPI.setInstance(new BungeeGuiAPI(guiHandler, this, placeholderHandler));
        placeholderHandler.registerBuiltins();

        try {
            final FileConfig config = FileUtil.ensureConfigFile(datafolder, "config.yml");
            config.load();

            logger.setFilter(record -> config.getEnumOrElse("debugLevel", LogLevel.ALL, EnumGetMethod.NAME_IGNORECASE).level.intValue() >= record.getLevel().intValue());

            guiHandler.load(config);
            Message.setMessageProvider(config);
            if (config.getIntOrElse("configVersion", 0) < ConfigUtil.LATEST) {
                StringUtil.blockPrint("BungeeGUI config.yml is built with an older version. Please see the plugin page for changes. Attempting automatic upgrade (backup saved as {file})".replace("{file}", ConfigUtil.backupAndUpgrade(config))).forEach(logger::warning);
            }

            if (config.getIntOrElse("configVersion", 0) > ConfigUtil.LATEST) {
                StringUtil.blockPrint("BungeeGUI config.yml is built with a newer version. Compatibility is not guaranteed.").forEach(logger::warning);
            }

            pluginManager.registerListener(plugin, new BungeeGuiListener(guiHandler));
            try {
                ProtocolAPI.getEventManager().registerListener(soundFixer);
            } catch (IllegalStateException e) {
                // TODO remove once there is a way to unregister it
            }
            soundFixer.enable();
        } catch (IOException e) {
            StringUtil.blockPrint("Could not enable plugin, please see the error below").forEach(logger::severe);
            logger.severe(e.getMessage());
            e.printStackTrace();
        }

        UpdateUtil.getNewest().thenAccept(newest -> {
           if (newest.equals("")) {
               logger.warning("Could not check for updates");
           }
           if (!newest.equals(plugin.getDescription().getVersion())) {
               StringUtil.blockPrint("You are not running the latest version of BungeeGUI. Please update for bugfixes and new features.").forEach(logger::warning);
           }
        });
    }

    void unload() {
        StringUtil.blockPrint("Unloading " + plugin.getDescription().getName() + " version " + plugin.getDescription().getVersion()).forEach(logger::info);

        guiHandler.getGuis().forEach(guiHandler::removeGui);
        soundFixer.disable();
        placeholderHandler.unregisterAll();
        BungeeGuiAPI.setInstance(null);
        pluginManager.unregisterCommands(plugin);
        pluginManager.unregisterListeners(plugin);
    }
}