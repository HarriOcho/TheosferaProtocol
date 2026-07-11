package com.theosfera.protocol.message.payload;

public record PongPayload(
        long pingSentAt,
        long respondedAt
) {

    public PongPayload {
        if (pingSentAt <= 0) {
            throw new IllegalArgumentException(
                    "pingSentAt must be greater than zero"
            );
        }

        if (respondedAt <= 0) {
            throw new IllegalArgumentException(
                    "respondedAt must be greater than zero"
            );
        }

        if (respondedAt < pingSentAt) {
            throw new IllegalArgumentException(
                    "respondedAt cannot be earlier than pingSentAt"
            );
        }
    }
}