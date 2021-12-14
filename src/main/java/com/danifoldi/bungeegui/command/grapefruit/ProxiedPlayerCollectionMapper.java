package com.danifoldi.bungeegui.command.grapefruit;

import com.danifoldi.bungeegui.util.Pair;
import com.danifoldi.bungeegui.util.StringUtil;
import com.danifoldi.bungeegui.util.VanishUtil;
import com.google.common.reflect.TypeToken;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.CommandInput;
import grapefruit.command.message.Message;
import grapefruit.command.message.Template;
import grapefruit.command.parameter.mapper.AbstractParameterMapper;
import grapefruit.command.parameter.mapper.ParameterMappingException;
import grapefruit.command.util.AnnotationList;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class ProxiedPlayerCollectionMapper extends AbstractParameterMapper<CommandSender, Collection<ProxiedPlayer>> {
    protected static final Pattern UUID_PATTERN =
            Pattern.compile("([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})");

    private final @NotNull ProxyServer proxyServer;

    @Inject
    public ProxiedPlayerCollectionMapper(final @NotNull ProxyServer proxyServer) {
        super(new TypeToken<>() {
        });

        this.proxyServer = proxyServer;
    }

    @Override
    public @NotNull Collection<ProxiedPlayer> map(final @NotNull CommandContext<CommandSender> context,
                                      final @NotNull Queue<CommandInput> queue,
                                      final @NotNull AnnotationList modifiers) throws ParameterMappingException {
        final String input = queue.element().rawArg();

        if (input.equals("all")) {
            return proxyServer.getPlayers();
        }
        final @NotNull Pair<String, String> target = StringUtil.get(input);
        switch (target.getFirst().toLowerCase(Locale.ROOT)) {
            case "p":
                return List.of(proxyServer.getPlayer(target.getSecond()));
            case "s":
                return Map.copyOf(proxyServer.getServers()).get(target.getSecond()).getPlayers();
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public @NotNull List<String> listSuggestions(final @NotNull CommandContext<CommandSender> context,
                                                 final @NotNull String currentArg,
                                                 final @NotNull AnnotationList modifiers) {
        return Stream.concat(Stream.concat(
                Stream.of("all"),
                proxyServer.getPlayers()
                .stream()
                .filter(player -> !VanishUtil.isVanished(player))
                .map(ProxiedPlayer::getName)
                .map(v -> "p:" + v)),
                Map.copyOf(proxyServer.getServers())
                .keySet()
                .stream()
                .map(v -> "s:" + v)
                )
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(currentArg.toLowerCase(Locale.ROOT)))
                .toList();
    }
}
