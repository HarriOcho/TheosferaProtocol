package com.theosfera.protocol.message;

import com.theosfera.protocol.message.payload.BackendHelloAckPayload;
import com.theosfera.protocol.message.payload.BackendHelloPayload;
import com.theosfera.protocol.message.payload.PingPayload;
import com.theosfera.protocol.message.payload.PlayerAuthenticatedPayload;
import com.theosfera.protocol.message.payload.PlayerServerReadyPayload;
import com.theosfera.protocol.message.payload.PongPayload;
import com.theosfera.protocol.message.payload.TransferRequestPayload;
import com.theosfera.protocol.message.payload.TransferResultPayload;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProtocolMessageRegistryTest {

    private static final Map<String, Class<?>> EXPECTED_TYPES =
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

    @Test
    void mapsEveryKnownTypeToItsPayloadClass() {
        EXPECTED_TYPES.forEach((messageType, payloadType) -> {
            assertTrue(
                    ProtocolMessageRegistry.isRegistered(
                            messageType
                    )
            );

            assertEquals(
                    payloadType,
                    ProtocolMessageRegistry.payloadType(
                            messageType
                    ).orElseThrow()
            );
        });
    }

    @Test
    void registersExactlyTheKnownMessageTypes() {
        assertEquals(
                ProtocolMessageType.knownTypes(),
                ProtocolMessageRegistry.registeredTypes()
        );
    }

    @Test
    void returnsEmptyForUnknownType() {
        assertTrue(
                ProtocolMessageRegistry.payloadType(
                        "FUTURE_MESSAGE"
                ).isEmpty()
        );

        assertFalse(
                ProtocolMessageRegistry.isRegistered(
                        "FUTURE_MESSAGE"
                )
        );
    }

    @Test
    void returnsEmptyForNullType() {
        assertTrue(
                ProtocolMessageRegistry.payloadType(null).isEmpty()
        );

        assertFalse(
                ProtocolMessageRegistry.isRegistered(null)
        );
    }

    @Test
    void doesNotNormalizeMessageTypes() {
        assertTrue(
                ProtocolMessageRegistry.payloadType(
                        " PING "
                ).isEmpty()
        );

        assertFalse(
                ProtocolMessageRegistry.isRegistered("ping")
        );
    }

    @Test
    void exposesImmutableRegisteredTypes() {
        Set<String> registeredTypes =
                ProtocolMessageRegistry.registeredTypes();

        assertThrows(
                UnsupportedOperationException.class,
                () -> registeredTypes.add("UNKNOWN")
        );
    }
}