package com.theosfera.protocol.message.payload;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerAuthenticatedAckPayloadTest {

    private static final UUID PLAYER_ID =
            UUID.fromString(
                    "5cf7b272-2369-4b4f-9945-3507231c3142"
            );

    @Test
    void createsAcceptedAcknowledgement() {
        PlayerAuthenticatedAckPayload payload =
                new PlayerAuthenticatedAckPayload(
                        PLAYER_ID,
                        true,
                        "  Session registered  "
                );

        assertEquals(
                PLAYER_ID,
                payload.playerId()
        );
        assertTrue(payload.accepted());
        assertEquals(
                "Session registered",
                payload.message()
        );
    }

    @Test
    void createsRejectedAcknowledgement() {
        PlayerAuthenticatedAckPayload payload =
                new PlayerAuthenticatedAckPayload(
                        PLAYER_ID,
                        false,
                        "Authentication rejected"
                );

        assertFalse(payload.accepted());
    }

    @Test
    void rejectsNullPlayerId() {
        assertThrows(
                NullPointerException.class,
                () -> new PlayerAuthenticatedAckPayload(
                        null,
                        true,
                        "Session registered"
                )
        );
    }

    @Test
    void rejectsNullMessage() {
        assertThrows(
                NullPointerException.class,
                () -> new PlayerAuthenticatedAckPayload(
                        PLAYER_ID,
                        true,
                        null
                )
        );
    }

    @Test
    void rejectsBlankMessage() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerAuthenticatedAckPayload(
                        PLAYER_ID,
                        false,
                        "   "
                )
        );
    }

    @Test
    void rejectsOversizedMessage() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerAuthenticatedAckPayload(
                        PLAYER_ID,
                        false,
                        "a".repeat(257)
                )
        );
    }

    @Test
    void acceptsMaximumMessageLength() {
        String message = "a".repeat(256);

        PlayerAuthenticatedAckPayload payload =
                new PlayerAuthenticatedAckPayload(
                        PLAYER_ID,
                        true,
                        message
                );

        assertEquals(
                message,
                payload.message()
        );
    }
}
