package com.danifoldi.protogui.platform.bungee;

import com.danifoldi.protogui.platform.PlatformInteraction;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class CommandWrapper extends Command implements TabExecutor {
    private final @NotNull ProtoGui protoGui;
    private final @NotNull BiConsumer<PlatformInteraction.ProtoSender, String> dispatch;
    private final @NotNull BiFunction<PlatformInteraction.ProtoSender, String, Collection<String>> suggest;

    CommandWrapper(final @NotNull List<String> aliases,
                   final @NotNull BiConsumer<PlatformInteraction.ProtoSender, String> dispatch,
                   final @NotNull BiFunction<PlatformInteraction.ProtoSender, String, Collection<String>> suggest,
                   final @NotNull ProtoGui protoGui) {
        super(aliases.stream().findFirst().orElseThrow(), null, aliases.stream().skip(1).toArray(String[]::new));
        this.dispatch = dispatch;
        this.suggest = suggest;
        this.protoGui = protoGui;
    }

    CommandWrapper(final @NotNull List<String> aliases,
                   final @NotNull String permission,
                   final @NotNull BiConsumer<PlatformInteraction.ProtoSender, String> dispatch,
                   final @NotNull BiFunction<PlatformInteraction.ProtoSender, String, Collection<String>> suggest,
                   final @NotNull ProtoGui protoGui) {
        super(aliases.stream().findFirst().orElseThrow(), permission, aliases.stream().skip(1).toArray(String[]::new));
        this.dispatch = dispatch;
        this.suggest = suggest;
        this.protoGui = protoGui;
    }

    @Override
    public void execute(final @NotNull CommandSender sender, @NotNull final String[] args) {
        PlatformInteraction.ProtoSender protoSender = protoGui.senderGenerator.apply(sender);
        if (sender instanceof ProxiedPlayer player) {
            protoSender = protoGui.playerGenerator.apply(player);
        }
        dispatch.accept(protoSender, buildArguments(args));
    }

    @Override
    public @NotNull Iterable<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull String[] args) {
        PlatformInteraction.ProtoSender protoSender = protoGui.senderGenerator.apply(sender);
        if (sender instanceof ProxiedPlayer player) {
            protoSender = protoGui.playerGenerator.apply(player);
        }
        return suggest.apply(protoSender, buildArguments(args));
    }

    private @NotNull String buildArguments(final @NotNull String[] args) {
        final StringJoiner joiner = new StringJoiner(" ");
        joiner.add(getName());
        Arrays.stream(args).forEach(joiner::add);
        return joiner.toString();
    }
}
