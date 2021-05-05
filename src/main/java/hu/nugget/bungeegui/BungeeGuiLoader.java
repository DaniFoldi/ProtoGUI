package hu.nugget.bungeegui;

import com.electronwill.nightconfig.core.Config;
import hu.nugget.bungeegui.gui.GuiHandler;
import hu.nugget.bungeegui.util.FileUtil;
import hu.nugget.bungeegui.util.Message;
import hu.nugget.bungeegui.util.StringUtil;
import net.md_5.bungee.api.plugin.PluginManager;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class BungeeGuiLoader {

    private final GuiHandler guiHandler;
    private final BungeeGuiPlugin plugin;
    private final Logger logger;
    private final PluginManager pluginManager;
    private final Path datafolder;

    @Inject
    public BungeeGuiLoader(GuiHandler guiHandler, BungeeGuiPlugin plugin, Logger logger, PluginManager pluginManager, Path datafolder) {
        this.guiHandler = guiHandler;
        this.plugin = plugin;
        this.logger = logger;
        this.pluginManager = pluginManager;
        this.datafolder = datafolder;
    }

    void load() {
        StringUtil.blockPrint("Loading " + plugin.getDescription().getName() + " version " + plugin.getDescription().getVersion()).forEach(logger::info);

        try {
            FileUtil.ensureFile(datafolder, "config.yml");
        } catch (IOException e) {
            logger.severe("Could not preload config file");
            logger.severe(e.getMessage());
            e.printStackTrace();
        }

        Config config = guiHandler.load(datafolder.resolve("config.yml"));

        Map<String, String> messages = new HashMap<>();
        for (Config.Entry message: ((Config)config.get("messages")).entrySet()) {
            messages.put(message.getKey(), message.getValue());
        }
        Message.setMessageProvider(messages);

        pluginManager.registerCommand(plugin, new ReloadCommand(this));
        guiHandler.registerCommands();
        pluginManager.registerListener(plugin, new BungeeGuiListener(guiHandler));
    }

    void unload() {
        StringUtil.blockPrint("Unloading " + plugin.getDescription().getName() + " version " + plugin.getDescription().getVersion()).forEach(logger::info);
        pluginManager.unregisterCommands(plugin);
        pluginManager.unregisterListeners(plugin);
    }
}