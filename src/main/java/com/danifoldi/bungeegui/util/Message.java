package com.danifoldi.bungeegui.util;

import com.electronwill.nightconfig.core.Config;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.HashMap;
import java.util.Map;

public enum Message {

    PLAYER_ONLY("playerOnly", "&cOnly players can execute this command"),
    TARGET_REQUIRED("targetRequired", "&cThis command requires a target player"),
    RELOAD_SUCCESS("reloadSuccess", "&bPlugin reloaded succeessfully in &l{time}ms");

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
        String result = new String(text);
        for (Pair<String, String> replacement: replacements) {
            result = result.replace("{" + replacement.getFirst() + "}", replacement.getSecond());
        }

        return result;
    }

    private static String colorCodes(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
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
