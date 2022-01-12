package com.danifoldi.protogui.platform;

import com.danifoldi.protogui.util.Message;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface PlatformInteraction {

    interface ProtoSender {
        boolean hasPermission(String permission);
        void send(String message);
        default void send(Message message) {
            send(message.process(this));
        }

        String displayName();
        String name();
        UUID uniqueId();
    }

    interface ProtoPlayer extends ProtoSender {
        boolean vanished();
        int protocol();
        int ping();
        String locale();
        void run(String command);

        void actionbar(String message);
        void chat(String message);
        void connect(ProtoServer server);
        ProtoServer connectedTo();
        void title(String message, int fadeIn, int stay, int fadeOut);
        void subtitle(String message, int fadeIn, int stay, int fadeOut);
    }

    interface ProtoServer {
        String name();
        String motd();
        Collection<ProtoPlayer> players();
        boolean online();
        String version();
        int playerMax();
        int playerCount();
    }

    interface ProtoPlugin {
        String name();
        String description();
        String main();
        String version();
        List<String> authors();
        List<String> dependencies();
        List<String> softDependencies();
    }

    ProtoPlayer getPlayer(UUID uuid);
    ProtoPlayer getPlayer(String name);
    List<ProtoPlayer> getPlayers();
    String platformName();
    String platformVersion();
    String pluginName();
    String pluginVersion();
    int maxPlayerCount();
    void setup();
    void teardown();
    void registerCommand(List<String> commandAliases, BiConsumer<ProtoSender, String> dispatch, BiFunction<ProtoSender, String, Collection<String>> suggest);
    void unregisterCommand(String command);
    void runConsoleCommand(String command);
    List<ProtoPlugin> getPlugins();
    Map<String, ProtoServer> getServers();
}
