package com.theosfera.protocol.message.payload;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransferResultPayloadTest {

    private static final UUID PLAYER_ID =
            UUID.fromString(
                    "417e98b4-74a1-467e-b453-a15be3af8996"
            );

    @Test
    void createsPayloadForEveryStatus() {
        for (TransferResultStatus status
                : TransferResultStatus.values()) {
            TransferResultPayload payload =
                    new TransferResultPayload(
                            PLAYER_ID,
                            status,
                            "Transfer result"
                    );

            assertEquals(PLAYER_ID, payload.playerId());
            assertEquals(status, payload.status());
            assertEquals(
                    "Transfer result",
                    payload.message()
            );
        }
    }

    @Test
    void trimsMessage() {
        TransferResultPayload payload =
                new TransferResultPayload(
                        PLAYER_ID,
                        TransferResultStatus.SUCCESS,
                        "  Transfer completed  "
                );

        assertEquals(
                "Transfer completed",
                payload.message()
        );
    }

    @Test
    void acceptsMessageAtMaximumLength() {
        String message = "a".repeat(256);

        TransferResultPayload payload =
                new TransferResultPayload(
                        PLAYER_ID,
                        TransferResultStatus.FAILED,
                        message
                );

        assertEquals(256, payload.message().length());
    }

    @Test
    void rejectsNullPlayerId() {
        assertThrows(
                NullPointerException.class,
                () -> new TransferResultPayload(
                        null,
                        TransferResultStatus.SUCCESS,
                        "Transfer completed"
                )
        );
    }

    @Test
    void rejectsNullStatus() {
        assertThrows(
                NullPointerException.class,
                () -> new TransferResultPayload(
                        PLAYER_ID,
                        null,
                        "Transfer failed"
                )
        );
    }

    @Test
    void rejectsNullMessage() {
        assertThrows(
                NullPointerException.class,
                () -> new TransferResultPayload(
                        PLAYER_ID,
                        TransferResultStatus.FAILED,
                        null
                )
        );
    }

    @Test
    void rejectsBlankMessage() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TransferResultPayload(
                        PLAYER_ID,
                        TransferResultStatus.REJECTED,
                        "   "
                )
        );
    }

    @Test
    void rejectsMessageLongerThanMaximum() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TransferResultPayload(
                        PLAYER_ID,
                        TransferResultStatus.TIMED_OUT,
                        "a".repeat(257)
                )
        );
    }
}