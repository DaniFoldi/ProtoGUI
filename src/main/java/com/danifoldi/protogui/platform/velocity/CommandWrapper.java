package com.danifoldi.protogui.platform.velocity;

import com.danifoldi.protogui.platform.PlatformInteraction;
import com.velocitypowered.api.command.RawCommand;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class CommandWrapper implements RawCommand {
    private final @NotNull ProtoGui protoGui;
    private final @NotNull BiConsumer<PlatformInteraction.ProtoSender, String> dispatch;
    private final @NotNull BiFunction<PlatformInteraction.ProtoSender, String, Collection<String>> suggest;
    private final @NotNull ExecutorService threadPool;
    private final @NotNull String name;

    CommandWrapper(final @NotNull BiConsumer<PlatformInteraction.ProtoSender, String> dispatch,
                   final @NotNull BiFunction<PlatformInteraction.ProtoSender, String, Collection<String>> suggest,
                   final @NotNull ProtoGui protoGui,
                   final @NotNull ExecutorService threadPool,
                   final @NotNull String name) {
        this.dispatch = dispatch;
        this.suggest = suggest;
        this.protoGui = protoGui;
        this.threadPool = threadPool;
        this.name = name;
    }

    private @NotNull String buildArguments(final @NotNull String args) {
        final StringJoiner joiner = new StringJoiner(" ");
        joiner.add(name);
        joiner.add(args);
        return joiner.toString();
    }

    @Override
    public void execute(Invocation invocation) {
        dispatch.accept(protoGui.senderGenerator.apply(invocation.source()), buildArguments(invocation.arguments()));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return suggest.apply(protoGui.senderGenerator.apply(invocation.source()), buildArguments(invocation.arguments())).stream().toList();
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return CompletableFuture.supplyAsync(() -> suggest.apply(protoGui.senderGenerator.apply(invocation.source()), buildArguments(invocation.arguments())).stream().toList(), threadPool);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true;
    }
}
