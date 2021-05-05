package hu.nugget.bungeegui;

import hu.nugget.bungeegui.gui.GuiHandler;
import hu.nugget.bungeegui.util.Message;
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
    private final GuiHandler guiHandler;

    public BungeeGuiCommand(String name, GuiHandler guiHandler) {
        super(guiHandler.getGui(name).getCommandAliases().stream().findFirst().orElseThrow(), guiHandler.getGui(name).getPermission(), guiHandler.getGui(name).getCommandAliases().stream().skip(1L).toArray(String[]::new));
        this.name = name;
        this.guiHandler = guiHandler;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(Message.PLAYER_ONLY.toComponent());
            return;
        }

        if (args.length == 0 && guiHandler.getGui(name).isTargeted()) {
            sender.sendMessage(Message.TARGET_REQUIRED.toComponent());
            return;
        }

        guiHandler.open(name, (ProxiedPlayer)sender, args.length == 0 ? "" : args[0]);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!guiHandler.getGui(name).isTargeted()) {
            return List.of();
        }
        if (args.length > 1) {
            return List.of();
        }

        String filter = args.length == 0 ? "" : args[args.length - 1];
        return ProxyServer.getInstance().getPlayers().stream().map(CommandSender::getName).filter(n -> n.toLowerCase(Locale.ROOT).startsWith(filter.toLowerCase(Locale.ROOT))).collect(Collectors.toList());
    }
}
