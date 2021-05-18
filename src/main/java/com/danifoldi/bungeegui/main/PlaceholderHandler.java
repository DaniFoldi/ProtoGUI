package com.danifoldi.bungeegui.main;

import com.danifoldi.bungeegui.util.NumberUtil;
import com.danifoldi.bungeegui.util.VersionUtil;
import de.exceptionflug.protocolize.api.protocol.ProtocolAPI;
import de.myzelyam.api.vanish.BungeeVanishAPI;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;

@Singleton
public class PlaceholderHandler {
    private final Map<String, Function<ProxiedPlayer, String>> builtinPlaceholders = new HashMap<>();
    private final Map<String, Function<ProxiedPlayer, String>> placeholders = new HashMap<>();
    private final ConcurrentMap<ServerInfo, ServerPing> latestPing = new ConcurrentHashMap<>();
    private final ProxyServer proxyServer;
    private final PluginManager pluginManager;
    private final BungeeGuiPlugin plugin;
    private final Logger logger;

    private ScheduledTask refreshData;

    @Inject
    public PlaceholderHandler(final @NotNull ProxyServer proxyServer,
                              final @NotNull PluginManager pluginManager,
                              final @NotNull BungeeGuiPlugin plugin,
                              final @NotNull Logger logger) {
        this.proxyServer = proxyServer;
        this.pluginManager = pluginManager;
        this.plugin = plugin;
        this.logger = logger;
    }

    void register(String name, Function<ProxiedPlayer, String> placeholder) {
        placeholders.putIfAbsent(name, placeholder);
    }

    void unregister(String name) {
        placeholders.remove(name);
    }

    void registerBuiltin(String name, Function<ProxiedPlayer, String> placeholder) {
        builtinPlaceholders.putIfAbsent(name, placeholder);
    }

    void unregisterBuiltin(String name) {
        builtinPlaceholders.remove(name);
    }

    String parse(ProxiedPlayer player, String text) {
        String result = text;
        int iter = 0;
        boolean changed = true;

        while (changed && iter < 8) {
            changed = false;

            for (Map.Entry<String, Function<ProxiedPlayer, String>> placeholder : builtinPlaceholders.entrySet()) {
                try {
                    if (result.contains("%" + placeholder.getKey() + "%")) {
                        final String value = placeholder.getValue().apply(player);
                        if (value == null) {
                            continue;
                        }
                        result = result.replace("%" + placeholder.getKey() + "%", value);
                        changed = true;
                    }
                } catch (Exception e) {
                    logger.warning("Placeholder " + placeholder.getKey() + " couldn't be processed");
                    e.printStackTrace();
                }
            }

            for (Map.Entry<String, Function<ProxiedPlayer, String>> placeholder : placeholders.entrySet()) {
                try {
                    if (result.contains("%" + placeholder.getKey() + "%")) {
                        final String value = placeholder.getValue().apply(player);
                        if (value == null) {
                            continue;
                        }
                        result = result.replace("%" + placeholder.getKey() + "%", value);
                        changed = true;
                    }
                } catch (Exception e) {
                    logger.warning("Placeholder " + placeholder.getKey() + " couldn't be processed");
                    e.printStackTrace();
                }
            }
            iter++;
        }

        return result;
    }

    void unregisterAll() {
        builtinPlaceholders.clear();
        placeholders.clear();
        if (refreshData != null) {
            refreshData.cancel();
            refreshData = null;
        }
    }

