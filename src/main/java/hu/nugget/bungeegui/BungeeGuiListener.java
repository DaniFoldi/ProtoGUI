package hu.nugget.bungeegui;

import de.exceptionflug.protocolize.inventory.Inventory;
import de.exceptionflug.protocolize.inventory.event.InventoryClickEvent;
import hu.nugget.bungeegui.gui.GuiHandler;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
    public void onInventoryClick(final @NotNull InventoryClickEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final Inventory inv = event.getInventory();
        final int slot = event.getSlot();


    }
}