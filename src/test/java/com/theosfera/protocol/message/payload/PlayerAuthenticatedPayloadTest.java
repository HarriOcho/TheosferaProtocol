package com.theosfera.protocol.message.payload;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlayerAuthenticatedPayloadTest {

    private static final UUID PLAYER_ID =
            UUID.fromString(
                    "417e98b4-74a1-467e-b453-a15be3af8996"
            );

    private static final long AUTHENTICATED_AT =
            1_750_000_000_000L;

    @Test
    void createsValidPayload() {
        PlayerAuthenticatedPayload payload =
                new PlayerAuthenticatedPayload(
                        PLAYER_ID,
                        "HarriOcho",
                        AUTHENTICATED_AT
                );

        assertEquals(PLAYER_ID, payload.playerId());
        assertEquals("HarriOcho", payload.playerName());
        assertEquals(
                AUTHENTICATED_AT,
                payload.authenticatedAt()
        );
    }

    @Test
    void trimsPlayerName() {
        PlayerAuthenticatedPayload payload =
                new PlayerAuthenticatedPayload(
                        PLAYER_ID,
                        "  HarriOcho  ",
                        AUTHENTICATED_AT
                );

        assertEquals("HarriOcho", payload.playerName());
    }

    @Test
    void acceptsMinimumLengthPlayerName() {
        PlayerAuthenticatedPayload payload =
                new PlayerAuthenticatedPayload(
                        PLAYER_ID,
                        "abc",
                        AUTHENTICATED_AT
                );

        assertEquals("abc", payload.playerName());
    }

    @Test
    void acceptsMaximumLengthPlayerName() {
        String playerName = "a".repeat(16);

        PlayerAuthenticatedPayload payload =
                new PlayerAuthenticatedPayload(
                        PLAYER_ID,
                        playerName,
                        AUTHENTICATED_AT
                );

        assertEquals(playerName, payload.playerName());
    }

    @Test
    void rejectsNullPlayerId() {
        assertThrows(
                NullPointerException.class,
                () -> new PlayerAuthenticatedPayload(
                        null,
                        "HarriOcho",
                        AUTHENTICATED_AT
                )
        );
    }

    @Test
    void rejectsNullPlayerName() {
        assertThrows(
                NullPointerException.class,
                () -> new PlayerAuthenticatedPayload(
                        PLAYER_ID,
                        null,
                        AUTHENTICATED_AT
                )
        );
    }

    @Test
    void rejectsBlankPlayerName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerAuthenticatedPayload(
                        PLAYER_ID,
                        "   ",
                        AUTHENTICATED_AT
                )
        );
    }

    @Test
    void rejectsPlayerNameShorterThanMinimum() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerAuthenticatedPayload(
                        PLAYER_ID,
                        "ab",
                        AUTHENTICATED_AT
                )
        );
    }

    @Test
    void rejectsPlayerNameLongerThanMaximum() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerAuthenticatedPayload(
                        PLAYER_ID,
                        "a".repeat(17),
                        AUTHENTICATED_AT
                )
        );
    }

    @Test
    void rejectsPlayerNameWithInvalidCharacters() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerAuthenticatedPayload(
                        PLAYER_ID,
                        "Harri-Ocho",
                        AUTHENTICATED_AT
                )
        );
    }

    @Test
    void rejectsZeroAuthenticationTimestamp() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerAuthenticatedPayload(
                        PLAYER_ID,
                        "HarriOcho",
                        0
                )
        );
    }

    @Test
    void rejectsNegativeAuthenticationTimestamp() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new PlayerAuthenticatedPayload(
                        PLAYER_ID,
                        "HarriOcho",
                        -1
                )
        );
    }
}