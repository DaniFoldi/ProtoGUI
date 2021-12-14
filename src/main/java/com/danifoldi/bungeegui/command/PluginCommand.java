package com.danifoldi.bungeegui.command;

import com.danifoldi.bungeegui.main.BungeeGuiAPI;
import com.danifoldi.bungeegui.util.Message;
import com.danifoldi.bungeegui.util.Pair;
import com.danifoldi.bungeegui.util.SoundUtil;
import dev.simplix.protocolize.api.SoundCategory;
import grapefruit.command.CommandContainer;
import grapefruit.command.CommandDefinition;
import grapefruit.command.dispatcher.Redirect;
import grapefruit.command.parameter.modifier.OptParam;
import grapefruit.command.parameter.modifier.Range;
import grapefruit.command.parameter.modifier.Source;
import grapefruit.command.parameter.modifier.string.Greedy;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.Collection;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class PluginCommand implements CommandContainer {

    private final @NotNull Logger logger;

    @Inject
    public PluginCommand(final @NotNull Logger logger) {
        this.logger = logger;
    }

    @Redirect(from = "bgui|bungeegui")
    @CommandDefinition(route = "bgui|bungeegui help", permission = "bungeegui.command.help", runAsync = true)
    public void onHelpCommand(@Source CommandSender sender) {

        sender.sendMessage(Message.COMMAND_HELP.toComponent(null));
        sender.sendMessage(Message.COMMAND_ACTIONBAR.toComponent(null));
        sender.sendMessage(Message.COMMAND_BROADCAST.toComponent(null));
        sender.sendMessage(Message.COMMAND_CHAT.toComponent(null));
        sender.sendMessage(Message.COMMAND_CLOSE.toComponent(null));
        sender.sendMessage(Message.COMMAND_GUIS.toComponent(null));
        sender.sendMessage(Message.COMMAND_LOG.toComponent(null));
        sender.sendMessage(Message.COMMAND_OPEN.toComponent(null));
        sender.sendMessage(Message.COMMAND_RELOAD.toComponent(null));
        sender.sendMessage(Message.COMMAND_SEND.toComponent(null));
        sender.sendMessage(Message.COMMAND_SOUND.toComponent(null));
        sender.sendMessage(Message.COMMAND_TITLE.toComponent(null));
    }

    @CommandDefinition(route = "bgui|bungeegui actionbar", permission = "bungeegui.command.actionbar", runAsync = true)
    public void onActionbarCommand(@Source CommandSender sender, Collection<ProxiedPlayer> targets, @Greedy String content) {

        targets.forEach(p -> p.sendMessage(ChatMessageType.ACTION_BAR, Message.toComponent(p, content)));
        sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui broadcast", permission = "bungeegui.command.broadcast", runAsync = true)
    public void onBroadcastCommand(@Source CommandSender sender, Collection<ProxiedPlayer> targets, @Greedy String content) {

        targets.forEach(p -> p.sendMessage(Message.toComponent(p, content)));
        sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui chat", permission = "bungeegui.command.chat", runAsync = true)
    public void onChatCommand(@Source CommandSender sender, ProxiedPlayer target, @Greedy String content) {

        target.chat(Message.colorCodes(BungeeGuiAPI.getInstance().parsePlaceholders(target, content)));
    }

    @CommandDefinition(route = "bgui|bungeegui close", permission = "bungeegui.command.close", runAsync = true)
    public void onCloseCommand(@Source CommandSender sender, Collection<ProxiedPlayer> targets) {

        targets.forEach(BungeeGuiAPI.getInstance()::closeGui);

        sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui list|guis", permission = "bungeegui.command.list", runAsync = true)
    public void onListCommand(@Source CommandSender sender) {

        sender.sendMessage(Message.GUI_LIST_TOP.toComponent(null, Pair.of("count", String.valueOf(BungeeGuiAPI.getInstance().getAvailableGuis().size()))));
        for (final @NotNull String name: BungeeGuiAPI.getInstance().getAvailableGuis().stream().sorted().collect(Collectors.toList())) {
            sender.sendMessage(Message.GUI_LIST_ITEM.toComponent(null, Pair.of("name", name)));
        }
    }

    @CommandDefinition(route = "bgui|bungeegui log", permission = "bungeegui.command.log", runAsync = true)
    public void onLogCommand(@Source CommandSender sender, @Greedy String message) {

        logger.info("Command Log: %s".formatted(message));
    }

    @CommandDefinition(route = "bgui|bungeegui open", permission = "bungeegui.command.open", runAsync = true)
    public void onOpenCommand(@Source CommandSender sender, Collection<ProxiedPlayer> targets, String guiName, @OptParam String target) {

        if (BungeeGuiAPI.getInstance().getGui(guiName) == null) {
            sender.sendMessage(Message.GUI_NOT_FOUND.toComponent(null, Pair.of("name", guiName)));
            return;
        }
        if (target == null && BungeeGuiAPI.getInstance().getGui(guiName).isTargeted()) {
            sender.sendMessage(Message.GUI_TARGET_REQUIRED.toComponent(null));
            return;
        }

        targets.forEach(p -> BungeeGuiAPI.getInstance().openGui(p, guiName, target == null ? "" : target));
        sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui reload", permission = "bungeegui.command.reload", runAsync = true)
    public void onReloadCommand(@Source CommandSender sender) {

        final long length = BungeeGuiAPI.getInstance().reloadGuis();
        sender.sendMessage(Message.RELOAD_SUCCESS.toComponent(null, Pair.of("time", String.valueOf(length))));
    }

    @CommandDefinition(route = "bgui|bungeegui send", permission = "bungeegui.command.send", runAsync = true)
    public void onSendCommand(@Source CommandSender sender, Collection<ProxiedPlayer> targets, String serverName) {

        final @Nullable ServerInfo server = ProxyServer.getInstance().getServerInfo(serverName);

        if (server == null) {
            sender.sendMessage(Message.SERVER_NOT_FOUND.toComponent(null, Pair.of("name", serverName)));
            return;
        }

        targets.forEach(p -> p.connect(server));
        sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui sound", permission = "bungeegui.command.sound", runAsync = true)
    public void onSoundCommand(@Source CommandSender sender, Collection<ProxiedPlayer> targets, String soundName, @OptParam String soundCategory, @OptParam @Range(min = 0.0, max = 1.0) Double volume, @OptParam @Range(min = 0.0, max = 2.0) Double pitch) {

        if (!SoundUtil.isValidSound(soundName)) {
            sender.sendMessage(Message.INVALID_PROPERTY.toComponent(null));
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

        targets.forEach(p -> SoundUtil.playSound(p, soundName, finalCategory, volume == null ? 1.0f : volume.floatValue(), pitch == null ? 1.0f : pitch.floatValue()));
        sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(targets.size()))));
    }

    @CommandDefinition(route = "bgui|bungeegui title", permission = "bungeegui.command.title", runAsync = true)
    public void onTitleCommand(@Source CommandSender sender, Collection<ProxiedPlayer> targets, String mode, int fadeIn, int stay, int fadeOut, String message) {

        for (final @NotNull ProxiedPlayer p: targets) {
            if (mode.equalsIgnoreCase("subtitle")) {
                ProxyServer.getInstance().createTitle().subTitle(Message.toComponent(p, message)).fadeIn(fadeIn).stay(stay).fadeOut(fadeOut).send(p);
            } else {
                ProxyServer.getInstance().createTitle().title(Message.toComponent(p, message)).fadeIn(fadeIn).stay(stay).fadeOut(fadeOut).send(p);
            }
        }

        sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(targets.size()))));
    }
}
