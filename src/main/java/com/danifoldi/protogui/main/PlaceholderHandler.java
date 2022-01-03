package com.danifoldi.protogui.main;

import com.danifoldi.protogui.util.NumberUtil;
import com.danifoldi.protogui.util.Pair;
import com.danifoldi.protogui.util.VersionUtil;
import de.myzelyam.api.vanish.BungeeVanishAPI;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class PlaceholderHandler {
    private final @NotNull Map<String, Function<ProxiedPlayer, String>> builtinPlaceholders = new HashMap<>();
    private final @NotNull Map<String, Function<ProxiedPlayer, String>> placeholders = new HashMap<>();
    private final @NotNull ConcurrentMap<ServerInfo, ServerPing> latestPing = new ConcurrentHashMap<>();
    private final @NotNull ProxyServer proxyServer;
    private final @NotNull PluginManager pluginManager;
    private final @NotNull BungeeGuiPlugin plugin;
    private final @NotNull Logger logger;

    private @Nullable ScheduledTask refreshData;

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

    void register(final @NotNull String name, final @NotNull Function<ProxiedPlayer, String> placeholder) {
        placeholders.putIfAbsent(name, placeholder);
    }

    void unregister(final @NotNull String name) {
        placeholders.remove(name);
    }

    void registerBuiltin(final @NotNull String name, final @NotNull Function<ProxiedPlayer, String> placeholder) {
        builtinPlaceholders.putIfAbsent(name, placeholder);
    }

    @NotNull String parse(final @Nullable ProxiedPlayer player, @NotNull String text) {
        int iter = 0;
        boolean changed = true;

        while (changed && iter < 8) {
            changed = false;

            for (Pair<String, Function<ProxiedPlayer, String>> placeholderPair : Stream.concat(builtinPlaceholders.entrySet().stream(), placeholders.entrySet().stream()).map(e -> Pair.of(e.getKey(), e.getValue())).collect(Collectors.toList())) {
                String placeholder = placeholderPair.getFirst();
                Function<ProxiedPlayer, String> function = placeholderPair.getSecond();
                try {
                    if (text.contains("%" + placeholder + "%")) {
                        final String value = function.apply(player);
                        if (value == null) {
                            continue;
                        }
                        text = text.replace("%" + placeholder + "%", value);
                        changed = true;
                    }
                } catch (Exception e) {
                    logger.warning("Placeholder " + placeholder + " couldn't be processed");
                    e.printStackTrace();
                }
            }

            iter++;
        }

        return text;
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
        final @NotNull ConcurrentMap<ServerInfo, Boolean> lastStatus = new ConcurrentHashMap<>();
        refreshData = proxyServer.getScheduler().schedule(plugin, () -> {
            for (ServerInfo server: Map.copyOf(proxyServer.getServers()).values()) {
                server.ping((ping, error) -> {
                    if (lastStatus.containsKey(server) && lastStatus.get(server) != (error == null)) {
                        if (error != null) {
                            logger.info("Server " + server.getName() + " no longer available");
                        } else {
                            logger.info("Server " + server.getName() + " now available");
                        }
                    }

                    lastStatus.put(server, error == null);
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
        registerBuiltin("servercount", player -> String.valueOf(Map.copyOf(proxyServer.getServers()).size()));
        registerBuiltin("plugincount", player -> String.valueOf(pluginManager.getPlugins().size()));
        registerBuiltin("placeholdercount", player -> String.valueOf(placeholders.size() + builtinPlaceholders.size()));
        registerBuiltin("displayname", player -> {
            if (player == null) {
                return "";
            }
            return player.getDisplayName();
        });
        registerBuiltin("uuid", player -> {
            if (player == null) {
                return "";
            }

            return player.getUniqueId().toString();
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
                //noinspection ConstantConditions
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

        registerBuiltin("luckperms_friendlyname", player -> {
            if (player == null) {
                return "";
            }
            try {
                final @Nullable User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
                if (user == null) {
                    return "";
                }
                return user.getFriendlyName();
            } catch (IllegalStateException | NullPointerException e) {
                return "";
            }
        });
        registerBuiltin("luckperms_prefix", player -> {
            if (player == null) {
                return "";
            }
            try {
                final @Nullable User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
                if (user == null) {
                    return "";
                }
                final @Nullable String value = user.getCachedData().getMetaData().getPrefix();
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
                final @Nullable User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
                if (user == null) {
                    return "";
                }
                final @Nullable String value = user.getCachedData().getMetaData().getSuffix();
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
                final @Nullable User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());
                if (user == null) {
                    return "";
                }
                final @Nullable String value = user.getCachedData().getMetaData().getPrimaryGroup();
                return value == null ? "" : value;
            } catch (IllegalStateException | NullPointerException e) {
                return "";
            }
        });

        for (Map.Entry<String, ServerInfo> server: Map.copyOf(proxyServer.getServers()).entrySet()) {
            registerBuiltin("status@" + server.getKey(), player -> {
                final boolean online = lastStatus.get(server.getValue());
                return online ? "Online" : "Offline";
            });
            registerBuiltin("is_online@" + server.getKey(), player -> {
                final boolean online = lastStatus.get(server.getValue());
                return online ? "Yes" : "No";
            });
            registerBuiltin("online_visible@" + server.getKey(), player -> {
                final @Nullable ServerPing ping = latestPing.getOrDefault(server.getValue(), null);
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
                final @Nullable ServerPing ping = latestPing.getOrDefault(server.getValue(), null);
                if (ping == null) {
                    return "0";
                }
                return String.valueOf(ping.getPlayers().getOnline());
            });
            registerBuiltin("max@" + server.getKey(), player -> {
                final @Nullable ServerPing ping = latestPing.getOrDefault(server.getValue(), null);
                if (ping == null) {
                    return "0";
                }
                return String.valueOf(ping.getPlayers().getMax());
            });
            registerBuiltin("version@" + server.getKey(), player -> {
                final @Nullable ServerPing ping = latestPing.getOrDefault(server.getValue(), null);
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
