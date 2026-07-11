package com.theosfera.protocol.message;

import java.util.Set;

public final class ProtocolMessageType {

    public static final String BACKEND_HELLO = "BACKEND_HELLO";
    public static final String BACKEND_HELLO_ACK = "BACKEND_HELLO_ACK";
    public static final String PLAYER_AUTHENTICATED = "PLAYER_AUTHENTICATED";
    public static final String PLAYER_SERVER_READY = "PLAYER_SERVER_READY";
    public static final String TRANSFER_REQUEST = "TRANSFER_REQUEST";
    public static final String TRANSFER_RESULT = "TRANSFER_RESULT";
    public static final String PING = "PING";
    public static final String PONG = "PONG";

    private static final Set<String> KNOWN_TYPES = Set.of(
            BACKEND_HELLO,
            BACKEND_HELLO_ACK,
            PLAYER_AUTHENTICATED,
            PLAYER_SERVER_READY,
            TRANSFER_REQUEST,
            TRANSFER_RESULT,
            PING,
            PONG
    );

    private ProtocolMessageType() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean isKnown(String type) {
        return type != null && KNOWN_TYPES.contains(type);
    }

    public static Set<String> knownTypes() {
        return KNOWN_TYPES;
    }
}