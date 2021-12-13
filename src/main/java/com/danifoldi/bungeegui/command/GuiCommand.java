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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

public class GuiCommand extends Command implements TabExecutor {

    private final String name;

    public GuiCommand(final @NotNull String name) {
        super(BungeeGuiAPI.getInstance().getGui(name).getCommandAliases().stream().findFirst().orElseThrow(), BungeeGuiAPI.getInstance().getGui(name).getPermission(), BungeeGuiAPI.getInstance().getGui(name).getCommandAliases().stream().skip(1L).toArray(String[]::new));
        this.name = name;
    }

    @Override
    public void execute(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Message.PLAYER_ONLY.toComponent(null));
            return;
        }

        final @NotNull ProxiedPlayer player = (ProxiedPlayer)sender;

        final @NotNull String server = ((ProxiedPlayer)sender).getServer().getInfo().getName();
        if (BungeeGuiAPI.getInstance().getGui(name).getBlacklistServers().contains(server)) {
            Message.SERVER_DISABLED.send(player);
            return;
        }
        if (BungeeGuiAPI.getInstance().getGui(name).getWhitelistServers().isEmpty() || (!BungeeGuiAPI.getInstance().getGui(name).getWhitelistServers().get(0).equals("*")) && !BungeeGuiAPI.getInstance().getGui(name).getWhitelistServers().contains(server)) {
            Message.SERVER_DISABLED.send(player);
            return;
        }

        if (args.length == 0 && BungeeGuiAPI.getInstance().getGui(name).isTargeted()) {
            Message.TARGET_REQUIRED.send(player);
            return;
        }

        @NotNull String target = args.length == 0 ? "" : args[0];
        if (BungeeGuiAPI.getInstance().getGui(name).isRequireOnlineTarget()) {
            final @Nullable ProxiedPlayer targetPlayer = ProxyServer.getInstance().getPlayer(target);
            if (targetPlayer == null) {
                Message.TARGET_NOT_FOUND.send(player, Pair.of("target", target));
                return;
            }
            if (BungeeGuiAPI.getInstance().getGui("name").isIgnoreVanished() && VanishUtil.isVanished(targetPlayer)) {
                Message.TARGET_NOT_FOUND.send(player, Pair.of("target", target));
                return;
            }
        }

        if (ProxyServer.getInstance().getPlayer(target) != null) {
            target = ProxyServer.getInstance().getPlayer(target).getName();

            if (BungeeGuiAPI.getInstance().getGui(name).isTargetBypass() && ProxyServer.getInstance().getPlayer(target).hasPermission(BungeeGuiAPI.getInstance().getGui(name).getPermission() + ".bypass")) {
                Message.TARGET_BYPASS.send(player);
                return;
            }
        }

        if (sender.getName().equals(target)) {
            Message.NO_SELF_TARGET.send(player);
            return;
        }

        BungeeGuiAPI.getInstance().openGui(player, name, target);
    }

    @Override
    public @NotNull Iterable<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull String[] args) {
        if (!BungeeGuiAPI.getInstance().getGui(name).isTargeted()) {
            return Collections.emptyList();
        }
        if (args.length > 1) {
            return Collections.emptyList();
        }

        final @NotNull String filter = args.length == 0 ? "" : args[args.length - 1];
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
