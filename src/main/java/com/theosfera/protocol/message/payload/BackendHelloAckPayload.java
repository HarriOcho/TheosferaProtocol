package com.theosfera.protocol.message.payload;

import java.util.Objects;

public record BackendHelloAckPayload(
        boolean accepted,
        String message
) {

    private static final int MAX_MESSAGE_LENGTH = 256;

    public BackendHelloAckPayload {
        message = Objects.requireNonNull(
                message,
                "message cannot be null"
        ).trim();

        if (message.isEmpty()) {
            throw new IllegalArgumentException(
                    "message cannot be blank"
            );
        }

        if (message.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException(
                    "message cannot contain more than "
                            + MAX_MESSAGE_LENGTH
                            + " characters"
            );
        }
    }
}