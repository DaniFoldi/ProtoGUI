package com.danifoldi.bungeegui.util;

import com.danifoldi.bungeegui.main.BungeeGuiAPI;
import com.electronwill.nightconfig.core.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum Message {

    PLAYER_ONLY("playerOnly", "&cOnly players can execute this command"),
    TARGET_REQUIRED("targetRequired", "&cThis command requires a target player"),
    NO_SELF_TARGET("noSelfTarget", "&cYou can't target yourself with this command"),
    SERVER_DISABLED("serverDisabled", "&cYou can't use this command on this server"),
    TARGET_BYPASS("targetBypass", "&cThis player can't be targeted with this command"),
    TARGET_NOT_FOUND("targetNotFound", "&cTarget {target} could not be found"),
    RELOAD_SUCCESS("reloadSuccess", "&aPlugin reloaded successfully in &l{time}ms"),
    COMMAND_HELP("commandHelp", "&0------------ &e&l%bungeegui% commands &0------------"),
    COMMAND_RELOAD("commandReload", "&0- &6/bgui &lreload&r &0- &7Reload the plugin"),
    COMMAND_GUIS("commandGuis", "&0- &6/bgui &lguis&r &0- &7List the loaded GUIs"),
    COMMAND_BROADCAST("commandBroadcast", "&0- &6/bgui &lbroadcast&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &6<message> &0- &7Send a message to one or more players"),
    COMMAND_LOG("commandLog", "&0- &6/bgui &llog&r &6<message> &0- &7Log a message into the console"),
    COMMAND_SEND("commandSend", "&0- &6/bgui &lsend&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &6<server> &0- &7Send a player to a server"),
    COMMAND_CHAT("commandChat", "&0- &6/bgui &lchat&r &6<player> &6<message> &0- &7Send a message to chat as a player"),
    COMMAND_ACTIONBAR("commandActionbar", "&0- &6/bgui &lactionbar&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &6<text> &0- &7Show a player a message in their action bar"),
    COMMAND_TITLE("commandTitle", "&0- &6/bgui &ltitle&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &7title&f|&7subtitle &6<fadeIn> &6<stay> &6<fadeOut> &6<message> &0- &7Send title to players"),
    COMMAND_SOUND("commandSound", "&0- &6/bgui &lsound&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &6<sound> &6[category] &6[volume] &6[pitch] &0- &7Play a sound for players"),
    COMMAND_OPEN("commandOpen", "&0- &6/bgui &lopen&r &7all&f|&7s:&6<server>&f|&7p:&6<player> &6<gui> &6[target] &0- &7Open a GUI for players"),
    COMMAND_CLOSE("commandClose", "&0- &6/bgui &lclose&r &6<player> &0- &7Close the GUI for players"),
    ACTION_COMPLETE("actionComplete", "&aAction completed for {count} players"),
    GUI_NOT_FOUND("guiNotFound", "&cGUI {name} not found"),
    GUI_TARGET_REQUIRED("guiTargetRequired", "&cThis GUI requires a target player"),
    INVALID_PROPERTY("invalidProperty", "&cA property is invalid"),
    SERVER_NOT_FOUND("serverNotFound", "&cServer {name} not found"),
    EMPTY_MESSAGE("emptyMessage", "&cMessage can't be empty"),
    NO_PERMISSION("noPermission", "&cYou don't have permission to execute that command"),
    GUI_LIST_TOP("guiListTop", "&a{count} GUIs are loaded:"),
    GUI_LIST_ITEM("guiListItem", "&0- &6{name}");

    private static final @NotNull Map<String, String> messages = new HashMap<>();
    public static void setMessageProvider(Config config) {
        final @NotNull Map<String, String> messages = new HashMap<>();
        for (final @NotNull Config.Entry message: ((Config)config.get("messages")).entrySet()) {
            messages.put(message.getKey(), message.getValue());
        }

        Message.messages.clear();
        Message.messages.putAll(messages);
    }

    private static final @NotNull Pattern HEX = Pattern.compile("(&#[a-fA-F0-9]{6})");

    private final @NotNull String messageId;
    private final @NotNull String defaultValue;

    Message(final @NotNull String messageId, final @NotNull String defaultValue) {
        this.messageId = messageId;
        this.defaultValue = defaultValue;
    }

    public @NotNull String getDefaultValue() {
        return defaultValue;
    }

    @SafeVarargs
    public static @NotNull String replace(@NotNull String text,final @NotNull Pair<String, String>... replacements) {
        for (Pair<String, String> replacement: replacements) {
            if (replacement.getFirst() == null || replacement.getSecond() == null) {
                continue;
            }
            text = text.replace("{" + replacement.getFirst() + "}", replacement.getSecond());
        }

        return text;
    }

    public static @NotNull String colorCodes(String text) {
        final @NotNull Matcher matcher = HEX.matcher(text);

        while (matcher.find()) {
            final @NotNull String color = matcher.group(1);
            final @NotNull String value = ChatColor.COLOR_CHAR + "x" + color.replace("&#", "").chars().mapToObj(i -> (char)i).map(String::valueOf).map(s -> ChatColor.COLOR_CHAR + s).collect(Collectors.joining());
            text = text.replace(color, value);
        }
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    @SafeVarargs
    public final void send(final @NotNull ProxiedPlayer player, final @NotNull Pair<String, String>... replacements) {
        if (!Message.messages.containsKey(messageId) || Message.messages.get(messageId).equals("")) {
            send(player, defaultValue, replacements);
        }

        send(player, Message.messages.get(messageId), replacements);
    }

    @SafeVarargs
    public static void send(final @NotNull ProxiedPlayer player, final @NotNull String value, final @NotNull Pair<String, String>... replacements) {
        player.sendMessage(toComponent(player, value, replacements));
    }

    @SafeVarargs
    public final @NotNull BaseComponent[] toComponent(final @Nullable ProxiedPlayer player, final @NotNull Pair<String, String>... replacements) {
        if (!Message.messages.containsKey(messageId) || Message.messages.get(messageId) == null || Message.messages.get(messageId).equals("")) {
            toComponent(player, defaultValue, replacements);
        }

        return toComponent(player, Message.messages.get(messageId), replacements);
    }

    @SafeVarargs
    public static @NotNull BaseComponent[] toComponent(final @Nullable ProxiedPlayer player, final @NotNull String value, final @NotNull Pair<String, String>... replacements) {
        return new BaseComponent[] {new TextComponent(colorCodes(BungeeGuiAPI.getInstance().parsePlaceholders(player, replace(value, replacements))))};
    }
}
