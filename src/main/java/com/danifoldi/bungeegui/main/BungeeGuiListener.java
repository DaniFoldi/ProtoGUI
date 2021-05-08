package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.gui.GuiGrid;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryType;
import de.exceptionflug.protocolize.inventory.event.InventoryClickEvent;
import de.exceptionflug.protocolize.inventory.event.InventoryCloseEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class BungeeGuiListener implements Listener {
    private final GuiHandler guiHandler;

    @Inject
    public BungeeGuiListener(final @NotNull GuiHandler guiHandler) {
        this.guiHandler = guiHandler;
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final ProxiedPlayer player = event.getPlayer();

        final GuiGrid openGui = guiHandler.getOpenGui(event.getPlayer().getUniqueId());
        if (openGui == null) {
            return;
        }

        final Inventory inventory = event.getInventory();
        final int slot = event.getSlot();

        if (inventory.getType().equals(InventoryType.PLAYER)) {
            return;
        }
        if (slot == -999) {
            return;
        }

        if (inventory.getItem(slot) == null) {
            return;
        }

        if (openGui.getItems().get(slot).getCommands().isEmpty()) {
            return;
        }

        final String target = guiHandler.getGuiTarget(player.getUniqueId());

        guiHandler.runCommand(player,openGui, slot, target);
        guiHandler.close(player);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onInventoryClose(final @NotNull InventoryCloseEvent event) {
        guiHandler.close(event.getPlayer());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onDisconnect(final @NotNull PlayerDisconnectEvent event) {
        guiHandler.close(event.getPlayer());
    }
}