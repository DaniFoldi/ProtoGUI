package com.danifoldi.protogui.command;

import com.danifoldi.protogui.command.grapefruit.GuiMapper;
import com.danifoldi.protogui.command.grapefruit.ProtoPlayerCollectionMapper;
import com.danifoldi.protogui.command.grapefruit.ProtoPlayerMapper;
import com.danifoldi.protogui.command.grapefruit.ProtoServerMapper;
import com.danifoldi.protogui.main.ProtoGuiAPI;
import com.danifoldi.protogui.platform.PlatformInteraction;
import com.danifoldi.protogui.util.Message;
import com.google.common.reflect.TypeToken;
import grapefruit.command.CommandContainer;
import grapefruit.command.dispatcher.CommandDispatcher;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

@Singleton
public final class CommandManager {
    private final @NotNull Set<CommandContainer> commands;
    private final @NotNull ExecutorService threadPool;
    private final @NotNull Logger logger;
    private @NotNull CommandDispatcher<PlatformInteraction.ProtoSender> dispatcher;

    @Inject
    public CommandManager(final @NotNull @Named("commands") Set<CommandContainer> commands,
                          final @NotNull ExecutorService threadPool,
                          final @NotNull Logger logger) {
        this.commands = commands;
        this.threadPool = threadPool;
        this.logger = logger;
    }

    public @NotNull CommandDispatcher<PlatformInteraction.ProtoSender> getDispatcher() {
        return this.dispatcher;
    }

    public void setup() {
        //noinspection UnstableApiUsage
        this.dispatcher = CommandDispatcher.builder(TypeToken.of(PlatformInteraction.ProtoSender.class))
                .withAsyncExecutor(threadPool)
                .withAuthorizer(PlatformInteraction.ProtoSender::hasPermission)
                .withMessageProvider(key -> Message.find(key.key()).value())
                .withMessenger(PlatformInteraction.ProtoSender::send)
                .withRegistrationHandler(context -> {
                    final String[] rootAliases = context.route().get(0).split("\\|");
                    if (rootAliases.length == 0) {
                        throw new IllegalArgumentException("No aliases...");
                    }

                    ProtoGuiAPI.getInstance().getPlatform().registerCommand(List.of(rootAliases), getDispatcher()::dispatchCommand, getDispatcher()::listSuggestions);
                })
                .build();
        this.dispatcher.mappers().registerMapper(new ProtoPlayerMapper());
        this.dispatcher.mappers().registerMapper(new ProtoPlayerCollectionMapper());
        this.dispatcher.mappers().registerMapper(new ProtoServerMapper());
        this.dispatcher.mappers().registerMapper(new GuiMapper());
        this.commands.forEach(c -> {
            try {
                this.dispatcher.registerCommands(c);
            } catch (Exception e) {
                logger.warning("Failed to register command");
                logger.warning(e.getMessage());
            }
        });
    }

    public void dispose() {

    }
}
