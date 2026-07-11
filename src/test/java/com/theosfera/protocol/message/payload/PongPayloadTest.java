package com.theosfera.protocol.message.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PongPayloadTest {

    private static final long PING_SENT_AT =
            1_750_000_000_000L;

    @Test
    void createsValidPayload() {
        PongPayload payload = new PongPayload(
                PING_SENT_AT,
                PING_SENT_AT + 25
        );

        assertEquals(PING_SENT_AT, payload.pingSentAt());
        assertEquals(PING_SENT_AT + 25, payload.respondedAt());
    }

    @Test
    void acceptsEqualTimestamps() {
        PongPayload payload = new PongPayload(
                PING_SENT_AT,
                PING_SENT_AT
        );

        assertEquals(PING_SENT_AT, payload.respondedAt());
    }

    @Test
    void rejectsZeroPingTimestamp() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PongPayload(0, PING_SENT_AT)
        );
    }

    @Test
    void rejectsNegativePingTimestamp() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PongPayload(-1, PING_SENT_AT)
        );
    }

    @Test
    void rejectsZeroResponseTimestamp() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PongPayload(PING_SENT_AT, 0)
        );
    }

    @Test
    void rejectsNegativeResponseTimestamp() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PongPayload(PING_SENT_AT, -1)
        );
    }

    @Test
    void rejectsResponseEarlierThanPing() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PongPayload(
                        PING_SENT_AT,
                        PING_SENT_AT - 1
                )
        );
    }
}