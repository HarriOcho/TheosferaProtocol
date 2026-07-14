package com.theosfera.protocol.message;

import com.theosfera.protocol.message.payload.BackendHelloAckPayload;
import com.theosfera.protocol.message.payload.BackendHelloPayload;
import com.theosfera.protocol.message.payload.PingPayload;
import com.theosfera.protocol.message.payload.PlayerAuthenticatedAckPayload;
import com.theosfera.protocol.message.payload.PlayerAuthenticatedPayload;
import com.theosfera.protocol.message.payload.PlayerServerReadyPayload;
import com.theosfera.protocol.message.payload.PongPayload;
import com.theosfera.protocol.message.payload.TransferRequestPayload;
import com.theosfera.protocol.message.payload.TransferResultPayload;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ProtocolMessageRegistry {

    private static final Map<String, Class<?>> PAYLOAD_TYPES =
            Map.ofEntries(
                    Map.entry(
                            ProtocolMessageType.BACKEND_HELLO,
                            BackendHelloPayload.class
                    ),
                    Map.entry(
                            ProtocolMessageType.BACKEND_HELLO_ACK,
                            BackendHelloAckPayload.class
                    ),
                    Map.entry(
                            ProtocolMessageType.PLAYER_AUTHENTICATED,
                            PlayerAuthenticatedPayload.class
                    ),
                    Map.entry(
                            ProtocolMessageType.PLAYER_AUTHENTICATED_ACK,
                            PlayerAuthenticatedAckPayload.class
                    ),
                    Map.entry(
                            ProtocolMessageType.PLAYER_SERVER_READY,
                            PlayerServerReadyPayload.class
                    ),
                    Map.entry(
                            ProtocolMessageType.TRANSFER_REQUEST,
                            TransferRequestPayload.class
                    ),
                    Map.entry(
                            ProtocolMessageType.TRANSFER_RESULT,
                            TransferResultPayload.class
                    ),
                    Map.entry(
                            ProtocolMessageType.PING,
                            PingPayload.class
                    ),
                    Map.entry(
                            ProtocolMessageType.PONG,
                            PongPayload.class
                    )
            );

    private ProtocolMessageRegistry() {
        throw new UnsupportedOperationException(
                "ProtocolMessageRegistry cannot be instantiated"
        );
    }

    public static Optional<Class<?>> payloadType(String messageType) {
        if (messageType == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(PAYLOAD_TYPES.get(messageType));
    }

    public static boolean isRegistered(String messageType) {
        return messageType != null
                && PAYLOAD_TYPES.containsKey(messageType);
    }

    public static Set<String> registeredTypes() {
        return PAYLOAD_TYPES.keySet();
    }
}
