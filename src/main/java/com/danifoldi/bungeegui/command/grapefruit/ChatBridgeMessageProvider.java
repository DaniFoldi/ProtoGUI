package com.danifoldi.bungeegui.command.grapefruit;

import com.danifoldi.messagelib.core.MessageBuilder;
import grapefruit.command.message.MessageKey;
import grapefruit.command.message.MessageProvider;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.Arrays;

public class ChatBridgeMessageProvider implements MessageProvider {
    private final MessageBuilder<String, String> messageBuilder;

    @Inject
    public ChatBridgeMessageProvider(final @NotNull MessageBuilder<String, String> messageBuilder) {
        this.messageBuilder = messageBuilder;
    }

    @Override
    public @NotNull String provide(final @NotNull MessageKey messageKey) {
        final String chatBridgeKey = toCamelCase(messageKey.key());
        return this.messageBuilder.getBase(chatBridgeKey).execute();
    }

    private static String toCamelCase(final @NotNull String notSoCamelCaseKey) {
        final String[] keyParts = notSoCamelCaseKey.split("-");
        if (keyParts.length == 0) {
            return "";
        }

        final StringBuilder keyBuilder = new StringBuilder();
        // The first part doesn't get modified
        keyBuilder.append(keyParts[0]);

        if (keyParts.length <= 1) {
            return keyBuilder.toString();
        }

        Arrays.stream(keyParts).skip(1L)
                .forEach(keyPart -> {
                    if (!keyPart.isEmpty()) {
                        final String camelCaseKeyPart = Character.toTitleCase(keyPart.charAt(0))
                                + (keyPart.length() > 1 ? keyPart.substring(1) : "");
                        keyBuilder.append(camelCaseKeyPart);
                    }
                });

        return keyBuilder.toString();
    }
}
