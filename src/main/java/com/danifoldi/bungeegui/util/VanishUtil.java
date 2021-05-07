package com.danifoldi.bungeegui.util;

import de.myzelyam.api.vanish.BungeeVanishAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class VanishUtil {
    public static boolean isVanished(ProxiedPlayer player) {
        Plugin premiumvanishPlugin = ProxyServer.getInstance().getPluginManager().getPlugin("PremiumVanish");
        if (premiumvanishPlugin == null) {
            return false;
        }

        return BungeeVanishAPI.isInvisible(player);
    }

    private VanishUtil() {
        throw new UnsupportedOperationException();
    }
}
