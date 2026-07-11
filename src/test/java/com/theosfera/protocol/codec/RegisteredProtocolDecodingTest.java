package com.theosfera.protocol.codec;

import com.theosfera.protocol.message.ProtocolEnvelope;
import com.theosfera.protocol.message.ProtocolMessageType;
import com.theosfera.protocol.message.payload.BackendHelloAckPayload;
import com.theosfera.protocol.message.payload.BackendHelloPayload;
import com.theosfera.protocol.message.payload.BackendType;
import com.theosfera.protocol.message.payload.PingPayload;
import com.theosfera.protocol.message.payload.PlayerAuthenticatedPayload;
import com.theosfera.protocol.message.payload.PlayerServerReadyPayload;
import com.theosfera.protocol.message.payload.PongPayload;
import com.theosfera.protocol.message.payload.TransferRequestPayload;
import com.theosfera.protocol.message.payload.TransferResultPayload;
import com.theosfera.protocol.message.payload.TransferResultStatus;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RegisteredProtocolDecodingTest {

    private static final UUID PLAYER_ID =
            UUID.fromString(
                    "417e98b4-74a1-467e-b453-a15be3af8996"
            );

    private static final long EVENT_TIMESTAMP =
            1_750_000_000_000L;

    private final ProtocolJsonCodec codec =
            new ProtocolJsonCodec();

    @Test
    void decodesEveryRegisteredPayloadType() {
        List<ProtocolEnvelope<?>> envelopes = List.of(
                ProtocolEnvelope.create(
                        ProtocolMessageType.BACKEND_HELLO,
                        new BackendHelloPayload(
                                "lobby-1",
                                BackendType.LOBBY
                        )
                ),
                ProtocolEnvelope.create(
                        ProtocolMessageType.BACKEND_HELLO_ACK,
                        new BackendHelloAckPayload(
                                true,
                                "Backend registered"
                        )
                ),
                ProtocolEnvelope.create(
                        ProtocolMessageType.PLAYER_AUTHENTICATED,
                        new PlayerAuthenticatedPayload(
                                PLAYER_ID,
                                "HarriOcho",
                                EVENT_TIMESTAMP
                        )
                ),
                ProtocolEnvelope.create(
                        ProtocolMessageType.PLAYER_SERVER_READY,
                        new PlayerServerReadyPayload(
                                PLAYER_ID,
                                "lobby-1",
                                EVENT_TIMESTAMP
                        )
                ),
                ProtocolEnvelope.create(
                        ProtocolMessageType.TRANSFER_REQUEST,
                        new TransferRequestPayload(
                                PLAYER_ID,
                                BackendType.SKYBLOCK
                        )
                ),
                ProtocolEnvelope.create(
                        ProtocolMessageType.TRANSFER_RESULT,
                        new TransferResultPayload(
                                PLAYER_ID,
                                TransferResultStatus.SUCCESS,
                                "Transfer completed"
                        )
                ),
                ProtocolEnvelope.create(
                        ProtocolMessageType.PING,
                        new PingPayload(EVENT_TIMESTAMP)
                ),
                ProtocolEnvelope.create(
                        ProtocolMessageType.PONG,
                        new PongPayload(
                                EVENT_TIMESTAMP,
                                EVENT_TIMESTAMP + 25
                        )
                )
        );

        for (ProtocolEnvelope<?> original : envelopes) {
            byte[] encoded = codec.encode(original);
            ProtocolEnvelope<?> decoded =
                    codec.decodeRegistered(encoded);

            assertEquals(original, decoded);
            assertEquals(
                    original.payload().getClass(),
                    decoded.payload().getClass()
            );
        }
    }

    @Test
    void resolvesPayloadUsingTheRegisteredType() {
        ProtocolEnvelope<?> original =
                ProtocolEnvelope.create(
                        ProtocolMessageType.PING,
                        new PingPayload(EVENT_TIMESTAMP)
                );

        ProtocolEnvelope<?> decoded =
                codec.decodeRegistered(
                        codec.encode(original)
                );

        assertInstanceOf(
                PingPayload.class,
                decoded.payload()
        );
    }

    @Test
    void rejectsUnknownMessageType() {
        byte[] encoded = """
                {
                  "version": 1,
                  "type": "FUTURE_MESSAGE",
                  "requestId": "417e98b4-74a1-467e-b453-a15be3af8996",
                  "timestamp": 1750000000000,
                  "payload": {}
                }
                """.getBytes(StandardCharsets.UTF_8);

        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decodeRegistered(encoded)
        );
    }

    @Test
    void rejectsMissingMessageType() {
        byte[] encoded = """
                {
                  "version": 1,
                  "requestId": "417e98b4-74a1-467e-b453-a15be3af8996",
                  "timestamp": 1750000000000,
                  "payload": {}
                }
                """.getBytes(StandardCharsets.UTF_8);

        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decodeRegistered(encoded)
        );
    }

    @Test
    void rejectsNonStringMessageType() {
        byte[] encoded = """
                {
                  "version": 1,
                  "type": 123,
                  "requestId": "417e98b4-74a1-467e-b453-a15be3af8996",
                  "timestamp": 1750000000000,
                  "payload": {}
                }
                """.getBytes(StandardCharsets.UTF_8);

        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decodeRegistered(encoded)
        );
    }

    @Test
    void rejectsNonObjectEnvelope() {
        byte[] encoded = "[]".getBytes(
                StandardCharsets.UTF_8
        );

        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decodeRegistered(encoded)
        );
    }

    @Test
    void rejectsMalformedJson() {
        byte[] encoded = "{invalid-json".getBytes(
                StandardCharsets.UTF_8
        );

        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decodeRegistered(encoded)
        );
    }

    @Test
    void rejectsMalformedUtf8() {
        byte[] encoded = {
                (byte) 0xC3,
                (byte) 0x28
        };

        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decodeRegistered(encoded)
        );
    }

    @Test
    void rejectsNullMessage() {
        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decodeRegistered(null)
        );
    }

    @Test
    void rejectsEmptyMessage() {
        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decodeRegistered(new byte[0])
        );
    }

    @Test
    void rejectsOversizedMessage() {
        byte[] oversized = new byte[
                ProtocolJsonCodec.MAX_MESSAGE_BYTES + 1
                ];

        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decodeRegistered(oversized)
        );
    }
}