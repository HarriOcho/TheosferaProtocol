package com.theosfera.protocol.message;

import com.theosfera.protocol.ProtocolVersion;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProtocolEnvelopeTest {

    @Test
    void createsEnvelopeWithGeneratedMetadata() {
        TestPayload payload = new TestPayload("hello");

        ProtocolEnvelope<TestPayload> envelope =
                ProtocolEnvelope.create("PING", payload);

        assertEquals(ProtocolVersion.CURRENT, envelope.version());
        assertEquals("PING", envelope.type());
        assertNotNull(envelope.requestId());
        assertTrue(envelope.timestamp() > 0);
        assertSame(payload, envelope.payload());
    }

    @Test
    void acceptsValidUpperSnakeCaseType() {
        ProtocolEnvelope<TestPayload> envelope = new ProtocolEnvelope<>(
                ProtocolVersion.CURRENT,
                "PLAYER_SERVER_READY",
                UUID.randomUUID(),
                System.currentTimeMillis(),
                new TestPayload("ready")
        );

        assertEquals("PLAYER_SERVER_READY", envelope.type());
    }

    @Test
    void trimsMessageType() {
        ProtocolEnvelope<TestPayload> envelope = new ProtocolEnvelope<>(
                ProtocolVersion.CURRENT,
                "  PING  ",
                UUID.randomUUID(),
                System.currentTimeMillis(),
                new TestPayload("hello")
        );

        assertEquals("PING", envelope.type());
    }

    @Test
    void rejectsUnsupportedVersion() {
        assertThrows(IllegalArgumentException.class, () ->
                new ProtocolEnvelope<>(
                        ProtocolVersion.CURRENT + 1,
                        "PING",
                        UUID.randomUUID(),
                        System.currentTimeMillis(),
                        new TestPayload("hello")
                )
        );
    }

    @Test
    void rejectsInvalidMessageType() {
        assertThrows(IllegalArgumentException.class, () ->
                new ProtocolEnvelope<>(
                        ProtocolVersion.CURRENT,
                        "player-ready",
                        UUID.randomUUID(),
                        System.currentTimeMillis(),
                        new TestPayload("hello")
                )
        );
    }

    @Test
    void rejectsNullRequestId() {
        assertThrows(NullPointerException.class, () ->
                new ProtocolEnvelope<>(
                        ProtocolVersion.CURRENT,
                        "PING",
                        null,
                        System.currentTimeMillis(),
                        new TestPayload("hello")
                )
        );
    }

    @Test
    void rejectsInvalidTimestamp() {
        assertThrows(IllegalArgumentException.class, () ->
                new ProtocolEnvelope<>(
                        ProtocolVersion.CURRENT,
                        "PING",
                        UUID.randomUUID(),
                        0,
                        new TestPayload("hello")
                )
        );
    }

    @Test
    void rejectsNullPayload() {
        assertThrows(NullPointerException.class, () ->
                new ProtocolEnvelope<>(
                        ProtocolVersion.CURRENT,
                        "PING",
                        UUID.randomUUID(),
                        System.currentTimeMillis(),
                        null
                )
        );
    }

    private record TestPayload(String value) {
    }
}