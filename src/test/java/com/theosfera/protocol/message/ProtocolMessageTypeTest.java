package com.theosfera.protocol.message;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProtocolMessageTypeTest {

    @Test
    void containsAllInitialMessageTypes() {
        assertEquals(
                Set.of(
                        "BACKEND_HELLO",
                        "BACKEND_HELLO_ACK",
                        "PLAYER_AUTHENTICATED",
                        "PLAYER_SERVER_READY",
                        "TRANSFER_REQUEST",
                        "TRANSFER_RESULT",
                        "PING",
                        "PONG"
                ),
                ProtocolMessageType.knownTypes()
        );
    }

    @Test
    void recognizesKnownMessageType() {
        assertTrue(
                ProtocolMessageType.isKnown(
                        ProtocolMessageType.TRANSFER_REQUEST
                )
        );
    }

    @Test
    void rejectsUnknownMessageType() {
        assertFalse(
                ProtocolMessageType.isKnown("UNKNOWN_MESSAGE")
        );
    }

    @Test
    void rejectsNullMessageType() {
        assertFalse(ProtocolMessageType.isKnown(null));
    }

    @Test
    void exposesImmutableKnownTypes() {
        assertThrows(
                UnsupportedOperationException.class,
                () -> ProtocolMessageType.knownTypes().add("NEW_TYPE")
        );
    }
}