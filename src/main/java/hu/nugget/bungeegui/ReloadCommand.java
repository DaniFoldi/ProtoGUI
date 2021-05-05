package hu.nugget.bungeegui;

import hu.nugget.bungeegui.util.Message;
import hu.nugget.bungeegui.util.Pair;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.time.Duration;
import java.time.Instant;

public class ReloadCommand extends Command {

    private final BungeeGuiLoader loader;

    public ReloadCommand(BungeeGuiLoader loader) {
        super("bguireload", "bungeegui.reload");
        this.loader = loader;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Instant loadStart = Instant.now();

        loader.unload();
        loader.load();

        Instant loadEnd = Instant.now();
        long length = Duration.between(loadStart, loadEnd).toMillis();

        sender.sendMessage(Message.RELOAD_SUCCESS.toComponent(Pair.of("time", String.valueOf(length))));
    }
}
