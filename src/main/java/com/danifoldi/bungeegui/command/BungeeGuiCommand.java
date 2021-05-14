package com.danifoldi.bungeegui.command;

import com.danifoldi.bungeegui.main.BungeeGuiAPI;
import com.danifoldi.bungeegui.util.Message;
import com.danifoldi.bungeegui.util.Pair;
import com.danifoldi.bungeegui.util.VanishUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

public class BungeeGuiCommand extends Command implements TabExecutor {

    private final String name;

    public BungeeGuiCommand(String name) {
        super(BungeeGuiAPI.getInstance().getGui(name).getCommandAliases().stream().findFirst().orElseThrow(), BungeeGuiAPI.getInstance().getGui(name).getPermission(), BungeeGuiAPI.getInstance().getGui(name).getCommandAliases().stream().skip(1L).toArray(String[]::new));
        this.name = name;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Message.PLAYER_ONLY.toComponent());
            return;
        }

        String server = ((ProxiedPlayer)sender).getServer().getInfo().getName();
        if (BungeeGuiAPI.getInstance().getGui(name).getBlacklistServers().contains(server)) {
            sender.sendMessage(Message.SERVER_DISABLED.toComponent());
            return;
        }
        if (BungeeGuiAPI.getInstance().getGui(name).getWhitelistServers().isEmpty() || (!BungeeGuiAPI.getInstance().getGui(name).getWhitelistServers().get(0).equals("*")) && !BungeeGuiAPI.getInstance().getGui(name).getWhitelistServers().contains(server)) {
            sender.sendMessage(Message.SERVER_DISABLED.toComponent());
            return;
        }

        if (args.length == 0 && BungeeGuiAPI.getInstance().getGui(name).isTargeted()) {
            sender.sendMessage(Message.TARGET_REQUIRED.toComponent());
            return;
        }

        String target = args.length == 0 ? "" : args[0];
        if (BungeeGuiAPI.getInstance().getGui(name).isRequireOnlineTarget()) {
            ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);
            if (targetPlayer == null) {
                sender.sendMessage(Message.TARGET_NOT_FOUND.toComponent(Pair.of("target", target)));
                return;
            }
            if (BungeeGuiAPI.getInstance().getGui("name").isIgnoreVanished() && VanishUtil.isVanished(targetPlayer)) {
                sender.sendMessage(Message.TARGET_NOT_FOUND.toComponent(Pair.of("target", target)));
                return;
            }
        }

        if (ProxyServer.getInstance().getPlayer(target) != null) {
            target = ProxyServer.getInstance().getPlayer(target).getName();
        }

        if (sender.getName().equals(target)) {
            sender.sendMessage(Message.NO_SELF_TARGET.toComponent());
            return;
        }

        BungeeGuiAPI.getInstance().openGui((ProxiedPlayer)sender, name, target);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!BungeeGuiAPI.getInstance().getGui(name).isTargeted()) {
            return Collections.emptyList();
        }
        if (args.length > 1) {
            return Collections.emptyList();
        }

        final String filter = args.length == 0 ? "" : args[args.length - 1];
        return ProxyServer
                .getInstance()
                .getPlayers()
                .stream()
                .filter(p -> BungeeGuiAPI.getInstance().getGui("name").isIgnoreVanished() && VanishUtil.isVanished(p))
                .map(CommandSender::getName)
                .filter(n -> n.toLowerCase(Locale.ROOT).startsWith(filter.toLowerCase(Locale.ROOT)))
                .filter(n -> BungeeGuiAPI.getInstance().getGui("name").isSelfTarget() || !n.equals(sender.getName()))
                .collect(Collectors.toList());
    }
}
