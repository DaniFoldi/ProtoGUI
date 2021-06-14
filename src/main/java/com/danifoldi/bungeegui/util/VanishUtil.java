package com.danifoldi.bungeegui.util;

import de.myzelyam.api.vanish.BungeeVanishAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VanishUtil {
    public static boolean isVanished(final @NotNull ProxiedPlayer player) {
        final @Nullable Plugin premiumvanishPlugin = ProxyServer.getInstance().getPluginManager().getPlugin("PremiumVanish");
        if (premiumvanishPlugin == null) {
            return false;
        }

        return BungeeVanishAPI.isInvisible(player);
    }

    private VanishUtil() {
        throw new UnsupportedOperationException();
    }
}
