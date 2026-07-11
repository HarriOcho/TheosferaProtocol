package com.theosfera.protocol.message.payload;

public record PingPayload(long sentAt) {

    public PingPayload {
        if (sentAt <= 0) {
            throw new IllegalArgumentException(
                    "sentAt must be greater than zero"
            );
        }
    }
}