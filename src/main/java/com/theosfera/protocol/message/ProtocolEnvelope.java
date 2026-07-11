package com.theosfera.protocol.message;

import com.theosfera.protocol.ProtocolVersion;

import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public record ProtocolEnvelope<T>(
        int version,
        String type,
        UUID requestId,
        long timestamp,
        T payload
) {

    private static final Pattern TYPE_PATTERN =
            Pattern.compile("^[A-Z][A-Z0-9_]{0,63}$");

    public ProtocolEnvelope {
        if (version != ProtocolVersion.CURRENT) {
            throw new IllegalArgumentException(
                    "Unsupported protocol version: " + version
            );
        }

        type = Objects.requireNonNull(type, "type cannot be null").trim();

        if (!TYPE_PATTERN.matcher(type).matches()) {
            throw new IllegalArgumentException(
                    "type must use UPPER_SNAKE_CASE and contain at most 64 characters"
            );
        }

        Objects.requireNonNull(requestId, "requestId cannot be null");

        if (timestamp <= 0) {
            throw new IllegalArgumentException(
                    "timestamp must be greater than zero"
            );
        }

        Objects.requireNonNull(payload, "payload cannot be null");
    }

    public static <T> ProtocolEnvelope<T> create(
            String type,
            T payload
    ) {
        return new ProtocolEnvelope<>(
                ProtocolVersion.CURRENT,
                type,
                UUID.randomUUID(),
                System.currentTimeMillis(),
                payload
        );
    }
}