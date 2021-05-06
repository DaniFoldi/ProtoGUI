package com.danifoldi.bungeegui.command;

import com.danifoldi.bungeegui.main.BungeeGuiAPI;
import com.danifoldi.bungeegui.util.Message;
import com.danifoldi.bungeegui.main.GuiHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.List;
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

        if (args.length == 0 && BungeeGuiAPI.getInstance().getGui(name).isTargeted()) {
            sender.sendMessage(Message.TARGET_REQUIRED.toComponent());
            return;
        }

        BungeeGuiAPI.getInstance().openGui((ProxiedPlayer)sender, name, args.length == 0 ? "" : args[0]);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!BungeeGuiAPI.getInstance().getGui(name).isTargeted()) {
            return List.of();
        }
        if (args.length > 1) {
            return List.of();
        }

        final String filter = args.length == 0 ? "" : args[args.length - 1];
        return ProxyServer.getInstance().getPlayers().stream().map(CommandSender::getName).filter(n -> n.toLowerCase(Locale.ROOT).startsWith(filter.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
    }
}
