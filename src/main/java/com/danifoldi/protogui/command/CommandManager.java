package com.danifoldi.protogui.command;

import com.danifoldi.protogui.command.grapefruit.ProxiedPlayerCollectionMapper;
import com.danifoldi.protogui.command.grapefruit.ProxiedPlayerMapper;
import com.danifoldi.protogui.main.BungeeGuiPlugin;
import com.danifoldi.protogui.util.Message;
import com.google.common.reflect.TypeToken;
import grapefruit.command.CommandContainer;
import grapefruit.command.dispatcher.CommandDispatcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@Singleton
public final class CommandManager {
    private final @NotNull ProxiedPlayerMapper playerMapper;
    private final @NotNull ProxiedPlayerCollectionMapper playerCollectionMapper;
    private final Set<CommandContainer> commands;
    private final CommandDispatcher<CommandSender> dispatcher;

    @Inject
    public CommandManager(final @NotNull ProxiedPlayerMapper playerMapper,
                          final @NotNull ProxiedPlayerCollectionMapper playerCollectionMapper,
                          final @NotNull @Named("commands") Set<CommandContainer> commands,
                          final @NotNull ExecutorService threadPool,
                          final @NotNull BungeeGuiPlugin plugin,
                          final @NotNull PluginManager pluginManager) {
        this.playerMapper = playerMapper;
        this.playerCollectionMapper = playerCollectionMapper;
        this.commands = commands;
        this.dispatcher = CommandDispatcher.builder(TypeToken.of(CommandSender.class))
                .withAsyncExecutor(threadPool)
                .withAuthorizer(CommandSender::hasPermission)
                .withMessageProvider(key -> Message.valueOf(key.key().toUpperCase(Locale.ROOT).replace("-", "_")).value())
                .withRegistrationHandler(context -> {
                    final String[] rootAliases = context.route().get(0).split("\\|");
                    if (rootAliases.length == 0) {
                        throw new IllegalArgumentException("No aliases...");
                    }

                    final CommandWrapper wrapper = new CommandWrapper(
                            rootAliases[0],
                            rootAliases.length > 1 ? Arrays.copyOfRange(rootAliases, 1, rootAliases.length) : null,
                            this
                    );

                    pluginManager.registerCommand(plugin, wrapper);
                })
                .withMessenger((source, message) -> source.sendMessage(Message.toComponent(source instanceof ProxiedPlayer ? (ProxiedPlayer)source : null, message)))
                .build();
    }

    public @NotNull CommandDispatcher<CommandSender> getDispatcher() {
        return this.dispatcher;
    }

    public void setup() {
        this.dispatcher.mappers().registerMapper(this.playerMapper);
        this.dispatcher.mappers().registerMapper(this.playerCollectionMapper);
        this.commands.forEach(this.dispatcher::registerCommands);
    }

    public void dispose() {

    }
}
