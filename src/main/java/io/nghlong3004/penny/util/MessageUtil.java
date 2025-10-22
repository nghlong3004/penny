package io.nghlong3004.penny.util;

import io.nghlong3004.penny.model.type.MessageType;

import java.util.regex.Pattern;

public final class MessageUtil {

    private static final Pattern TRANSACTION_GUESS_PATTERN = Pattern.compile(".*\\d.*", Pattern.CASE_INSENSITIVE);

    public static MessageType classifyMessage(String rawMessage) {
        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            return MessageType.UNKNOWN;
        }

        String message = rawMessage.trim();

        if (message.startsWith("/")) {
            return MessageType.COMMAND;
        }

        if (TRANSACTION_GUESS_PATTERN.matcher(message).matches()) {
            return MessageType.TRANSACTION;
        }

        return MessageType.UNKNOWN;
    }

    private MessageUtil() {
    }

}
