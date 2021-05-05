package hu.nugget.bungeegui;

import hu.nugget.bungeegui.inject.BungeeGuiComponent;
import hu.nugget.bungeegui.inject.DaggerBungeeGuiComponent;
import hu.nugget.bungeegui.util.FileUtil;
import net.md_5.bungee.api.plugin.Plugin;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;

public class BungeeGuiPlugin extends Plugin {
    private BungeeGuiLoader loader;

    @Override
    public void onEnable() {
        final BungeeGuiComponent component = DaggerBungeeGuiComponent.builder()
                .plugin(this)
                .logger(getLogger())
                .pluginManager(getProxy().getPluginManager())
                .datafolder(getDataFolder().toPath())
                .build();
        this.loader = component.loader();

        this.loader.load();
    }

    @Override
    public void onDisable() {
        this.loader.unload();
    }
}
