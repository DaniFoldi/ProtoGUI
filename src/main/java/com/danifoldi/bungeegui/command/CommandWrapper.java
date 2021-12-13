package com.danifoldi.bungeegui.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.StringJoiner;

import static java.util.Objects.requireNonNull;

public class CommandWrapper extends Command implements TabExecutor {
    private final CommandManager commandManager;

    CommandWrapper(final @NotNull String name,
                   final @Nullable String[] aliases,
                   final @NotNull CommandManager commandManager) {
        super(name, null, aliases != null ? aliases : new String[0]);
        this.commandManager = requireNonNull(commandManager, "commandManager cannot be null");
    }

    @Override
    public void execute(final @NotNull CommandSender sender, @NotNull final String[] args) {
        this.commandManager.getDispatcher().dispatchCommand(sender, buildArguments(args));
    }

    @Override
    public @NotNull Iterable<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull String[] args) {
        return this.commandManager.getDispatcher().listSuggestions(sender, buildArguments(args));
    }

    private @NotNull String buildArguments(final @NotNull String[] args) {
        final StringJoiner joiner = new StringJoiner(" ");
        joiner.add(getName());
        Arrays.stream(args).forEach(joiner::add);
        return joiner.toString();
    }
}
