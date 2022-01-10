package com.danifoldi.protogui.platform.bungee;

import com.danifoldi.protogui.inject.DaggerProtoGuiComponent;
import com.danifoldi.protogui.inject.ProtoGuiComponent;
import com.danifoldi.protogui.main.ProtoGuiAPI;
import com.danifoldi.protogui.main.ProtoGuiLoader;
import com.danifoldi.protogui.platform.PlatformInteraction;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import dev.simplix.protocolize.api.Protocolize;
import dev.simplix.protocolize.api.inventory.PlayerInventory;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ProtoGui extends Plugin implements Listener {
    private @Nullable ProtoGuiLoader loader;
    private @Nullable ScheduledTask pingTask;

    private final @NotNull Map<String, CommandWrapper> registeredCommands = new ConcurrentHashMap<>();
    private final @NotNull Map<ServerInfo, ServerPing> lastResponse = new ConcurrentHashMap<>();

    // Everything in onLoad is only needed for compatibility with older versions, so only on Bungee
    private boolean enabled = false;
    @Override
    public void onLoad() {
        try {
            if (!Files.exists(getDataFolder().toPath().resolve("compat"))) {
                return;
            }

            Files.move(getDataFolder().toPath().getParent().resolve("BungeeGUI"), getDataFolder().toPath());
            getLogger().info("Moved datafolder to new location");
        } catch (IOException ignored) {

        }
        try {
            PluginManager pluginManager = ProxyServer.getInstance().getPluginManager();
            Field pluginsField = pluginManager.getClass().getDeclaredField("plugins");
            pluginsField.setAccessible(true);
            if (pluginsField.get(pluginManager) instanceof Map pluginMap) {
                //noinspection unchecked
                pluginMap.put("BungeeGUI", this);
            }
            getLogger().info("Registering plugin as BungeeGUI, double loading message is normal");
        } catch (ReflectiveOperationException e) {
            getLogger().warning("Could not force-add old plugin name");
            getLogger().info(e.getMessage());
        }
    }

    @Override
    public void onEnable() {
        if (!enabled) {
            enabled = true;
        } else {
            return;
        }
        final @NotNull ProtoGuiComponent component = DaggerProtoGuiComponent.builder()
                .plugin(this)
                .logger(getLogger())
                .datafolder(getDataFolder().toPath())
                .threadPool(Executors.newCachedThreadPool(new ThreadFactoryBuilder()
                        .setNameFormat(getClass().getSimpleName() + " Async Pool - #%1$d")
                        .setDaemon(false)
                        .build()))
                .platformInteraction(platform)
                .build();

        this.loader = component.loader();
        this.loader.load();
    }

    @Override
    public void onDisable() {
        if (enabled) {
            enabled = false;
        } else {
            return;
        }
        if (this.loader == null) {
            return;
        }
        this.loader.unload();
    }

    final @NotNull Function<CommandSender, PlatformInteraction.ProtoSender> senderGenerator = commandSender -> new PlatformInteraction.ProtoSender() {
        @Override
        public boolean hasPermission(String permission) {
            return commandSender.hasPermission(permission);
        }

        @Override
        public void send(String message) {
            commandSender.sendMessage(new TextComponent(message));
        }

        @Override
        public String displayName() {
            if (commandSender instanceof ProxiedPlayer player) {
                return player.getDisplayName();
            }
            return commandSender.getName();
        }

        @Override
        public String name() {
            return commandSender.getName();
        }

        @Override
        public UUID uniqueId() {
            if (commandSender instanceof ProxiedPlayer player) {
                return player.getUniqueId();
            }
            return UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");
        }
    };

    final @NotNull Function<ServerInfo, PlatformInteraction.ProtoServer> serverGenerator = serverInfo -> new PlatformInteraction.ProtoServer() {
        @Override
        public String name() {
            return serverInfo.getName();
        }

        @Override
        public String motd() {
            return serverInfo.getMotd();
        }

        @Override
        public Collection<PlatformInteraction.ProtoPlayer> players() {
            return serverInfo.getPlayers().stream().map(playerGenerator).toList();
        }

        @Override
        public boolean online() {
            return lastResponse.containsKey(serverInfo);
        }

        @Override
        public String version() {
            return Optional.ofNullable(lastResponse.get(serverInfo)).map(p -> p.getVersion().getName()).orElse("");
        }

        @Override
        public int playerMax() {
            return Optional.ofNullable(lastResponse.get(serverInfo)).map(p -> p.getPlayers().getMax()).orElse(0);
        }

        @Override
        public int playerCount() {
            return Optional.ofNullable(lastResponse.get(serverInfo)).map(p -> p.getPlayers().getOnline()).orElse(0);
        }
    };

    final @NotNull Function<ProxiedPlayer, PlatformInteraction.ProtoPlayer> playerGenerator = proxiedPlayer -> new PlatformInteraction.ProtoPlayer() {
        @Override
        public boolean vanished() {
            final @Nullable Plugin premiumvanishPlugin = ProxyServer.getInstance().getPluginManager().getPlugin("PremiumVanish");
            if (premiumvanishPlugin == null) {
                return false;
            }

            return de.myzelyam.api.vanish.BungeeVanishAPI.isInvisible(proxiedPlayer);
        }

        @Override
        public int protocol() {
            return proxiedPlayer.getPendingConnection().getVersion();
        }

        @Override
        public int ping() {
            return proxiedPlayer.getPing();
        }

        @Override
        public String locale() {
            return proxiedPlayer.getLocale().getDisplayName();
        }

        @Override
        public void run(String command) {
            ProxyServer.getInstance().getPluginManager().dispatchCommand(proxiedPlayer, command);
        }

        @Override
        public void actionbar(String message) {
            proxiedPlayer.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }

        @Override
        public void chat(String message) {
            proxiedPlayer.chat(message);
        }

        @Override
        public void connect(PlatformInteraction.ProtoServer server) {
            proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo(server.name()));
        }

        @Override
        public PlatformInteraction.ProtoServer connectedTo() {
            return serverGenerator.apply(proxiedPlayer.getServer().getInfo());
        }

        @Override
        public void title(String message, int fadeIn, int stay, int fadeOut) {
            proxiedPlayer.sendTitle(ProxyServer.getInstance().createTitle().title(new TextComponent(message)).fadeIn(fadeIn).stay(stay).fadeOut(fadeOut));
        }

        @Override
        public void subtitle(String message, int fadeIn, int stay, int fadeOut) {
            proxiedPlayer.sendTitle(ProxyServer.getInstance().createTitle().subTitle(new TextComponent(message)).fadeIn(fadeIn).stay(stay).fadeOut(fadeOut));
        }

        @Override
        public boolean hasPermission(String permission) {
            return proxiedPlayer.hasPermission(permission);
        }

        @Override
        public void send(String message) {
            proxiedPlayer.sendMessage(new TextComponent(message));
        }

        @Override
        public String displayName() {
            return proxiedPlayer.getDisplayName();
        }

        @Override
        public String name() {
            return proxiedPlayer.getName();
        }

        @Override
        public UUID uniqueId() {
            return proxiedPlayer.getUniqueId();
        }
    };

    final @NotNull Function<Plugin, PlatformInteraction.ProtoPlugin> pluginGenerator = plugin -> new PlatformInteraction.ProtoPlugin() {
        @Override
        public String name() {
            return plugin.getDescription().getName();
        }

        @Override
        public String description() {
            return plugin.getDescription().getDescription();
        }

        @Override
        public String main() {
            return plugin.getDescription().getMain();
        }

        @Override
        public String version() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public List<String> authors() {
            return Arrays.stream(plugin.getDescription().getAuthor().split(",")).map(String::trim).toList();
        }

        @Override
        public List<String> dependencies() {
            return plugin.getDescription().getDepends().stream().toList();
        }

        @Override
        public List<String> softDependencies() {
            return plugin.getDescription().getSoftDepends().stream().toList();
        }
    };

    final @NotNull PlatformInteraction platform = new PlatformInteraction() {
        @Override
        public ProtoPlayer getPlayer(UUID uuid) {
            return playerGenerator.apply(ProxyServer.getInstance().getPlayer(uuid));
        }

        @Override
        public ProtoPlayer getPlayer(String name) {
            return playerGenerator.apply(ProxyServer.getInstance().getPlayer(name));
        }

        @Override
        public List<ProtoPlayer> getPlayers() {
            return ProxyServer.getInstance().getPlayers().stream().map(playerGenerator).toList();
        }

        @Override
        public String platformName() {
            return ProxyServer.getInstance().getName();
        }

        @Override
        public String platformVersion() {
            return ProxyServer.getInstance().getVersion();
        }

        @Override
        public String pluginName() {
            return getDescription().getName();
        }

        @Override
        public String pluginVersion() {
            return getDescription().getVersion();
        }

        @Override
        public int maxPlayerCount() {
            return ProxyServer.getInstance().getConfig().getPlayerLimit();
        }

        @Override
        public void setup() {
            if (pingTask != null) {
                pingTask.cancel();
                pingTask = null;
            }
            lastResponse.clear();
            registeredCommands.clear();

            pingTask = ProxyServer.getInstance().getScheduler().schedule(ProtoGui.this, () -> {
                Map.copyOf(ProxyServer.getInstance().getServers()).forEach((name, info) -> info.ping((response, error) -> {
                    if (error != null) {
                        lastResponse.remove(info);
                    } else {
                        lastResponse.put(info, response);
                    }
                }));
            }, 5, 5, TimeUnit.SECONDS);
            ProxyServer.getInstance().getPluginManager().registerListener(ProtoGui.this, ProtoGui.this);

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
            registeredCommands.clear();

            ProxyServer.getInstance().getPluginManager().unregisterListeners(ProtoGui.this);
            ProxyServer.getInstance().getPluginManager().unregisterCommands(ProtoGui.this);
        }

        @Override
        public void registerCommand(String command, BiConsumer<ProtoSender, String> dispatch, BiFunction<ProtoSender, String, Collection<String>> suggest) {
            CommandWrapper commandWrapper = new CommandWrapper(command, dispatch, suggest, ProtoGui.this);
            registeredCommands.put(command, commandWrapper);
            ProxyServer.getInstance().getPluginManager().registerCommand(ProtoGui.this, commandWrapper);
        }

        @Override
        public void unregisterCommand(String command) {
            ProxyServer.getInstance().getPluginManager().unregisterCommand(registeredCommands.remove(command));
        }

        @Override
        public void runConsoleCommand(String command) {
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), command);
        }

        @Override
        public List<ProtoPlugin> getPlugins() {
            return ProxyServer.getInstance().getPluginManager().getPlugins().stream().map(pluginGenerator).toList();
        }

        @Override
        public Map<String, ProtoServer> getServers() {
            return Map
                    .copyOf(ProxyServer.getInstance().getServers())
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> serverGenerator.apply(e.getValue())));
        }
    };

    @EventHandler
    @SuppressWarnings("unused")
    public void onServerSwitch(final @NotNull ServerSwitchEvent event) {
        ProtoGuiAPI.getInstance().updateActions(event.getPlayer().getUniqueId());
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onDisconnect(final @NotNull PlayerDisconnectEvent event) {
        ProtoGuiAPI.getInstance().closeGui(event.getPlayer().getUniqueId());
    }
}
