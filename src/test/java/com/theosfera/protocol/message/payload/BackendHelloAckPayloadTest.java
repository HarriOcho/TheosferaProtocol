package com.theosfera.protocol.message.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BackendHelloAckPayloadTest {

    @Test
    void createsAcceptedPayload() {
        BackendHelloAckPayload payload =
                new BackendHelloAckPayload(
                        true,
                        "Backend registered"
                );

        assertTrue(payload.accepted());
        assertEquals("Backend registered", payload.message());
    }

    @Test
    void createsRejectedPayload() {
        BackendHelloAckPayload payload =
                new BackendHelloAckPayload(
                        false,
                        "Unauthorized backend"
                );

        assertFalse(payload.accepted());
        assertEquals("Unauthorized backend", payload.message());
    }

    @Test
    void trimsMessage() {
        BackendHelloAckPayload payload =
                new BackendHelloAckPayload(
                        true,
                        "  Backend registered  "
                );

        assertEquals("Backend registered", payload.message());
    }

    @Test
    void rejectsNullMessage() {
        assertThrows(
                NullPointerException.class,
                () -> new BackendHelloAckPayload(true, null)
        );
    }

    @Test
    void rejectsBlankMessage() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BackendHelloAckPayload(false, "   ")
        );
    }

    @Test
    void rejectsMessageLongerThanMaximum() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BackendHelloAckPayload(
                        false,
                        "a".repeat(257)
                )
        );
    }

    @Test
    void acceptsMessageAtMaximumLength() {
        BackendHelloAckPayload payload =
                new BackendHelloAckPayload(
                        true,
                        "a".repeat(256)
                );

        assertEquals(256, payload.message().length());
    }
}