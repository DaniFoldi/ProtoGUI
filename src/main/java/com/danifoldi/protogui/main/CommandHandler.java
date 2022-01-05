package com.danifoldi.protogui.main;

import com.danifoldi.protogui.gui.GuiGrid;
import com.danifoldi.protogui.platform.PlatformInteraction;
import com.danifoldi.protogui.util.Message;
import com.danifoldi.protogui.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

@SuppressWarnings("ClassCanBeARecord")
public class CommandHandler {

    private final @NotNull String name;
    private final @NotNull GuiGrid gui;

    public CommandHandler(final @NotNull String name,
                          final @NotNull GuiGrid gui) {
        this.name = name;
        this.gui = gui;
    }

    public void dispatch(PlatformInteraction.ProtoSender sender, String args) {
        if (!(sender instanceof final @NotNull PlatformInteraction.ProtoPlayer player)) {
            sender.send(Message.PLAYER_ONLY);
            return;
        }

        final @NotNull String server = player.connectedTo().name();
        if (gui.getBlacklistServers().contains(server)) {
            Message.SERVER_DISABLED.send(player);
            return;
        }
        if (gui.getWhitelistServers().isEmpty() || (!gui.getWhitelistServers().get(0).equals("*")) && !gui.getWhitelistServers().contains(server)) {
            Message.SERVER_DISABLED.send(player);
            return;
        }

        if (args.length() == 0 && gui.isTargeted()) {
            Message.TARGET_REQUIRED.send(player);
            return;
        }

        @NotNull String target = args.length() == 0 ? "" : args.split(" ")[0];
        if (gui.isRequireOnlineTarget()) {
            final @Nullable PlatformInteraction.ProtoPlayer targetPlayer = ProtoGuiAPI.getInstance().getPlatform().getPlayer(target);
            if (targetPlayer == null) {
                Message.TARGET_NOT_FOUND.send(player, Pair.of("target", target));
                return;
            }
            if (gui.isIgnoreVanished() && targetPlayer.vanished()) {
                Message.TARGET_NOT_FOUND.send(player, Pair.of("target", target));
                return;
            }
        }

        PlatformInteraction.ProtoPlayer targetPlayer = ProtoGuiAPI.getInstance().getPlatform().getPlayer(player.uniqueId());
        if (targetPlayer != null) {
            target = targetPlayer.name();

            if (gui.isTargetBypass() && targetPlayer.hasPermission(gui.getPermission() + ".bypass")) {
                Message.TARGET_BYPASS.send(player);
                return;
            }
        }

        if (gui.isSelfTarget() && player.equals(targetPlayer)) {
            Message.NO_SELF_TARGET.send(player);
            return;
        }

        ProtoGuiAPI.getInstance().openGui(player.uniqueId(), name, target);
    };

    public Collection<String> suggest(PlatformInteraction.ProtoSender sender, String currentArg) {
        if (!gui.isTargeted()) {
            return Collections.emptyList();
        }
            if (currentArg.contains(" ")) {
            return Collections.emptyList();
        }

        @NotNull String filter = currentArg;
        return ProtoGuiAPI
                .getInstance()
                .getPlatform()
                .getPlayers()
                .stream()
                .filter(p -> gui.isIgnoreVanished() && p.vanished())
                .map(PlatformInteraction.ProtoPlayer::name)
                .filter(n -> n.toLowerCase(Locale.ROOT).startsWith(filter.toLowerCase(Locale.ROOT)))
                .filter(n -> gui.isSelfTarget() || !n.equals(sender.name()))
                .collect(Collectors.toList());
    };
}
