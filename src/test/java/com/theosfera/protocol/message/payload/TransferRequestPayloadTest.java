package com.theosfera.protocol.message.payload;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransferRequestPayloadTest {

    private static final UUID PLAYER_ID =
            UUID.fromString(
                    "417e98b4-74a1-467e-b453-a15be3af8996"
            );

    @Test
    void createsLobbyTransferRequest() {
        TransferRequestPayload payload =
                new TransferRequestPayload(
                        PLAYER_ID,
                        BackendType.LOBBY
                );

        assertEquals(PLAYER_ID, payload.playerId());
        assertEquals(
                BackendType.LOBBY,
                payload.targetBackendType()
        );
    }

    @Test
    void createsSkyblockTransferRequest() {
        TransferRequestPayload payload =
                new TransferRequestPayload(
                        PLAYER_ID,
                        BackendType.SKYBLOCK
                );

        assertEquals(
                BackendType.SKYBLOCK,
                payload.targetBackendType()
        );
    }

    @Test
    void rejectsNullPlayerId() {
        assertThrows(
                NullPointerException.class,
                () -> new TransferRequestPayload(
                        null,
                        BackendType.LOBBY
                )
        );
    }

    @Test
    void rejectsNullTargetBackendType() {
        assertThrows(
                NullPointerException.class,
                () -> new TransferRequestPayload(
                        PLAYER_ID,
                        null
                )
        );
    }

    @Test
    void rejectsAuthAsTransferTarget() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new TransferRequestPayload(
                        PLAYER_ID,
                        BackendType.AUTH
                )
        );
    }
}