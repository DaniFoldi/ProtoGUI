package com.danifoldi.bungeegui.command;

import com.danifoldi.bungeegui.main.BungeeGuiAPI;
import com.danifoldi.bungeegui.util.Message;
import com.danifoldi.bungeegui.util.Pair;
import com.danifoldi.bungeegui.util.SoundUtil;
import com.danifoldi.bungeegui.util.StringUtil;
import de.exceptionflug.protocolize.world.SoundCategory;
import de.exceptionflug.protocolize.world.WorldModule;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PluginCommand extends Command implements TabExecutor {

    private final List<String> commands = List.of("actionbar", "broadcast", "chat", "close", "guis", "info", "log", "open", "reload", "send", "sound", "title");
    private final Logger logger;

    @Inject
    public PluginCommand(Logger logger) {
        super("bungeegui", "bungeegui.command", "bgui");
        this.logger = logger;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0 || !commands.contains(args[0].toLowerCase(Locale.ROOT))) {
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
            return;
        }

        if (!sender.hasPermission("bungeegui.command." + args[0].toLowerCase(Locale.ROOT))) {
            sender.sendMessage(Message.NO_PERMISSION.toComponent(null));
            return;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "actionbar": {
                if (args.length < 2) {
                    sender.sendMessage(Message.TARGET_REQUIRED.toComponent(null));
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage(Message.EMPTY_MESSAGE.toComponent(null));
                    return;
                }
                Collection<ProxiedPlayer> players = targets(args[1]);
                players.forEach(p -> p.sendMessage(ChatMessageType.ACTION_BAR, Message.toComponent(p, skip(args, 2))));
                sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(players.size()))));
                break;
            }
            case "broadcast": {
                if (args.length < 2) {
                    sender.sendMessage(Message.TARGET_REQUIRED.toComponent(null));
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage(Message.EMPTY_MESSAGE.toComponent(null));
                    return;
                }
                Collection<ProxiedPlayer> players = targets(args[1]);
                players.forEach(p -> p.sendMessage(Message.toComponent(p, skip(args, 2))));
                sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(players.size()))));
                break;
            }
            case "chat": {
                if (args.length < 2) {
                    sender.sendMessage(Message.TARGET_REQUIRED.toComponent(null));
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage(Message.EMPTY_MESSAGE.toComponent(null));
                    return;
                }

                ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(Message.TARGET_NOT_FOUND.toComponent(null));
                    return;
                }
                player.chat(Message.colorCodes(BungeeGuiAPI.getInstance().parsePlaceholders(player, skip(args, 2))));
                break;
            }
            case "close": {
                if (args.length < 2) {
                    sender.sendMessage(Message.TARGET_REQUIRED.toComponent(null));
                    return;
                }

                Collection<ProxiedPlayer> players = targets(args[1]);
                players.forEach(BungeeGuiAPI.getInstance()::closeGui);

                sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(players.size()))));
                break;
            }
            case "guis": {
                sender.sendMessage(Message.GUI_LIST_TOP.toComponent(null, Pair.of("count", String.valueOf(BungeeGuiAPI.getInstance().getAvailableGuis().size()))));
                for (String name : BungeeGuiAPI.getInstance().getAvailableGuis().stream().sorted().collect(Collectors.toList())) {
                    sender.sendMessage(Message.GUI_LIST_ITEM.toComponent(null, Pair.of("name", name)));
                }
                break;
            }
            case "log": {
                if (args.length < 2) {
                    sender.sendMessage(Message.EMPTY_MESSAGE.toComponent(null));
                    return;
                }

                logger.info("Command Log: " + skip(args, 1));
                break;
            }
            case "open": {
                if (args.length < 2) {
                    sender.sendMessage(Message.TARGET_REQUIRED.toComponent(null));
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage(Message.INVALID_PROPERTY.toComponent(null));
                    return;
                }
                String gui = args[2];

                if (BungeeGuiAPI.getInstance().getGui(gui) == null) {
                    sender.sendMessage(Message.GUI_NOT_FOUND.toComponent(null, Pair.of("name", gui)));
                    return;
                }
                if (args.length < 4 && BungeeGuiAPI.getInstance().getGui(gui).isTargeted()) {
                    sender.sendMessage(Message.GUI_TARGET_REQUIRED.toComponent(null));
                    return;
                }

                Collection<ProxiedPlayer> players = targets(args[1]);
                players.forEach(p -> BungeeGuiAPI.getInstance().openGui(p, gui, args.length < 4 ? "" : args[3]));
                sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(players.size()))));
                break;
            }
            case "reload": {
                long length = BungeeGuiAPI.getInstance().reloadGuis();
                sender.sendMessage(Message.RELOAD_SUCCESS.toComponent(null, Pair.of("time", String.valueOf(length))));
                break;
            }
            case "send": {
                if (args.length < 2) {
                    sender.sendMessage(Message.TARGET_REQUIRED.toComponent(null));
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage(Message.INVALID_PROPERTY.toComponent(null));
                    return;
                }

                ServerInfo server = ProxyServer.getInstance().getServerInfo(args[2]);

                if (server == null) {
                    sender.sendMessage(Message.SERVER_NOT_FOUND.toComponent(null, Pair.of("name", args[2])));
                    return;
                }

                Collection<ProxiedPlayer> players = targets(args[1]);
                players.forEach(p -> p.connect(server));
                sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(players.size()))));
                break;
            }
            case "sound": {
                if (args.length < 2) {
                    sender.sendMessage(Message.TARGET_REQUIRED.toComponent(null));
                    return;
                }
                if (args.length < 3) {
                    sender.sendMessage(Message.INVALID_PROPERTY.toComponent(null));
                    return;
                }
                if (!SoundUtil.isValidSound(args[2])) {
                    sender.sendMessage(Message.INVALID_PROPERTY.toComponent(null));
                    return;
                }

                SoundCategory category = SoundCategory.MASTER;
                if (args.length > 3) {
                    try {
                        category = SoundCategory.valueOf(args[3]);
                    } catch (IllegalArgumentException ignored) {

                    }
                }

                float volume = 1f;
                if (args.length > 4) {
                    try {
                        volume = Float.parseFloat(args[4]);
                    } catch (IllegalArgumentException ignored) {

                    }
                }

                float pitch = 1f;

                if (args.length > 5) {
                    try {
                        pitch = Float.parseFloat(args[5]);
                    } catch (IllegalArgumentException ignored) {

                    }
                }

                Collection<ProxiedPlayer> players = targets(args[1]);
                for (ProxiedPlayer player: players) {
                    SoundUtil.playSound(player, args[2], category, volume, pitch);
                }
                sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(players.size()))));
                break;
            }
            case "title": {
                if (args.length < 7) {
                    sender.sendMessage(Message.INVALID_PROPERTY.toComponent(null));
                    return;
                }

                boolean isSubtitle = false;
                if (args[2].equalsIgnoreCase("subtitle")) {
                    isSubtitle = true;
                }

                int fadeIn = 20;
                int stay = 60;
                int fadeOut = 20;

                try {
                    fadeIn = Integer.parseInt(args[3]);
                } catch (NumberFormatException ignored) {

                }
                try {
                    stay = Integer.parseInt(args[4]);
                } catch (NumberFormatException ignored) {

                }
                try {
                    fadeOut = Integer.parseInt(args[5]);
                } catch (NumberFormatException ignored) {

                }

                Collection<ProxiedPlayer> players = targets(args[1]);

                for (ProxiedPlayer player: players) {
                    if (isSubtitle) {
                        ProxyServer.getInstance().createTitle().subTitle(Message.toComponent(player, skip(args, 6))).fadeIn(fadeIn).stay(stay).fadeOut(fadeOut).send(player);
                    } else {
                        ProxyServer.getInstance().createTitle().title(Message.toComponent(player, skip(args, 6))).fadeIn(fadeIn).stay(stay).fadeOut(fadeOut).send(player);
                    }
                }

                sender.sendMessage(Message.ACTION_COMPLETE.toComponent(null, Pair.of("count", String.valueOf(players.size()))));
                break;
            }
        }
    }

    private Collection<ProxiedPlayer> targets(String value) {
        if (value.equals("all")) {
            return ProxyServer.getInstance().getPlayers();
        }

        Pair<String, String> target = StringUtil.get(value);
        switch (target.getFirst().toLowerCase(Locale.ROOT)) {
            case "p":
                return List.of(ProxyServer.getInstance().getPlayer(target.getSecond()));
            case "s":
                return ProxyServer.getInstance().getServersCopy().get(target.getSecond()).getPlayers();
            default:
                return Collections.emptyList();
        }
    }

    private String skip(String[] values, int elements) {
        return Arrays.stream(values).skip(elements).collect(Collectors.joining(" "));
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            return commands
                    .stream()
                    .filter(c -> sender.hasPermission("bungeegui.command." + c))
                    .filter(c -> c.startsWith((args.length == 0) ? "" : args[0]))
                    .collect(Collectors.toList());
        }

        String command = args[0].toLowerCase(Locale.ROOT);
        if (!sender.hasPermission("bungeegui.command." + command)) {
            return Collections.emptyList();
        }

       switch (command) {
           case "reload":
           case "guis":
               return Collections.emptyList();
       }

        return Collections.emptyList();
    }
}
