package com.theosfera.protocol.message.payload;

import java.util.Objects;
import java.util.regex.Pattern;

public record BackendHelloPayload(
        String backendName,
        BackendType backendType
) {

    private static final Pattern BACKEND_NAME_PATTERN =
            Pattern.compile("^[A-Za-z0-9][A-Za-z0-9_-]{0,63}$");

    public BackendHelloPayload {
        backendName = Objects.requireNonNull(
                backendName,
                "backendName cannot be null"
        ).trim();

        Objects.requireNonNull(
                backendType,
                "backendType cannot be null"
        );

        if (!BACKEND_NAME_PATTERN.matcher(backendName).matches()) {
            throw new IllegalArgumentException(
                    "backendName must contain only letters, numbers, "
                            + "underscores or hyphens and contain at most "
                            + "64 characters"
            );
        }
    }
}