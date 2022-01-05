package com.danifoldi.protogui.main;

import com.danifoldi.protogui.platform.PlatformInteraction;
import com.danifoldi.protogui.util.NumberUtil;
import com.danifoldi.protogui.util.Pair;
import com.danifoldi.protogui.util.VersionUtil;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Stream;

@Singleton
public class PlaceholderHandler {
    private final @NotNull Map<String, Function<UUID, String>> builtinPlaceholders = new HashMap<>();
    private final @NotNull Map<String, Function<UUID, String>> placeholders = new HashMap<>();
    private final @NotNull Logger logger;


    @Inject
    public PlaceholderHandler(final @NotNull Logger logger) {
        this.logger = logger;
    }

    void register(final @NotNull String name, final @NotNull Function<UUID, String> placeholder) {
        placeholders.putIfAbsent(name, placeholder);
    }

    void unregister(final @NotNull String name) {
        placeholders.remove(name);
    }

    void registerBuiltin(final @NotNull String name, final @NotNull Function<UUID, String> placeholder) {
        builtinPlaceholders.putIfAbsent(name, placeholder);
    }

    @NotNull String parse(final @Nullable UUID uuid, @NotNull String text) {
        int iter = 0;
        boolean changed = true;

        while (changed && iter < 8) {
            changed = false;

            for (Pair<String, Function<UUID, String>> placeholderPair : Stream.concat(builtinPlaceholders.entrySet().stream(), placeholders.entrySet().stream()).map(e -> Pair.of(e.getKey(), e.getValue())).toList()) {
                String placeholder = placeholderPair.getFirst();
                Function<UUID, String> function = placeholderPair.getSecond();
                try {
                    if (text.contains("%" + placeholder + "%")) {
                        final String value = function.apply(uuid);
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
    }

    void registerBuiltins() {
        PlatformInteraction platform = ProtoGuiAPI.getInstance().getPlatform();
        //registerBuiltin("", player -> "%");

        registerBuiltin("ram_used", uuid -> NumberUtil.formatDecimal((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024d * 1024d)));
        registerBuiltin("ram_total", uuid -> NumberUtil.formatDecimal((Runtime.getRuntime().totalMemory()) / (1024d * 1024d)));

        registerBuiltin("proxyname", uuid -> platform.platformName());
        registerBuiltin("proxyversion", uuid -> platform.platformVersion());
        registerBuiltin("bungeegui", uuid -> platform.pluginName() + " " + platform.pluginVersion());
        registerBuiltin("version", uuid -> {
            if (uuid == null) {
                return "";
            }
            return VersionUtil.find(platform.getPlayer(uuid).protocol()).getVersion();
        });
        registerBuiltin("max", uuid -> String.valueOf(platform.maxPlayerCount()));
        registerBuiltin("online", uuid -> String.valueOf(platform.getPlayers().size()));
        registerBuiltin("online_visible", uuid -> String.valueOf(platform.getPlayers().stream().filter(p -> !p.vanished()).count()));
        registerBuiltin("guicount", uuid -> String.valueOf(ProtoGuiAPI.getInstance().getLoadedGuis().size()));
        registerBuiltin("servercount", uuid -> String.valueOf(platform.getServers().size()));
        registerBuiltin("plugincount", uuid -> String.valueOf(platform.getPlugins().size()));
        registerBuiltin("placeholdercount", uuid -> String.valueOf(placeholders.size() + builtinPlaceholders.size()));
        registerBuiltin("displayname", uuid -> {
            if (uuid == null) {
                return "";
            }
            return platform.getPlayer(uuid).displayName();
        });
        registerBuiltin("uuid", uuid -> {
            if (uuid == null) {
                return "";
            }

            return uuid.toString();
        });
        registerBuiltin("name", uuid -> {
            if (uuid == null) {
                return "";
            }
            return platform.getPlayer(uuid).name();
        });
        registerBuiltin("locale", uuid -> {
            if (uuid == null) {
                return "No";
            }
            return platform.getPlayer(uuid).locale();
        });
        registerBuiltin("ping", uuid -> String.valueOf(platform.getPlayer(uuid).ping()));
        registerBuiltin("vanished", uuid -> {
            if (uuid == null) {
                return "No";
            }
            return platform.getPlayer(uuid).vanished() ? "Yes" : "No";
        });
        registerBuiltin("servername", uuid -> {
            if (uuid == null) {
                return "";
            }
            return platform.getPlayer(uuid).connectedTo().name();
        });
        registerBuiltin("servermotd", uuid -> {
            if (uuid == null) {
                return "No";
            }
            return platform.getPlayer(uuid).connectedTo().motd();
        });

        registerBuiltin("luckperms_friendlyname", uuid -> {
            if (uuid == null) {
                return "";
            }
            try {
                final @Nullable User user = LuckPermsProvider.get().getUserManager().getUser(uuid);
                if (user == null) {
                    return "";
                }
                return user.getFriendlyName();
            } catch (IllegalStateException | NullPointerException e) {
                return "";
            }
        });
        registerBuiltin("luckperms_prefix", uuid -> {
            if (uuid == null) {
                return "";
            }
            try {
                final @Nullable User user = LuckPermsProvider.get().getUserManager().getUser(uuid);
                if (user == null) {
                    return "";
                }
                final @Nullable String value = user.getCachedData().getMetaData().getPrefix();
                return value == null ? "" : value;
            } catch (IllegalStateException | NullPointerException e) {
                return "";
            }
        });
        registerBuiltin("luckperms_suffix", uuid -> {
            if (uuid == null) {
                return "";
            }
            try {
                final @Nullable User user = LuckPermsProvider.get().getUserManager().getUser(uuid);
                if (user == null) {
                    return "";
                }
                final @Nullable String value = user.getCachedData().getMetaData().getSuffix();
                return value == null ? "" : value;
            } catch (IllegalStateException | NullPointerException e) {
                return "";
            }
        });
        registerBuiltin("luckperms_group", uuid -> {
            if (uuid == null) {
                return "";
            }
            try {
                final @Nullable User user = LuckPermsProvider.get().getUserManager().getUser(uuid);
                if (user == null) {
                    return "";
                }
                final @Nullable String value = user.getCachedData().getMetaData().getPrimaryGroup();
                return value == null ? "" : value;
            } catch (IllegalStateException | NullPointerException e) {
                return "";
            }
        });

        for (Map.Entry<String, PlatformInteraction.ProtoServer> server: Map.copyOf(platform.getServers()).entrySet()) {
            registerBuiltin("status@" + server.getKey(), uuid -> server.getValue().online() ? "Online" : "Offline");
            registerBuiltin("is_online@" + server.getKey(), uuid -> server.getValue().online() ? "Yes" : "No");
            registerBuiltin("online_visible@" + server.getKey(), uuid -> String.valueOf(server.getValue().playerCount() - server.getValue().players().stream().filter(PlatformInteraction.ProtoPlayer::vanished).count()));
            registerBuiltin("online@" + server.getKey(), uuid -> String.valueOf(server.getValue().playerCount()));
            registerBuiltin("max@" + server.getKey(), uuid -> String.valueOf(server.getValue().playerMax()));
            registerBuiltin("version@" + server.getKey(), uuid -> server.getValue().version());
            registerBuiltin("name@" + server.getKey(), uuid -> server.getValue().name());
            registerBuiltin("motd@" + server.getKey(), uuid -> server.getValue().motd());
        }

        for (PlatformInteraction.ProtoPlugin plugin: platform.getPlugins()) {
            final String name = plugin.name();
            registerBuiltin("plugin_description@" + name, uuid -> plugin.description());
            registerBuiltin("plugin_main@" + name, uuid -> plugin.main());
            registerBuiltin("plugin_version@" + name, uuid -> plugin.version());
            registerBuiltin("plugin_author@" + name, uuid -> String.join(", ", plugin.authors()));
            registerBuiltin("plugin_depends@" + name, uuid -> String.join(", ", plugin.dependencies()));
            registerBuiltin("plugin_softdepends@" + name, uuid -> String.join(", ", plugin.softDependencies()));
        }
    }
}
