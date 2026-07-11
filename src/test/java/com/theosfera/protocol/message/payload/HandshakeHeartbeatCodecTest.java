package com.theosfera.protocol.message.payload;

import com.theosfera.protocol.codec.ProtocolJsonCodec;
import com.theosfera.protocol.message.ProtocolEnvelope;
import com.theosfera.protocol.message.ProtocolMessageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HandshakeHeartbeatCodecTest {

    private static final long PING_SENT_AT =
            1_750_000_000_000L;

    private final ProtocolJsonCodec codec =
            new ProtocolJsonCodec();

    @Test
    void roundTripsBackendHelloPayload() {
        BackendHelloPayload payload = new BackendHelloPayload(
                "lobby-1",
                BackendType.LOBBY
        );

        assertRoundTrip(
                ProtocolMessageType.BACKEND_HELLO,
                payload,
                BackendHelloPayload.class
        );
    }

    @Test
    void roundTripsAcceptedBackendHelloAckPayload() {
        BackendHelloAckPayload payload =
                new BackendHelloAckPayload(
                        true,
                        "Backend registered"
                );

        assertRoundTrip(
                ProtocolMessageType.BACKEND_HELLO_ACK,
                payload,
                BackendHelloAckPayload.class
        );
    }

    @Test
    void roundTripsRejectedBackendHelloAckPayload() {
        BackendHelloAckPayload payload =
                new BackendHelloAckPayload(
                        false,
                        "Unauthorized backend"
                );

        assertRoundTrip(
                ProtocolMessageType.BACKEND_HELLO_ACK,
                payload,
                BackendHelloAckPayload.class
        );
    }

    @Test
    void roundTripsPingPayload() {
        PingPayload payload = new PingPayload(PING_SENT_AT);

        assertRoundTrip(
                ProtocolMessageType.PING,
                payload,
                PingPayload.class
        );
    }

    @Test
    void roundTripsPongPayload() {
        PongPayload payload = new PongPayload(
                PING_SENT_AT,
                PING_SENT_AT + 25
        );

        assertRoundTrip(
                ProtocolMessageType.PONG,
                payload,
                PongPayload.class
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