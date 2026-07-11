package com.theosfera.protocol.message.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PingPayloadTest {

    @Test
    void createsValidPayload() {
        PingPayload payload = new PingPayload(1_750_000_000_000L);

        assertEquals(1_750_000_000_000L, payload.sentAt());
    }

    @Test
    void rejectsZeroTimestamp() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PingPayload(0)
        );
    }

    @Test
    void rejectsNegativeTimestamp() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PingPayload(-1)
        );
    }
}