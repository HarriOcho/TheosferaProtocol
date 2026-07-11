package com.theosfera.protocol.message.payload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BackendHelloPayloadTest {

    @Test
    void createsValidPayload() {
        BackendHelloPayload payload = new BackendHelloPayload(
                "lobby-1",
                BackendType.LOBBY
        );

        assertEquals("lobby-1", payload.backendName());
        assertEquals(BackendType.LOBBY, payload.backendType());
    }

    @Test
    void trimsBackendName() {
        BackendHelloPayload payload = new BackendHelloPayload(
                "  skyblock_1  ",
                BackendType.SKYBLOCK
        );

        assertEquals("skyblock_1", payload.backendName());
    }

    @Test
    void rejectsNullBackendName() {
        assertThrows(
                NullPointerException.class,
                () -> new BackendHelloPayload(
                        null,
                        BackendType.AUTH
                )
        );
    }

    @Test
    void rejectsBlankBackendName() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BackendHelloPayload(
                        "   ",
                        BackendType.AUTH
                )
        );
    }

    @Test
    void rejectsBackendNameWithInvalidCharacters() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BackendHelloPayload(
                        "lobby 1",
                        BackendType.LOBBY
                )
        );
    }

    @Test
    void rejectsBackendNameLongerThanMaximum() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new BackendHelloPayload(
                        "a".repeat(65),
                        BackendType.LOBBY
                )
        );
    }

    @Test
    void rejectsNullBackendType() {
        assertThrows(
                NullPointerException.class,
                () -> new BackendHelloPayload(
                        "auth-1",
                        null
                )
        );
    }
}