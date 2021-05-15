package com.danifoldi.bungeegui.main;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Singleton
public class PlaceholderHandler {
    private final Map<String, Function<ProxiedPlayer, String>> builtinPlaceholders = new HashMap<>();
    private final Map<String, Function<ProxiedPlayer, String>> placeholders = new HashMap<>();

    @Inject
    public PlaceholderHandler() {

    }

    void register(String name, Function<ProxiedPlayer, String> placeholder) {
        placeholders.putIfAbsent(name, placeholder);
    }

    void unregister(String name) {
        placeholders.remove(name);
    }

    void registerBuiltin(String name, Function<ProxiedPlayer, String> placeholder) {
        builtinPlaceholders.putIfAbsent(name, placeholder);
    }

    void unregisterBuiltin(String name) {
        builtinPlaceholders.remove(name);
    }

    String parse(ProxiedPlayer player, String text) {
        String result = text;

        for (Map.Entry<String, Function<ProxiedPlayer, String>> placeholder: builtinPlaceholders.entrySet()) {
            String value = placeholder.getValue().apply(player);
            result = result.replace("%" + placeholder.getKey() + "%", value == null ? "" : value);
        }

        for (Map.Entry<String, Function<ProxiedPlayer, String>> placeholder: placeholders.entrySet()) {
            String value = placeholder.getValue().apply(player);
            result = result.replace("%" + placeholder.getKey() + "%", value == null ? "" : value);
        }

        return result;
    }

    void unregisterAll() {
        builtinPlaceholders.clear();
        placeholders.clear();
    }

    void registerBuiltins() {
        registerBuiltin("servername", player -> ProxyServer.getInstance().getName());
        registerBuiltin("online", player -> String.valueOf(ProxyServer.getInstance().getOnlineCount()));
    }
}
