package com.theosfera.protocol.message.payload;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerServerReadyPayloadTest {

    private static final UUID PLAYER_ID =
            UUID.fromString(
                    "417e98b4-74a1-467e-b453-a15be3af8996"
            );

    private static final long READY_AT =
            1_750_000_000_000L;

    @Test
    void createsValidPayload() {
        PlayerServerReadyPayload payload =
                new PlayerServerReadyPayload(
                        PLAYER_ID,
                        "lobby-1",
                        READY_AT
                );

        assertEquals(PLAYER_ID, payload.playerId());
        assertEquals("lobby-1", payload.backendName());
        assertEquals(READY_AT, payload.readyAt());
    }

    @Test
    void trimsBackendName() {
        PlayerServerReadyPayload payload =
                new PlayerServerReadyPayload(
                        PLAYER_ID,
                        "  skyblock_1  ",
                        READY_AT
                );

        assertEquals("skyblock_1", payload.backendName());
    }

    @Test
    void acceptsBackendNameAtMaximumLength() {
        String backendName = "a".repeat(64);

        PlayerServerReadyPayload payload =
                new PlayerServerReadyPayload(
                        PLAYER_ID,
                        backendName,
                        READY_AT
                );

        assertEquals(backendName, payload.backendName());
    }

    @Test
    void rejectsNullPlayerId() {
        assertThrows(
                NullPointerException.class,
                () -> new PlayerServerReadyPayload(
                        null,
                        "lobby-1",
                        READY_AT
                )
        );
    }

    @Test
    void rejectsNullBackendName() {
        assertThrows(
                NullPointerException.class,
                () -> new PlayerServerReadyPayload(
                        PLAYER_ID,
                        null,
                        READY_AT
                )
        );
    }

    @Test
    void rejectsBlankBackendName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerServerReadyPayload(
                        PLAYER_ID,
                        "   ",
                        READY_AT
                )
        );
    }

    @Test
    void rejectsBackendNameWithInvalidCharacters() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerServerReadyPayload(
                        PLAYER_ID,
                        "lobby 1",
                        READY_AT
                )
        );
    }

    @Test
    void rejectsBackendNameLongerThanMaximum() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerServerReadyPayload(
                        PLAYER_ID,
                        "a".repeat(65),
                        READY_AT
                )
        );
    }

    @Test
    void rejectsZeroReadyTimestamp() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerServerReadyPayload(
                        PLAYER_ID,
                        "lobby-1",
                        0
                )
        );
    }

    @Test
    void rejectsNegativeReadyTimestamp() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerServerReadyPayload(
                        PLAYER_ID,
                        "lobby-1",
                        -1
                )
        );
    }
}