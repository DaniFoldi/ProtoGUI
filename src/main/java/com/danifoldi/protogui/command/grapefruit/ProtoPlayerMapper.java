package com.danifoldi.protogui.command.grapefruit;

import com.danifoldi.protogui.main.ProtoGuiAPI;
import com.danifoldi.protogui.platform.PlatformInteraction;
import com.danifoldi.protogui.util.Message;
import com.google.common.reflect.TypeToken;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.CommandInput;
import grapefruit.command.message.MessageKey;
import grapefruit.command.message.Template;
import grapefruit.command.parameter.mapper.AbstractParameterMapper;
import grapefruit.command.parameter.mapper.ParameterMappingException;
import grapefruit.command.util.AnnotationList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Singleton
public class ProtoPlayerMapper extends AbstractParameterMapper<PlatformInteraction.ProtoSender, PlatformInteraction.ProtoPlayer> {
    protected static final Pattern UUID_PATTERN =
            Pattern.compile("([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})");

    @Inject
    public ProtoPlayerMapper() {
        //noinspection UnstableApiUsage
        super(TypeToken.of(PlatformInteraction.ProtoPlayer.class));
    }

    @Override
    public @NotNull PlatformInteraction.ProtoPlayer map(final @NotNull CommandContext<PlatformInteraction.ProtoSender> context,
                                                        final @NotNull Queue<CommandInput> queue,
                                                        final @NotNull AnnotationList modifiers) throws ParameterMappingException {
        final String input = queue.element().rawArg();
        final Matcher matcher = UUID_PATTERN.matcher(input);
        final @Nullable PlatformInteraction.ProtoPlayer player;
        if (matcher.matches()) {
            final UUID uuid = UUID.fromString(input);
            player = ProtoGuiAPI.getInstance().getPlatform().getPlayer(uuid);
        } else {
            player = ProtoGuiAPI.getInstance().getPlatform().getPlayer(input);
        }

        if (player == null) {
            throw new ParameterMappingException(grapefruit.command.message.Message.of(
                    MessageKey.of(Message.PLAYER_NOT_FOUND.name()),
                    Template.of("{player}", input)
            ));
        }

        return player;
    }

    @Override
    public @NotNull List<String> listSuggestions(final @NotNull CommandContext<PlatformInteraction.ProtoSender> context,
                                                 final @NotNull String currentArg,
                                                 final @NotNull AnnotationList modifiers) {
        return ProtoGuiAPI
                .getInstance()
                .getPlatform()
                .getPlayers()
                .stream()
                .filter(player -> !player.vanished())
                .map(PlatformInteraction.ProtoPlayer::name)
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(currentArg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }
}
