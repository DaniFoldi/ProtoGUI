package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.gui.GuiGrid;
import com.danifoldi.bungeegui.util.SoundUtil;
import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.InventoryType;
import de.exceptionflug.protocolize.inventory.event.InventoryClickEvent;
import de.exceptionflug.protocolize.inventory.event.InventoryCloseEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final @NotNull ProxiedPlayer player = event.getPlayer();

        final @Nullable GuiGrid openGui = guiHandler.getOpenGui(event.getPlayer().getUniqueId());
        if (openGui == null) {
            return;
        }

        final @NotNull Inventory inventory = event.getInventory();
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

        if (openGui.getItems().get(slot).getClickSound() != null) {
            if (SoundUtil.isValidSound(openGui.getItems().get(slot).getClickSound().getSoundName())) {
                logger.warning("Sound " + openGui.getItems().get(slot).getClickSound().getSoundName() + " is probably invalid");
            }
            openGui.getItems().get(slot).getClickSound().playFor(player);
        }

        if (openGui.getItems().get(slot).getCommands().isEmpty()) {
            return;
        }

        final @NotNull String target = guiHandler.getGuiTarget(player.getUniqueId());

        guiHandler.runCommand(player,openGui, slot, target);
        guiHandler.close(player, true);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onInventoryClose(final @NotNull InventoryCloseEvent event) {
        guiHandler.close(event.getPlayer(), false);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onDisconnect(final @NotNull PlayerDisconnectEvent event) {
        guiHandler.close(event.getPlayer(), true);
    }
}