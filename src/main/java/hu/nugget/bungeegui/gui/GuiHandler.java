package hu.nugget.bungeegui.gui;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class GuiHandler {
    private final Map<String, GuiGrid> menus = new HashMap<>();

    @Inject
    public GuiHandler() {}

    public @NotNull Optional<GuiGrid> getGui(final @NotNull String name) {
        return Optional.ofNullable(this.menus.get(name));
    }
}
