package hu.nugget.bungeegui.inject;

import dagger.BindsInstance;
import dagger.Component;
import hu.nugget.bungeegui.BungeeGuiLoader;
import hu.nugget.bungeegui.BungeeGuiPlugin;

import javax.inject.Singleton;

@Singleton
@Component
public interface BungeeGuiComponent {

    BungeeGuiLoader loader();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder plugin(final BungeeGuiPlugin plugin);

        BungeeGuiComponent build();
    }
}