    void registerBuiltins() {
        ConcurrentMap<ServerInfo, Boolean> lastStatus = new ConcurrentHashMap<>();
        refreshData = proxyServer.getScheduler().schedule(plugin, () -> {
            for (ServerInfo server: proxyServer.getServersCopy().values()) {
                server.ping((ping, error) -> {
                    if (lastStatus.containsKey(server) && lastStatus.get(server) != (boolean)(error != null)) {
                        if (error != null) {
                            logger.info("Server " + server.getName() + " no longer available");
                        } else {
                            logger.info("Server " + server.getName() + " now available");
                        }
                    }

                    lastStatus.put(server, error != null);
                    if (error == null) {
                        latestPing.put(server, ping);
                    }
                });
            }
        }, 1, 5, TimeUnit.SECONDS);

        //registerBuiltin("", player -> "%");

        registerBuiltin("ram_used", player -> NumberUtil.formatDecimal((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024d * 1024d)));
        registerBuiltin("ram_total", player -> NumberUtil.formatDecimal((Runtime.getRuntime().totalMemory()) / (1024d * 1024d)));

        registerBuiltin("proxyname", player -> proxyServer.getName());
        registerBuiltin("proxyversion", player -> proxyServer.getVersion());
        registerBuiltin("bungeegui", player -> plugin.getDescription().getName() + " " + plugin.getDescription().getVersion());
        registerBuiltin("version", player -> {
            if (player == null) {
                return "";
            }
            return VersionUtil.find(player.getPendingConnection().getVersion()).getVersion();
        });
        registerBuiltin("max", player -> String.valueOf(proxyServer.getConfig().getPlayerLimit()));
        registerBuiltin("online", player -> String.valueOf(proxyServer.getOnlineCount()));
        registerBuiltin("online_visible", player -> {
            int count = proxyServer.getOnlineCount();
            if (pluginManager.getPlugin("PremiumVanish") != null) {
                count -= BungeeVanishAPI.getInvisiblePlayers().size();
            }
            return String.valueOf(count);
        });
        registerBuiltin("guicount", player -> String.valueOf(BungeeGuiAPI.getInstance().getAvailableGuis().size()));
        registerBuiltin("servercount", player -> String.valueOf(proxyServer.getServersCopy().size()));
        registerBuiltin("plugincount", player -> String.valueOf(pluginManager.getPlugins().size()));
        registerBuiltin("placeholdercount", player -> String.valueOf(placeholders.size() + builtinPlaceholders.size()));
        registerBuiltin("displayname", player -> {
            if (player == null) {
                return "";
            }
            return player.getDisplayName();
        });
        registerBuiltin("name", player -> {
            if (player == null) {
                return "";
            }
            return player.getName();
        });
        registerBuiltin("locale", player -> {
            if (player == null) {
                return "No";
            }
            return player.getLocale().getDisplayName();
        });
        registerBuiltin("ping", player -> String.valueOf(player.getPing()));
        registerBuiltin("vanished", player -> {
            if (player == null) {
                return "No";
            }
            if (pluginManager.getPlugin("PremiumVanish") != null) {
                return BungeeVanishAPI.isInvisible(player) ? "Yes" : "No";
            } else {
                return "No";
            }
        });
        registerBuiltin("servername", player -> {
            if (player == null) {
                return "";
            }
            return player.getServer().getInfo().getName();
        });
        registerBuiltin("servermotd", player -> {
            if (player == null) {
                return "No";
            }
            return player.getServer().getInfo().getMotd();
        });

        registerBuiltin("traffic_downstream_input", player -> {
            if (player == null) {
                return "0";
            }
            return String.valueOf(ProtocolAPI.getTrafficManager().getTrafficData(player.getName()).getDownstreamInput());
        });

        registerBuiltin("traffic_downstream_output", player -> {
            if (player == null) {
                return "0";
            }
            return String.valueOf(ProtocolAPI.getTrafficManager().getTrafficData(player.getName()).getDownstreamOutput());
        });

        registerBuiltin("traffic_upstream_input", player -> {
            if (player == null) {
                return "0";
            }
            return String.valueOf(ProtocolAPI.getTrafficManager().getTrafficData(player.getName()).getUpstreamInput());
        });

        registerBuiltin("traffic_upstream_output", player -> {
            if (player == null) {
                return "0";
            }
            return String.valueOf(ProtocolAPI.getTrafficManager().getTrafficData(player.getName()).getUpstreamOutput());
        });

        registerBuiltin("traffic_downstream_input_last_minute", player -> {
            if (player == null) {
                return "0";
            }
            return String.valueOf(ProtocolAPI.getTrafficManager().getTrafficData(player.getName()).getDownstreamInputLastMinute());
        });

        registerBuiltin("traffic_downstream_output_last_minute", player -> {
            if (player == null) {
                return "0";
            }
            return String.valueOf(ProtocolAPI.getTrafficManager().getTrafficData(player.getName()).getDownstreamOutputLastMinute());
        });

        registerBuiltin("traffic_upstream_input_last_minute", player -> {
            if (player == null) {
                return "0";
            }
            return String.valueOf(ProtocolAPI.getTrafficManager().getTrafficData(player.getName()).getUpstreamInputLastMinute());
        });

        registerBuiltin("traffic_upstream_output_last_minute", player -> {
            if (player == null) {
                return "0";
            }
            return String.valueOf(ProtocolAPI.getTrafficManager().getTrafficData(player.getName()).getUpstreamOutputLastMinute());
        });

        registerBuiltin("luckperms_prefix", player -> {
            if (player == null) {
                return "";
            }
            try {
                String value = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getPrefix();
                return value == null ? "" : value;
            } catch (IllegalStateException | NullPointerException e) {
                return "";
            }
        });
        registerBuiltin("luckperms_suffix", player -> {
            if (player == null) {
                return "";
            }
            try {
                String value = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getSuffix();
                return value == null ? "" : value;
            } catch (IllegalStateException | NullPointerException e) {
                return "";
            }
        });
        registerBuiltin("luckperms_group", player -> {
            if (player == null) {
                return "";
            }
            try {
                String value = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId()).getCachedData().getMetaData().getPrimaryGroup();
                return value == null ? "" : value;
            } catch (IllegalStateException | NullPointerException e) {
                return "";
            }
        });

