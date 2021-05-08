package com.danifoldi.bungeegui.command;

import com.danifoldi.bungeegui.main.BungeeGuiAPI;
import com.danifoldi.bungeegui.util.Message;
import com.danifoldi.bungeegui.util.Pair;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCommand extends Command {

    public ReloadCommand() {
        super("bguireload", "bungeegui.command.reload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        long length = BungeeGuiAPI.getInstance().reloadGuis();
        sender.sendMessage(Message.RELOAD_SUCCESS.toComponent(Pair.of("time", String.valueOf(length))));
    }
}
