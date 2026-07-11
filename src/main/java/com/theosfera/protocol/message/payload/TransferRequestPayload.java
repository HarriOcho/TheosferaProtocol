package com.theosfera.protocol.message.payload;

import java.util.Objects;
import java.util.UUID;

public record TransferRequestPayload(
        UUID playerId,
        BackendType targetBackendType
) {

    public TransferRequestPayload {
        Objects.requireNonNull(
                playerId,
                "playerId cannot be null"
        );

        Objects.requireNonNull(
                targetBackendType,
                "targetBackendType cannot be null"
        );

        if (targetBackendType == BackendType.AUTH) {
            throw new IllegalArgumentException(
                    "AUTH cannot be used as a transfer target"
            );
        }
    }
}