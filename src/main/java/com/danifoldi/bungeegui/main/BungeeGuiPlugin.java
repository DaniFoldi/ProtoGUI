package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.inject.BungeeGuiComponent;
import com.danifoldi.bungeegui.inject.DaggerBungeeGuiComponent;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;

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
                .threadPool(Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("BungeeGUI Async Pool - #%1$d").setDaemon(false).build()))
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
