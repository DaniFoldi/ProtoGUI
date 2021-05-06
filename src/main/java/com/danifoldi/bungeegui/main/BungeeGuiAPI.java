package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.gui.GuiGrid;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.time.Instant;

@Singleton
@SuppressWarnings("unused")
public class BungeeGuiAPI {

    private static BungeeGuiAPI apiInstance;
    public static BungeeGuiAPI getInstance() {
        return apiInstance;
    }
    static void setInstance(BungeeGuiAPI apiInstance) {
        BungeeGuiAPI.apiInstance = apiInstance;
    }

    private final GuiHandler guiHandler;
    private final BungeeGuiLoader loader;

    @Inject
    BungeeGuiAPI(final @NotNull GuiHandler guiHandler,
                 final @NotNull BungeeGuiLoader loader) {
        this.guiHandler = guiHandler;
        this.loader = loader;
    }




    public void openGui(ProxiedPlayer player, String guiName, String target) {
        guiHandler.open(guiName, player, target);
    }

    public void closeGui(ProxiedPlayer player) {
        guiHandler.close(player);
    }

    public boolean hasOpenGui(ProxiedPlayer player) {
        return guiHandler.getOpenGui(player.getUniqueId()) != null;
    }

    public String getOpenGui(ProxiedPlayer player) {
        return guiHandler.getGuiName(guiHandler.getOpenGui(player.getUniqueId()));
    }

    public GuiGrid getGui(String name) {
        return guiHandler.getGui(name);
    }

    public long reloadGuis() {
        final Instant loadStart = Instant.now();

        loader.unload();
        loader.load();

        final Instant loadEnd = Instant.now();
        return Duration.between(loadStart, loadEnd).toMillis();
    }
}
