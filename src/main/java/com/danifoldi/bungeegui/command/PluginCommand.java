package com.danifoldi.bungeegui.command;

import com.danifoldi.bungeegui.main.BungeeGuiAPI;
import com.danifoldi.bungeegui.util.Message;
import com.danifoldi.bungeegui.util.Pair;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PluginCommand extends Command implements TabExecutor {

    private final List<String> commands = List.of("reload", "guis");

    public PluginCommand() {
        super("bungeegui", "bungeegui.command", "bgui");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0 || !commands.contains(args[0].toLowerCase(Locale.ROOT))) {
            sender.sendMessage(Message.COMMAND_HELP.toComponent(null));
            sender.sendMessage(Message.COMMAND_GUIS.toComponent(null));
            sender.sendMessage(Message.COMMAND_RELOAD.toComponent(null));
            return;
        }

        if (!sender.hasPermission("bungeegui.command." + args[0].toLowerCase(Locale.ROOT))) {
            sender.sendMessage(Message.NO_PERMISSION.toComponent(null));
            return;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload":
                long length = BungeeGuiAPI.getInstance().reloadGuis();
                sender.sendMessage(Message.RELOAD_SUCCESS.toComponent(null, Pair.of("time", String.valueOf(length))));
                break;
            case "guis":
                sender.sendMessage(Message.GUI_LIST_TOP.toComponent(null, Pair.of("count", String.valueOf(BungeeGuiAPI.getInstance().getAvailableGuis().size()))));
                for (String name: BungeeGuiAPI.getInstance().getAvailableGuis().stream().sorted().collect(Collectors.toList())) {
                    sender.sendMessage(Message.GUI_LIST_ITEM.toComponent(null, Pair.of("name", name)));
                }
                break;
        }
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

       switch (command) {
           case "reload":
           case "guis":
               return Collections.emptyList();
       }

        return Collections.emptyList();
    }
}