        for (Map.Entry<String, ServerInfo> server: proxyServer.getServersCopy().entrySet()) {
            registerBuiltin("online_visible@" + server.getKey(), player -> {
                ServerPing ping = latestPing.getOrDefault(server.getValue(), null);
                if (ping == null) {
                    return "0";
                }
                int count = ping.getPlayers().getOnline();
                if (pluginManager.getPlugin("PremiumVanish") != null) {
                    count -= BungeeVanishAPI.getInvisiblePlayers().stream().filter(u -> proxyServer.getPlayer(u).getServer().getInfo() == server.getValue()).count();
                }
                return String.valueOf(count);
            });
            registerBuiltin("online@" + server.getKey(), player -> {
                ServerPing ping = latestPing.getOrDefault(server.getValue(), null);
                if (ping == null) {
                    return "0";
                }
                return String.valueOf(ping.getPlayers().getOnline());
            });
            registerBuiltin("max@" + server.getKey(), player -> {
                ServerPing ping = latestPing.getOrDefault(server.getValue(), null);
                if (ping == null) {
                    return "0";
                }
                return String.valueOf(ping.getPlayers().getMax());
            });
            registerBuiltin("version@" + server.getKey(), player -> {
                ServerPing ping = latestPing.getOrDefault(server.getValue(), null);
                if (ping == null) {
                    return "-";
                }
                return String.valueOf(ping.getVersion().getName());
            });
            registerBuiltin("canaccess@" + server.getKey(), player -> {
                if (player == null) {
                    return "No";
                }
                return server.getValue().canAccess(player) ? "Yes" : "No";
            });
            registerBuiltin("restricted@" + server.getKey(), player -> server.getValue().isRestricted() ? "Yes" : "No");
            registerBuiltin("name@" + server.getKey(), player -> server.getValue().getName());
            registerBuiltin("motd@" + server.getKey(), player -> server.getValue().getMotd());
        }

        for (Plugin plugin: pluginManager.getPlugins()) {
            final String name = plugin.getDescription().getName();
            registerBuiltin("plugin_description@" + name, player -> plugin.getDescription().getDescription());
            registerBuiltin("plugin_main@" + name, player -> plugin.getDescription().getMain());
            registerBuiltin("plugin_version@" + name, player -> plugin.getDescription().getVersion());
            registerBuiltin("plugin_author@" + name, player -> plugin.getDescription().getAuthor());
            registerBuiltin("plugin_depends@" + name, player -> String.join(", ", plugin.getDescription().getDepends()));
            registerBuiltin("plugin_softdepends@" + name, player -> String.join(", ", plugin.getDescription().getSoftDepends()));
        }
    }
}
