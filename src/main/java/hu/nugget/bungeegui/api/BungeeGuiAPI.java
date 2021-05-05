package hu.nugget.bungeegui.api;

import hu.nugget.bungeegui.gui.GuiHandler;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@SuppressWarnings("unused")
public class BungeeGuiAPI {

    private static BungeeGuiAPI apiInstance;
    public static BungeeGuiAPI getInstance() {
        return apiInstance;
    }
    protected static void setInstance(BungeeGuiAPI apiInstance) {
        BungeeGuiAPI.apiInstance = apiInstance;
    }

    private final GuiHandler guiHandler;

    @Inject
    public BungeeGuiAPI(GuiHandler guiHandler) {
        this.guiHandler = guiHandler;
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
}
