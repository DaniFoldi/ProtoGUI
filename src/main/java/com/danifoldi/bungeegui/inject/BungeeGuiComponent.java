package com.danifoldi.bungeegui.inject;

import com.danifoldi.bungeegui.main.BungeeGuiLoader;
import com.danifoldi.bungeegui.main.BungeeGuiPlugin;
import dagger.BindsInstance;
import dagger.Component;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.logging.Logger;

@Singleton
@Component
public interface BungeeGuiComponent {

    BungeeGuiLoader loader();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder plugin(final BungeeGuiPlugin plugin);

        @BindsInstance
        Builder logger(final Logger logger);

        @BindsInstance
        Builder proxyServer(final ProxyServer proxyServer);

        @BindsInstance
        Builder scheduler(final TaskScheduler scheduler);

        @BindsInstance
        Builder pluginManager(final PluginManager pluginManager);

        @BindsInstance
        Builder datafolder(final Path datafolder);

        BungeeGuiComponent build();

    }
}