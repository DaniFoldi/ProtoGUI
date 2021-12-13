package com.danifoldi.bungeegui.inject;

import com.danifoldi.bungeegui.main.BungeeGuiLoader;
import com.danifoldi.bungeegui.main.BungeeGuiPlugin;
import dagger.BindsInstance;
import dagger.Component;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

@Singleton
@Component
public interface BungeeGuiComponent {

    @NotNull BungeeGuiLoader loader();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder plugin(final @NotNull BungeeGuiPlugin plugin);

        @BindsInstance
        Builder logger(final @NotNull Logger logger);

        @BindsInstance
        Builder proxyServer(final @NotNull ProxyServer proxyServer);

        @BindsInstance
        Builder scheduler(final @NotNull TaskScheduler scheduler);

        @BindsInstance
        Builder pluginManager(final @NotNull PluginManager pluginManager);

        @BindsInstance
        Builder datafolder(final @NotNull Path datafolder);

        @BindsInstance
        Builder threadPool(final @NotNull ExecutorService threadPool);

        BungeeGuiComponent build();
    }
}