package hu.nugget.bungeegui.inject;

import dagger.BindsInstance;
import dagger.Component;
import hu.nugget.bungeegui.BungeeGuiLoader;
import hu.nugget.bungeegui.BungeeGuiPlugin;
import net.md_5.bungee.api.plugin.PluginManager;

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
        Builder pluginManager(final PluginManager pluginManager);

        @BindsInstance
        Builder datafolder(final Path datafolder);

        BungeeGuiComponent build();

    }
}