package com.theosfera.protocol.message.payload;

import com.theosfera.protocol.codec.ProtocolJsonCodec;
import com.theosfera.protocol.message.ProtocolEnvelope;
import com.theosfera.protocol.message.ProtocolMessageType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerLifecycleCodecTest {

    private static final UUID PLAYER_ID =
            UUID.fromString(
                    "417e98b4-74a1-467e-b453-a15be3af8996"
            );

    private static final long EVENT_TIMESTAMP =
            1_750_000_000_000L;

    private final ProtocolJsonCodec codec =
            new ProtocolJsonCodec();

    @Test
    void roundTripsPlayerAuthenticatedPayload() {
        PlayerAuthenticatedPayload payload =
                new PlayerAuthenticatedPayload(
                        PLAYER_ID,
                        "HarriOcho",
                        EVENT_TIMESTAMP
                );

        assertRoundTrip(
                ProtocolMessageType.PLAYER_AUTHENTICATED,
                payload,
                PlayerAuthenticatedPayload.class
        );
    }

    @Test
    void roundTripsPlayerServerReadyPayload() {
        PlayerServerReadyPayload payload =
                new PlayerServerReadyPayload(
                        PLAYER_ID,
                        "lobby-1",
                        EVENT_TIMESTAMP + 25
                );

        assertRoundTrip(
                ProtocolMessageType.PLAYER_SERVER_READY,
                payload,
                PlayerServerReadyPayload.class
        );
    }

    private <T> void assertRoundTrip(
            String messageType,
            T payload,
            Class<T> payloadType
    ) {
        ProtocolEnvelope<T> original =
                ProtocolEnvelope.create(messageType, payload);

        byte[] encoded = codec.encode(original);
        ProtocolEnvelope<T> decoded =
                codec.decode(encoded, payloadType);

        assertEquals(original, decoded);
    }
}