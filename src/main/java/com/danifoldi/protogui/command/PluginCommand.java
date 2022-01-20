package com.danifoldi.protogui.command;

import com.danifoldi.protogui.gui.GuiGrid;
import com.danifoldi.protogui.main.ProtoGuiAPI;
import com.danifoldi.protogui.platform.PlatformInteraction;
import com.danifoldi.protogui.util.Message;
import com.danifoldi.protogui.util.Pair;
import com.danifoldi.protogui.util.SoundUtil;
import dev.simplix.protocolize.api.SoundCategory;
import grapefruit.command.CommandContainer;
import grapefruit.command.CommandDefinition;
import grapefruit.command.dispatcher.Redirect;
import grapefruit.command.parameter.modifier.OptParam;
import grapefruit.command.parameter.modifier.Range;
import grapefruit.command.parameter.modifier.Source;
import grapefruit.command.parameter.modifier.string.Greedy;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Collection;
import java.util.logging.Logger;

@SuppressWarnings("unused, ClassCanBeRecord")
public class PluginCommand implements CommandContainer {

    private final @NotNull Logger logger;

    @Inject
    public PluginCommand(final @NotNull Logger logger) {
        this.logger = logger;
    }

    @Redirect(from = "bgui|bungeegui|pgui|protogui")
    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui help", permission = "protogui.command.help", runAsync = true)
    public void onHelpCommand(@Source PlatformInteraction.ProtoSender sender) {

        sender.send(Message.COMMAND_HELP);
        sender.send(Message.COMMAND_ACTIONBAR);
        sender.send(Message.COMMAND_BROADCAST);
        sender.send(Message.COMMAND_CHAT);
        sender.send(Message.COMMAND_CLOSE);
        sender.send(Message.COMMAND_GUIS);
        sender.send(Message.COMMAND_LOG);
        sender.send(Message.COMMAND_OPEN);
        sender.send(Message.COMMAND_RELOAD);
        sender.send(Message.COMMAND_SEND);
        sender.send(Message.COMMAND_SOUND);
        sender.send(Message.COMMAND_TITLE);
    }

    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui actionbar", permission = "protogui.command.actionbar", runAsync = true)
    public void onActionbarCommand(@Source PlatformInteraction.ProtoSender sender, Collection<PlatformInteraction.ProtoPlayer> targets, @Greedy String content) {

        targets.forEach(p -> p.actionbar(Message.process(p, content)));
        sender.send(Message.ACTION_COMPLETE.process(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui broadcast", permission = "protogui.command.broadcast", runAsync = true)
    public void onBroadcastCommand(@Source PlatformInteraction.ProtoSender sender, Collection<PlatformInteraction.ProtoPlayer> targets, @Greedy String content) {

        targets.forEach(p -> p.send(Message.process(p, content)));
        sender.send(Message.ACTION_COMPLETE.process(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui chat", permission = "protogui.command.chat", runAsync = true)
    public void onChatCommand(@Source PlatformInteraction.ProtoSender sender, PlatformInteraction.ProtoPlayer target, @Greedy String content) {

        target.chat(Message.colorCodes(ProtoGuiAPI.getInstance().parsePlaceholders(target.uniqueId(), content)));
    }

    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui close", permission = "protogui.command.close", runAsync = true)
    public void onCloseCommand(@Source PlatformInteraction.ProtoSender sender, Collection<PlatformInteraction.ProtoPlayer> targets) {

        targets.stream().map(PlatformInteraction.ProtoPlayer::uniqueId).forEach(ProtoGuiAPI.getInstance()::closeGui);
        sender.send(Message.ACTION_COMPLETE.process(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui list|guis", permission = "protogui.command.list", runAsync = true)
    public void onListCommand(@Source PlatformInteraction.ProtoSender sender) {

        sender.send(Message.GUI_LIST_TOP.process(null, Pair.of("count", String.valueOf(ProtoGuiAPI.getInstance().getLoadedGuis().size()))));
        for (final @NotNull String name: ProtoGuiAPI.getInstance().getLoadedGuis().stream().sorted().toList()) {
            sender.send(Message.GUI_LIST_ITEM.process(null, Pair.of("name", name)));
        }
    }

    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui log", permission = "protogui.command.log", runAsync = true)
    public void onLogCommand(@Source PlatformInteraction.ProtoSender sender, @Greedy String message) {

        logger.info("Command Log: %s".formatted(message));
    }

    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui open", permission = "protogui.command.open", runAsync = true)
    public void onOpenCommand(@Source PlatformInteraction.ProtoSender sender, Collection<PlatformInteraction.ProtoPlayer> targets, GuiGrid gui, @OptParam String target) {

        if (target == null && gui.isTargeted()) {
            sender.send(Message.GUI_TARGET_REQUIRED.process(null));
            return;
        }

        targets.forEach(p -> ProtoGuiAPI.getInstance().openGui(p.uniqueId(), gui, target == null ? "" : target));
        sender.send(Message.ACTION_COMPLETE.process(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui reload", permission = "protogui.command.reload", runAsync = true)
    public void onReloadCommand(@Source PlatformInteraction.ProtoSender sender) {

        final long length = ProtoGuiAPI.getInstance().reloadGuis();
        sender.send(Message.RELOAD_SUCCESS.process(null, Pair.of("time", String.valueOf(length))));
    }

    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui send", permission = "protogui.command.send", runAsync = true)
    public void onSendCommand(@Source PlatformInteraction.ProtoSender sender, Collection<PlatformInteraction.ProtoPlayer> targets, PlatformInteraction.ProtoServer server) {

        targets.forEach(p -> p.connect(server));
        sender.send(Message.ACTION_COMPLETE.process(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui sound", permission = "protogui.command.sound", runAsync = true)
    public void onSoundCommand(@Source PlatformInteraction.ProtoSender sender, Collection<PlatformInteraction.ProtoPlayer> targets, String soundName, @OptParam String soundCategory, @OptParam @Range(min = 0.0, max = 1.0) Double volume, @OptParam @Range(min = 0.0, max = 2.0) Double pitch) {

        if (!SoundUtil.isValidSound(soundName)) {
            sender.send(Message.INVALID_PROPERTY.process(null));
            return;
        }

        @NotNull SoundCategory category = SoundCategory.MASTER;
        if (soundCategory != null) {
            try {
                category = SoundCategory.valueOf(soundCategory);
            } catch (IllegalArgumentException ignored) {

            }
        }
        @NotNull SoundCategory finalCategory = category;

        targets.forEach(p -> SoundUtil.playSound(p.uniqueId(), soundName, finalCategory, volume == null ? 1.0f : volume.floatValue(), pitch == null ? 1.0f : pitch.floatValue()));
        sender.send(Message.ACTION_COMPLETE.process(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui subtitle", permission = "protogui.command.subtitle", runAsync = true)
    public void onSubtitleCommand(@Source PlatformInteraction.ProtoSender sender, Collection<PlatformInteraction.ProtoPlayer> targets, int fadeIn, int stay, int fadeOut, String message) {

        targets.forEach(p -> p.subtitle(Message.process(p, message), fadeIn, stay, fadeOut));
        sender.send(Message.ACTION_COMPLETE.process(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui sudo", permission = "protogui.command.sudo", runAsync = true)
    public void onSudoCommand(@Source PlatformInteraction.ProtoSender sender, Collection<PlatformInteraction.ProtoPlayer> targets, @Greedy String command) {

        targets.forEach(p -> p.run(command));
        sender.send(Message.ACTION_COMPLETE.process(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui|pgui|protogui title", permission = "protogui.command.title", runAsync = true)
    public void onTitleCommand(@Source PlatformInteraction.ProtoSender sender, Collection<PlatformInteraction.ProtoPlayer> targets, String mode, int fadeIn, int stay, int fadeOut, String message) {

        targets.forEach(p -> p.title(Message.process(p, message), fadeIn, stay, fadeOut));
        sender.send(Message.ACTION_COMPLETE.process(null, Pair.of("count", String.valueOf(targets.size()))));
    }
}
