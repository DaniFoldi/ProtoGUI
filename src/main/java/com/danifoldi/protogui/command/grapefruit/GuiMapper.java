package com.danifoldi.protogui.command.grapefruit;

import com.danifoldi.protogui.gui.GuiGrid;
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

import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.stream.Collectors;

public class GuiMapper extends AbstractParameterMapper<PlatformInteraction.ProtoSender, GuiGrid> {
    public GuiMapper() {
        //noinspection UnstableApiUsage
        super(TypeToken.of(GuiGrid.class));
    }

    @Override
    public @NotNull GuiGrid map(@NotNull CommandContext<PlatformInteraction.ProtoSender> context, @NotNull Queue<CommandInput> args, @NotNull AnnotationList modifiers) throws ParameterMappingException {
        String guiName = args.element().rawArg();
        GuiGrid gui = ProtoGuiAPI.getInstance().getGui(guiName);
        if (gui == null) {
            throw new ParameterMappingException(grapefruit.command.message.Message.of(
                    MessageKey.of(Message.GUI_NOT_FOUND.name()),
                    Template.of("{name}", guiName)
            ));
        }
        return gui;
    }

    @Override
    public @NotNull List<String> listSuggestions(@NotNull CommandContext<PlatformInteraction.ProtoSender> context, @NotNull String currentArg, @NotNull AnnotationList modifiers) {
        return ProtoGuiAPI.getInstance().getLoadedGuis()
                .stream()
                .filter(g -> g.toLowerCase(Locale.ROOT).startsWith(currentArg.toLowerCase(Locale.ROOT)))
                .sorted()
                .toList();
    }
}
