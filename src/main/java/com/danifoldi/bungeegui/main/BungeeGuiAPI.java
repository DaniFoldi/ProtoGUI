package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.gui.GuiGrid;
import com.danifoldi.protogui.main.ProtoGuiAPI;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Deprecated(forRemoval = true)
public class BungeeGuiAPI {

    private static BungeeGuiAPI instance;

    @Deprecated(forRemoval = true)
    public BungeeGuiAPI getInstance() {
        return instance;
    }

    static {
        instance = new BungeeGuiAPI();
    }


    /**
     * Opens a GUI for a player
     *
     * @param player - the player that the GUI will open for
     * @param guiName - the name of the GUI to open
     * @param target - target value for the GUI, pass any {@link String} for untargeted
     */
    @Deprecated(forRemoval = true)
    public void openGui(final @NotNull ProxiedPlayer player, final @NotNull String guiName, final @NotNull String target) {
        ProtoGuiAPI.getInstance().openGui(player.getUniqueId(), guiName, target);
    }

    /**
     * Closes the open GUI for a player
     * @param player - the player to force close the GUI for
     */
    @Deprecated(forRemoval = true)
    public void closeGui(final @NotNull ProxiedPlayer player) {
        ProtoGuiAPI.getInstance().closeGui(player.getUniqueId());
    }

    /**
     * Get whether the player has an open GUI
     * @param player - the player to check
     * @return true if the player has an open GUI
     */
    @Deprecated(forRemoval = true)
    public boolean hasOpenGui(final @NotNull ProxiedPlayer player) {
        return ProtoGuiAPI.getInstance().hasOpenGui(player.getUniqueId());
    }

    /**
     * Get the open GUI name for a player
     * @param player - the player to check
     * @return the GUI name, or null if has no open GUI
     */
    @Deprecated(forRemoval = true)
    public @Nullable String getOpenGui(final @NotNull ProxiedPlayer player) {
        return ProtoGuiAPI.getInstance().getOpenGui((player.getUniqueId()));
    }

    /**
     * Get the GUI with a name
     * @param name - the GUI name to retrieve
     * @return the {@link GuiGrid} with the name
     */
    @Deprecated(forRemoval = true)
    public @Nullable GuiGrid getGui(final @NotNull String name) {
        return GuiGrid.fromNew(ProtoGuiAPI.getInstance().getGui(name));
    }

    /**
     * Get all loaded GUI names
     * @return {@link List<String>} with the GUI names
     */
    @Deprecated(forRemoval = true)
    public @NotNull List<String> getAvailableGuis() {
        return new ArrayList<>(ProtoGuiAPI.getInstance().getLoadedGuis());
    }

    /**
     * Adds a GUI to the handler
     * @implNote if a GUI with this name exists, no operation is performed
     * @param name - the name of the GUI to be added
     * @param gui - the GUI to be added
     */
    @Deprecated(forRemoval = true)
    public void addGui(final @NotNull String name, final @NotNull GuiGrid gui) {
        ProtoGuiAPI.getInstance().addGui(name, GuiGrid.toNew(gui));
    }

    /**
     * Removes a GUI from the handler
     * @implNote the GUI is closed for all players immediately
     * @param name - the GUI to be removed
     */
    @Deprecated(forRemoval = true)
    public void removeGui(final @NotNull String name) {
        ProtoGuiAPI.getInstance().removeGui(name);
    }

    /**
     * Reload the plugin and all GUIs
     * @apiNote all GUIs added via the API are lost
     * @return the time the reload took in ms
     */
    @Deprecated(forRemoval = true)
    public long reloadGuis() {
        return ProtoGuiAPI.getInstance().reloadGuis();
    }

    /**
     * Register a custom placeholder for use later
     * @param name - the name of the placeholder without % symbols
     * @param placeholder - the function to be called on the player when the placeholder is requested
     * @implNote if the function returns null, the placeholder isn't parsed, the argument can be null if the parse target is the console
     */
    @Deprecated(forRemoval = true)
    public void registerPlaceholder(final @NotNull String name, final @NotNull Function<ProxiedPlayer, String> placeholder) {
        Function<UUID, ProxiedPlayer> getPlayer = ProxyServer.getInstance()::getPlayer;
        ProtoGuiAPI.getInstance().registerPlaceholder(name, getPlayer.andThen(placeholder));
    }

    /**
     * Unregister a custom placeholder
     * @param name - the name of the placeholder to unregister
     */
    @Deprecated(forRemoval = true)
    public void unregisterPlaceholder(final @NotNull String name) {
        ProtoGuiAPI.getInstance().unregisterPlaceholder(name);
    }

    /**
     * Replace placeholders with their values
     * @param player - the player to look up placeholders for
     * @param text - the text to replace placeholders in
     * @return the text with the placeholders replaced
     */
    @Deprecated(forRemoval = true)
    public @NotNull String parsePlaceholders(final @Nullable ProxiedPlayer player, final @NotNull String text) {
        return ProtoGuiAPI.getInstance().parsePlaceholders(player.getUniqueId(), text);
    }
}
