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
public class ProtoServerMapper extends AbstractParameterMapper<PlatformInteraction.ProtoSender, PlatformInteraction.ProtoServer> {

    @Inject
    public ProtoServerMapper() {
        //noinspection UnstableApiUsage
        super(TypeToken.of(PlatformInteraction.ProtoServer.class));
    }

    @Override
    public @NotNull PlatformInteraction.ProtoServer map(final @NotNull CommandContext<PlatformInteraction.ProtoSender> context,
                                                        final @NotNull Queue<CommandInput> queue,
                                                        final @NotNull AnnotationList modifiers) throws ParameterMappingException {
        final String input = queue.element().rawArg();
        final @Nullable PlatformInteraction.ProtoServer server = ProtoGuiAPI.getInstance().getPlatform().getServers().get(input);

        if (server == null) {
            throw new ParameterMappingException(grapefruit.command.message.Message.of(
                    MessageKey.of(Message.SERVER_NOT_FOUND.name()),
                    Template.of("{name}", input)
            ));
        }

        return server;
    }

    @Override
    public @NotNull List<String> listSuggestions(final @NotNull CommandContext<PlatformInteraction.ProtoSender> context,
                                                 final @NotNull String currentArg,
                                                 final @NotNull AnnotationList modifiers) {
        return ProtoGuiAPI
                .getInstance()
                .getPlatform()
                .getServers()
                .keySet()
                .stream()
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(currentArg.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList());
    }
}
