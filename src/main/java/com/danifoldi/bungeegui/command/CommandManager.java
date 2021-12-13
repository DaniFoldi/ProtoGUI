package com.danifoldi.bungeegui.command;

import com.danifoldi.bungeegui.command.grapefruit.ChatBridgeMessageProvider;
import com.danifoldi.bungeegui.command.grapefruit.ProxiedPlayerMapper;
import com.danifoldi.bungeegui.main.BungeeGuiPlugin;
import com.danifoldi.bungeegui.util.Message;
import com.danifoldi.messagelib.core.MessageBuilder;
import com.google.common.reflect.TypeToken;
import grapefruit.command.CommandContainer;
import grapefruit.command.dispatcher.CommandDispatcher;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ExecutorService;

@Singleton
public final class CommandManager {
    private final ProxiedPlayerMapper playerMapper;
    private final Set<CommandContainer> commands;
    private final CommandDispatcher<CommandSender> dispatcher;

    @Inject
    public CommandManager(final @NotNull ProxiedPlayerMapper playerMapper,
                          final @NotNull ChatBridgeMessageProvider messageProvider,
                          final @NotNull @Named("commands") Set<CommandContainer> commands,
                          final @NotNull ExecutorService threadPool,
                          final @NotNull BungeeGuiPlugin plugin) {
        this.playerMapper = playerMapper;
        this.commands = commands;
        this.dispatcher = CommandDispatcher.builder(TypeToken.of(CommandSender.class))
                .withAsyncExecutor(threadPool)
                .withAuthorizer(CommandSender::hasPermission)
                .withMessageProvider(messageProvider)
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

                    ProxyServer.getInstance().getPluginManager().registerCommand(plugin, wrapper);
                })
                .withMessenger((source, message) -> source.sendMessage(Message.toComponent(source instanceof ProxiedPlayer ? (ProxiedPlayer)source : null, message)))
                .build();
    }

    public @NotNull CommandDispatcher<CommandSender> getDispatcher() {
        return this.dispatcher;
    }

    public void setup() {
        this.dispatcher.mappers().registerMapper(this.playerMapper);
        this.commands.forEach(this.dispatcher::registerCommands);
    }

    public void dispose() {

    }
}
