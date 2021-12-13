package com.danifoldi.bungeegui.command.grapefruit;

import grapefruit.command.message.MessageKey;

public final class ChatBridgeMessageKeys {
    public static final MessageKey NO_SUCH_PLAYER = MessageKey.of("command.no-such-player");

    private ChatBridgeMessageKeys() {
        throw new UnsupportedOperationException();
    }
}
