package com.danifoldi.bungeegui.main;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.logging.Logger;

public class BungeeGuiListener implements Listener {
    private final @NotNull GuiHandler guiHandler;
    private final @NotNull Logger logger;

    @Inject
    public BungeeGuiListener(final @NotNull GuiHandler guiHandler,
                             final @NotNull Logger logger) {
        this.guiHandler = guiHandler;
        this.logger = logger;
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onServerSwitch(final @NotNull ServerSwitchEvent event) {
        guiHandler.actions(event.getPlayer());
    }

    /*public void onItemClick(final @NotNull PlayerInteractEvent event) {
        if (guiHandler.getOpenGui(event.getPlayer().getUniqueId()) != null) {
            return;
        }

        PlayerInventory inventory = InventoryManager.getInventory(event.getPlayer().getUniqueId());
        guiHandler.interact(event.getPlayer(), inventory.getHeldItem());
    }*/

    @EventHandler
    @SuppressWarnings("unused")
    public void onDisconnect(final @NotNull PlayerDisconnectEvent event) {
        guiHandler.close(event.getPlayer(), true);
    }
}