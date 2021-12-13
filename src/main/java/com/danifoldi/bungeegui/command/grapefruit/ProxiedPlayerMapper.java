package com.danifoldi.bungeegui.command.grapefruit;

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
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Singleton
public class ProxiedPlayerMapper extends AbstractParameterMapper<CommandSender, ProxiedPlayer> {
    protected static final Pattern UUID_PATTERN =
            Pattern.compile("([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})");

    @Inject
    public ProxiedPlayerMapper() {
        super(TypeToken.of(ProxiedPlayer.class));
    }

    @Override
    public @NotNull ProxiedPlayer map(final @NotNull CommandContext<CommandSender> context,
                                      final @NotNull Queue<CommandInput> queue,
                                      final @NotNull AnnotationList modifiers) throws ParameterMappingException {
        final String input = queue.element().rawArg();
        final Matcher matcher = UUID_PATTERN.matcher(input);
        final @Nullable ProxiedPlayer player;
        if (matcher.matches()) {
            final UUID uuid = UUID.fromString(input);
            player = ProxyServer.getInstance().getPlayer(uuid);
        } else {
            player = ProxyServer.getInstance().getPlayer(input);
        }

        if (player == null) {
            throw new ParameterMappingException(Message.of(
                    ChatBridgeMessageKeys.NO_SUCH_PLAYER,
                    Template.of("{player}", input)
            ));
        }

        return player;
    }

    @Override
    public @NotNull List<String> listSuggestions(final @NotNull CommandContext<CommandSender> context,
                                                 final @NotNull String currentArg,
                                                 final @NotNull AnnotationList modifiers) {
        return ProxyServer.getInstance().getPlayers()
                .stream()
                .filter(player -> !VanishUtil.isVanished(player))
                .map(ProxiedPlayer::getName)
                .collect(Collectors.toList());
    }
}
