package com.danifoldi.protogui.platform.velocity;

import com.danifoldi.protogui.inject.DaggerProtoGuiComponent;
import com.danifoldi.protogui.inject.ProtoGuiComponent;
import com.danifoldi.protogui.main.ProtoGuiAPI;
import com.danifoldi.protogui.main.ProtoGuiLoader;
import com.danifoldi.protogui.platform.PlatformInteraction;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.EventTask;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.plugin.meta.PluginDependency;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.scheduler.ScheduledTask;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Plugin(authors = {"DaniFoldi", "HgeX"},
dependencies = {
        @Dependency(id = "Protocolize"),
        @Dependency(id = "LuckPerms", optional = true),
        @Dependency(id = "PremiumVanish", optional = true)
},
description = "Create GUIs that perform commands on behalf of the player",
id = "protogui",
version = "@version@"
)
public class ProtoGui {

    private @Nullable ProtoGuiLoader loader;
    private @Nullable ScheduledTask pingTask;

    private final @NotNull ProxyServer server;
    private final @NotNull Path datafolder;
    private final @NotNull ExecutorService threadPool = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
            .setNameFormat(getClass().getSimpleName() + " Async Pool - #%1$d")
            .setDaemon(false)
            .build());

    private final @NotNull Map<String, CommandWrapper> registeredCommands = new ConcurrentHashMap<>();
    private final @NotNull Map<RegisteredServer, ServerPing> lastResponse = new ConcurrentHashMap<>();

    public ProtoGui(final @NotNull ProxyServer server, @DataDirectory Path datafolder) {
        this.server = server;
        this.datafolder = datafolder;
    }

    @Subscribe
    public void onInitialize(ProxyInitializeEvent event) {
        final @NotNull ProtoGuiComponent component = DaggerProtoGuiComponent.builder()
                .logger(Logger.getLogger("ProtoGUI"))
                .datafolder(datafolder)
                .threadPool(threadPool)
                .platformInteraction(platform)
                .build();

        this.loader = component.loader();
        this.loader.load();
    }

    @Subscribe
    public void onShutdown(ProxyShutdownEvent event) {
        if (this.loader == null) {
            return;
        }
        this.loader.unload();
    }

    final @NotNull Function<CommandSource, PlatformInteraction.ProtoSender> senderGenerator = commandSource -> new PlatformInteraction.ProtoSender() {
        @Override
        public boolean hasPermission(String permission) {
            return commandSource.hasPermission(permission);
        }

        @Override
        public void send(String message) {
            commandSource.sendMessage(Component.text(message));
        }

        @Override
        public String displayName() {
            if (commandSource instanceof Player player) {
                // todo displayname
                return player.getUsername();
            }
            return "CONSOLE";
        }

        @Override
        public String name() {
            if (commandSource instanceof Player player) {
                return player.getUsername();
            }
            return "CONSOLE";
        }

        @Override
        public UUID uniqueId() {
            if (commandSource instanceof Player player) {
                return player.getUniqueId();
            }
            return UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        }
    };

    final @NotNull Function<RegisteredServer, PlatformInteraction.ProtoServer> serverGenerator = registeredServer -> new PlatformInteraction.ProtoServer() {
        @Override
        public String name() {
            return registeredServer.getServerInfo().getName();
        }

        @Override
        public String motd() {
            // todo motd properly, or at least optional.orelse
            return lastResponse.get(registeredServer).getDescriptionComponent().toString();
        }

        @Override
        public Collection<PlatformInteraction.ProtoPlayer> players() {
            return registeredServer.getPlayersConnected().stream().map(playerGenerator).toList();
        }

        @Override
        public boolean online() {
            return lastResponse.containsKey(registeredServer);
        }

        @Override
        public String version() {
            return Optional.ofNullable(lastResponse.get(registeredServer)).map(p -> p.getVersion().getName()).orElse("");
        }

        @Override
        public int playerMax() {
            return Optional.ofNullable(lastResponse.get(registeredServer)).map(p -> p.getPlayers().orElse(null).getMax()).orElse(0);
        }

        @Override
        public int playerCount() {
            return Optional.ofNullable(lastResponse.get(registeredServer)).map(p -> p.getPlayers().orElse(null).getOnline()).orElse(0);
        }
    };

    final @NotNull Function<Player, PlatformInteraction.ProtoPlayer> playerGenerator = player -> new PlatformInteraction.ProtoPlayer() {
        @Override
        public boolean vanished() {
            // todo implement once PV supports velocity
            return false;
        }

        @Override
        public int protocol() {
            return player.getProtocolVersion().getProtocol();
        }

        @Override
        public int ping() {
            return (int)player.getPing();
        }

        @Override
        public String locale() {
            return player.getEffectiveLocale().getDisplayName();
        }

        @Override
        public void run(String command) {
            server.getCommandManager().executeAsync(player, command);
        }

        @Override
        public void actionbar(String message) {
            player.sendActionBar(Component.text(message));
        }

        @Override
        public void chat(String message) {
            player.spoofChatInput(message);
        }

        @Override
        public void connect(PlatformInteraction.ProtoServer pServer) {
            player.createConnectionRequest(server.getServer(pServer.name()).orElse(null)).fireAndForget();
        }

        @Override
        public PlatformInteraction.ProtoServer connectedTo() {
            return serverGenerator.apply(player.getCurrentServer().map(ServerConnection::getServer).orElse(null));
        }

        @Override
        public void title(String message, int fadeIn, int stay, int fadeOut) {
            // todo send title
        }

        @Override
        public void subtitle(String message, int fadeIn, int stay, int fadeOut) {
            // todo send subtitle
        }

        @Override
        public boolean hasPermission(String permission) {
            return player.hasPermission(permission);
        }

        @Override
        public void send(String message) {
            player.sendMessage(Component.text(message));
        }

        @Override
        public String displayName() {
            // todo displayname
            return player.getUsername();
        }

        @Override
        public String name() {
            return player.getUsername();
        }

        @Override
        public UUID uniqueId() {
            return player.getUniqueId();
        }
    };

    final @NotNull Function<PluginContainer, PlatformInteraction.ProtoPlugin> pluginGenerator = plugin -> new PlatformInteraction.ProtoPlugin() {
        @Override
        public String name() {
            return plugin.getDescription().getName().orElse(null);
        }

        @Override
        public String description() {
            return plugin.getDescription().getDescription().orElse(null);
        }

        @Override
        public String main() {
            // todo implement
            return plugin.getDescription().getSource().toString();
        }

        @Override
        public String version() {
            return plugin.getDescription().getVersion().orElse(null);
        }

        @Override
        public List<String> authors() {
            return plugin.getDescription().getAuthors();
        }

        @Override
        public List<String> dependencies() {
            return plugin.getDescription().getDependencies().stream().filter(p -> !p.isOptional()).map(PluginDependency::getId).toList();
        }

        @Override
        public List<String> softDependencies() {
            return plugin.getDescription().getDependencies().stream().filter(PluginDependency::isOptional).map(PluginDependency::getId).toList();
        }
    };

    final @NotNull PlatformInteraction platform = new PlatformInteraction() {
        @Override
        public ProtoPlayer getPlayer(UUID uuid) {
            return playerGenerator.apply(server.getPlayer(uuid).orElse(null));
        }

        @Override
        public ProtoPlayer getPlayer(String name) {
            return playerGenerator.apply(server.getPlayer(name).orElse(null));
        }

        @Override
        public List<ProtoPlayer> getPlayers() {
            return server.getAllPlayers().stream().map(playerGenerator).toList();
        }

        @Override
        public String platformName() {
            return server.getVersion().getName();
        }

        @Override
        public String platformVersion() {
            return server.getVersion().toString();
        }

        @Override
        public String pluginName() {
            return server.getPluginManager().getPlugin("protogui").get().getDescription().getName().orElse("");
        }

        @Override
        public String pluginVersion() {
            return server.getPluginManager().getPlugin("protogui").get().getDescription().getVersion().orElse("");
        }

        @Override
        public int maxPlayerCount() {
            return server.getConfiguration().getShowMaxPlayers();
        }

        @Override
        public void setup() {
            if (pingTask != null) {
                pingTask.cancel();
                pingTask = null;
            }
            lastResponse.clear();
            registeredCommands.clear();
            pingTask = server.getScheduler().buildTask(this, () -> {
                server.getAllServers().forEach(server -> server.ping().thenAcceptAsync(ping -> {
                    // todo implement timeout with remove
                    lastResponse.put(server, ping);
                }, threadPool));
            }).delay(5, TimeUnit.SECONDS).repeat(5, TimeUnit.SECONDS).schedule();

            Protocolize.playerProvider().onConstruct(protocolizePlayer -> {
                protocolizePlayer.onInteract(event -> {
                    if (ProtoGuiAPI.getInstance().hasOpenGui(protocolizePlayer.uniqueId())) {
                        return;
                    }

                    PlayerInventory inventory = protocolizePlayer.proxyInventory();
                    ProtoGuiAPI.getInstance().handleActions(protocolizePlayer.uniqueId(), inventory.heldItem());
                });
            });
        }

        @Override
        public void teardown() {
            if (pingTask != null) {
                pingTask.cancel();
                pingTask = null;
            }
            lastResponse.clear();

            registeredCommands.keySet().forEach(c -> {
                server.getCommandManager().unregister(c);
            });
            registeredCommands.clear();
        }

        @Override
        public void registerCommand(List<String> commandAliases, BiConsumer<ProtoSender, String> dispatch, BiFunction<ProtoSender, String, Collection<String>> suggest) {
            CommandWrapper commandWrapper = new CommandWrapper(dispatch, suggest, ProtoGui.this, threadPool, commandAliases.stream().findFirst().orElseThrow());
            registeredCommands.put(commandAliases.stream().findFirst().orElseThrow(), commandWrapper);
            server.getCommandManager().register(commandAliases.stream().findFirst().orElseThrow(), commandWrapper, commandAliases.stream().skip(1).toArray(String[]::new));
        }

        @Override
        public void unregisterCommand(String command) {
            registeredCommands.remove(command);
            server.getCommandManager().unregister(command);
        }

        @Override
        public void runConsoleCommand(String command) {
            server.getCommandManager().executeAsync(server.getConsoleCommandSource(), command);
        }

        @Override
        public List<ProtoPlugin> getPlugins() {
            return server.getPluginManager().getPlugins().stream().map(pluginGenerator).toList();
        }

        @Override
        public Map<String, ProtoServer> getServers() {
            return server.getAllServers()
                    .stream()
                    .collect(Collectors.toMap(s -> s.getServerInfo().getName(), serverGenerator));
        }
    };

    @Subscribe
    public EventTask onServerSwitch(final @NotNull ServerConnectedEvent event) {
        return EventTask.async(() -> ProtoGuiAPI.getInstance().updateActions(event.getPlayer().getUniqueId()));
    }

    @Subscribe
    public EventTask onDisconnect(final @NotNull DisconnectEvent event) {
        return EventTask.async(() -> ProtoGuiAPI.getInstance().closeGui(event.getPlayer().getUniqueId()));
    }
}
