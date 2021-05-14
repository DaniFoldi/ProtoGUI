package com.danifoldi.bungeegui.util;

import com.electronwill.nightconfig.core.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Message {

    PLAYER_ONLY("playerOnly", "&cOnly players can execute this command"),
    TARGET_REQUIRED("targetRequired", "&cThis command requires a target player"),
    NO_SELF_TARGET("noSelfTarget", "&cYou can't target yourself with this command"),
    SERVER_DISABLED("serverDisabled", "&cYou can't use this command on this server"),
    TARGET_BYPASS("targetBypass", "&cThis player can't be targeted with this command"),
    TARGET_NOT_FOUND("targetNotFound", "&cTarget {target} could not be found"),
    RELOAD_SUCCESS("reloadSuccess", "&bPlugin reloaded successfully in &l{time}ms");

    private static Map<String, String> messages = new HashMap<>();
    public static void setMessageProvider(Config config) {
        final Map<String, String> messages = new HashMap<>();
        for (Config.Entry message: ((Config)config.get("messages")).entrySet()) {
            messages.put(message.getKey(), message.getValue());
        }

        Message.messages = messages;
    }

    private final String messageId;
    private final String defaultValue;

    Message(String messageId, String defaultValue) {
        this.messageId = messageId;
        this.defaultValue = defaultValue;
    }

    @SafeVarargs
    public static String replace(String text, Pair<String, String>... replacements) {
        String result = text;
        for (Pair<String, String> replacement: replacements) {
            result = result.replace("{" + replacement.getFirst() + "}", replacement.getSecond());
        }

        return result;
    }

    private static final Pattern HEX = Pattern.compile("(&#[a-fA-F0-9]{6})");
    private static String colorCodes(String text) {
        String colorized = text;
        Matcher matcher = HEX.matcher(colorized);

        while (matcher.find()) {
            String color = matcher.group(1);
            String value = ChatColor.COLOR_CHAR + "x" + color.replace("&#", "").chars().mapToObj(i -> (char)i).map(String::valueOf).map(s -> ChatColor.COLOR_CHAR + s).collect(Collectors.joining());
            colorized = colorized.replace(color, value);
        }
        return ChatColor.translateAlternateColorCodes('&', colorized);
    }

    @SafeVarargs
    public final BaseComponent[] toComponent(Pair<String, String>... replacements) {
        if (!Message.messages.containsKey(messageId) || Message.messages.get(messageId).equals("")) {
            return toComponent(defaultValue, replacements);
        }

        return toComponent(Message.messages.get(messageId), replacements);
    }

    @SafeVarargs
    public static BaseComponent[] toComponent(String value, Pair<String, String>... replacements) {
        return new BaseComponent[] {new TextComponent(colorCodes(replace(value, replacements)))};
    }
}
