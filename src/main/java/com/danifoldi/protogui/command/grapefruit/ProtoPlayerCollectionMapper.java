package com.danifoldi.protogui.command.grapefruit;

import com.danifoldi.protogui.main.ProtoGuiAPI;
import com.danifoldi.protogui.platform.PlatformInteraction;
import com.danifoldi.protogui.util.Pair;
import com.danifoldi.protogui.util.StringUtil;
import com.google.common.reflect.TypeToken;
import grapefruit.command.dispatcher.CommandContext;
import grapefruit.command.dispatcher.CommandInput;
import grapefruit.command.parameter.mapper.AbstractParameterMapper;
import grapefruit.command.parameter.mapper.ParameterMappingException;
import grapefruit.command.util.AnnotationList;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Stream;

@Singleton
public class ProtoPlayerCollectionMapper extends AbstractParameterMapper<PlatformInteraction.ProtoSender, Collection<PlatformInteraction.ProtoPlayer>> {

    public ProtoPlayerCollectionMapper() {
        //noinspection UnstableApiUsage
        super(new TypeToken<>() { });
    }

    @Override
    public @NotNull Collection<PlatformInteraction.ProtoPlayer> map(final @NotNull CommandContext<PlatformInteraction.ProtoSender> context,
                                                                    final @NotNull Queue<CommandInput> queue,
                                                                    final @NotNull AnnotationList modifiers) throws ParameterMappingException {
        final String input = queue.element().rawArg();

        if (input.equals("all")) {
            return ProtoGuiAPI.getInstance().getPlatform().getPlayers();
        }
        final @NotNull Pair<String, String> target = StringUtil.get(input);
        switch (target.getFirst().toLowerCase(Locale.ROOT)) {
            case "p":
                PlatformInteraction.ProtoPlayer player = ProtoGuiAPI.getInstance().getPlatform().getPlayer(target.getSecond());
                return player != null ? List.of(player) : Collections.emptyList();
            case "s":
                PlatformInteraction.ProtoServer server = Map.copyOf(ProtoGuiAPI.getInstance().getPlatform().getServers()).get(target.getSecond());
                return server != null ? List.copyOf(server.players()) : Collections.emptyList();
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public @NotNull List<String> listSuggestions(final @NotNull CommandContext<PlatformInteraction.ProtoSender> context,
                                                 final @NotNull String currentArg,
                                                 final @NotNull AnnotationList modifiers) {
        return Stream.concat(Stream.concat(
                Stream.of("all"),
                ProtoGuiAPI.getInstance().getPlatform().getPlayers()
                    .stream()
                    .filter(player -> !player.vanished())
                    .map(PlatformInteraction.ProtoPlayer::name)
                    .map(v -> "p:" + v)
                    .sorted()),
                Map.copyOf(ProtoGuiAPI.getInstance().getPlatform().getServers())
                    .keySet()
                    .stream()
                    .map(v -> "s:" + v)
                    .sorted()
                )
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(currentArg.toLowerCase(Locale.ROOT)))
                .toList();
    }
}
