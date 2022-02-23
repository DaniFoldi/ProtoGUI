package com.danifoldi.protogui.main;

import com.danifoldi.protogui.gui.GuiGrid;
import com.danifoldi.protogui.platform.PlatformInteraction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Singleton
@SuppressWarnings("unused")
public class ProtoGuiAPI {

    private static @Nullable ProtoGuiAPI apiInstance;
    public static @NotNull ProtoGuiAPI getInstance() {
        if (apiInstance == null) {
            throw new RuntimeException("Cannot access API while plugin is not loaded");
        }

        return apiInstance;
    }
    static void setInstance(final @Nullable ProtoGuiAPI apiInstance) {
        ProtoGuiAPI.apiInstance = apiInstance;
    }


    private final @NotNull GuiHandler guiHandler;
    private final @NotNull ProtoGuiLoader loader;
    private final @NotNull PlaceholderHandler placeholderHandler;
    private final @NotNull PlatformInteraction platform;

    @Inject
    ProtoGuiAPI(final @NotNull GuiHandler guiHandler,
                final @NotNull ProtoGuiLoader loader,
                final @NotNull PlaceholderHandler placeholderHandler) {
        this.guiHandler = guiHandler;
        this.loader = loader;
        this.placeholderHandler = placeholderHandler;
        this.platform = loader.platform;
    }


    /**
     * Internally used API for platform-specific interactions
     * @hidden functions in this class are not guaranteed to remain compatible across versions
     */
    public @NotNull PlatformInteraction getPlatform() {
        return platform;
    }

    /**
     * Opens a GUI for a player
     *
     * @param uuid - the user that the GUI will open for
     * @param guiName - the name of the GUI to open
     * @param target - target value for the GUI, pass any {@link String} for untargeted GUIs
     */
    public void openGui(final @NotNull UUID uuid, final @NotNull String guiName, final @NotNull String target) {
        guiHandler.open(getGui(guiName), uuid, target);
    }

    /**
     * Opens a GUI for a player
     *
     * @param uuid - the user that the GUI will open for
     * @param gui - the name GUI to open
     * @param target - target value for the GUI, pass any {@link String} for untargeted GUIs
     */
    public void openGui(final @NotNull UUID uuid, final @NotNull GuiGrid gui, final @NotNull String target) {
        guiHandler.open(gui, uuid, target);
    }

    /**
     * Closes the open GUI for a player
     * @param uuid - the user to force close the GUI for
     */
    public void closeGui(final @NotNull UUID uuid) {
        guiHandler.close(uuid, true);
    }

    /**
     * Get whether the player has an open GUI
     * @param uuid - the user to check
     * @return true if the player has an open GUI
     */
    public boolean hasOpenGui(final @NotNull UUID uuid) {
        return guiHandler.getOpenGui(uuid) != null;
    }

    /**
     * Get the open GUI name for a player
     * @param uuid - the user to check
     * @return the GUI name, or null if player has no open GUI
     */
    public @Nullable String getOpenGui(final @NotNull UUID uuid) {
        return guiHandler.getGuiName(guiHandler.getOpenGui(uuid));
    }

    /**
     * Get the GUI with a name
     * @param name - the GUI name to retrieve
     * @return the {@link GuiGrid} with the name
     */
    public @Nullable GuiGrid getGui(final @NotNull String name) {
        return guiHandler.getGui(name);
    }

    /**
     * Get all loaded GUI names
     * @return {@link List<String>} with the GUI names
     */
    public @NotNull Collection<String> getLoadedGuis() {
        return guiHandler.getGuis();
    }

    /**
     * Adds a GUI to the handler
     * @implNote if a GUI with this name exists, no operation is performed
     * @param name - the name of the GUI to be added
     * @param gui - the GUI to be added
     */
    public void addGui(final @NotNull String name, final @NotNull GuiGrid gui) {
        guiHandler.addGui(name, gui);
    }

    /**
     * Removes a GUI from the handler
     * @implNote the GUI is closed for all players immediately
     * @param name - the GUI to be removed
     */
    public void removeGui(final @NotNull String name) {
        guiHandler.removeGui(name);
    }

    /**
     * Reload the plugin and all GUIs
     * @apiNote all GUIs added via the API are lost
     * @return the time the reload took in ms
     */
    public long reloadGuis() {
        final @NotNull Instant loadStart = Instant.now();

        loader.unload(true);
        loader.load(true);

        final @NotNull Instant loadEnd = Instant.now();
        return Duration.between(loadStart, loadEnd).toMillis();
    }

    /**
     * Update hotbar action items
     * @param uuid - the user to update
     */
    public void updateActions(UUID uuid) {
        guiHandler.updateActions(uuid);
    }

    /**
     * Handle interaction with hotbar action item
     * @param uuid - the user that interacted
     * @param slot - the slot they interacted with
     */
    public void handleActions(UUID uuid, int slot) {
        guiHandler.handleActions(uuid, slot);
    }

    /**
     * Register a custom placeholder for use later
     * @param name - the name of the placeholder without % symbols
     * @param placeholder - the function to be called on the player when the placeholder is requested
     * @implNote if the function returns null, the placeholder isn't parsed, the argument can be null if the parse target is the console
     */
    public void registerPlaceholder(final @NotNull String name, final @NotNull Function<UUID, String> placeholder) {
        placeholderHandler.register(name, placeholder);
    }

    /**
     * Unregister a custom placeholder
     * @param name - the name of the placeholder to unregister
     */
    public void unregisterPlaceholder(final @NotNull String name) {
        placeholderHandler.unregister(name);
    }

    /**
     * Replace placeholders with their values
     * @param uuid - the user to look up placeholders for
     * @param text - the text to replace placeholders in
     * @return the text with the placeholders replaced
     */
    public @NotNull String parsePlaceholders(final @Nullable UUID uuid, final @NotNull String text) {
        return placeholderHandler.parse(uuid, text);
    }
}
