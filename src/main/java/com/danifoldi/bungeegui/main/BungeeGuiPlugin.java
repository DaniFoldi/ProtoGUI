package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.inject.BungeeGuiComponent;
import com.danifoldi.bungeegui.inject.DaggerBungeeGuiComponent;
import net.md_5.bungee.api.plugin.Plugin;

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
