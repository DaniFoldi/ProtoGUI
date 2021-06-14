package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.inject.BungeeGuiComponent;
import com.danifoldi.bungeegui.inject.DaggerBungeeGuiComponent;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BungeeGuiPlugin extends Plugin {
    private @Nullable BungeeGuiLoader loader;

    @Override
    public void onEnable() {
        final @NotNull BungeeGuiComponent component = DaggerBungeeGuiComponent.builder()
                .plugin(this)
                .logger(getLogger())
                .proxyServer(getProxy())
                .scheduler(getProxy().getScheduler())
                .pluginManager(getProxy().getPluginManager())
                .datafolder(getDataFolder().toPath())
                .build();
        this.loader = component.loader();

        this.loader.load();
    }

    @Override
    public void onDisable() {
        if (this.loader == null) {
            return;
        }
        this.loader.unload();
    }
}
