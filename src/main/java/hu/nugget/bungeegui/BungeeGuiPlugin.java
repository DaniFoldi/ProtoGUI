package hu.nugget.bungeegui;

import hu.nugget.bungeegui.inject.BungeeGuiComponent;
import hu.nugget.bungeegui.inject.DaggerBungeeGuiComponent;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeGuiPlugin extends Plugin {
    private BungeeGuiLoader loader;

    @Override
    public void onEnable() {
        final BungeeGuiComponent component = DaggerBungeeGuiComponent.builder()
                .plugin(this)
                .build();
        this.loader = component.loader();
        this.loader.load();
    }

    @Override
    public void onDisable() {
        this.loader.unload();
    }
}
