package com.theosfera.protocol.codec;

import com.theosfera.protocol.message.ProtocolEnvelope;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProtocolJsonCodecTest {

    private final ProtocolJsonCodec codec = new ProtocolJsonCodec();

    @Test
    void encodesAndDecodesEnvelope() {
        ProtocolEnvelope<TestPayload> original =
                ProtocolEnvelope.create(
                        "PING",
                        new TestPayload("¡Hola, Theosfera!")
                );

        byte[] encoded = codec.encode(original);
        ProtocolEnvelope<TestPayload> decoded =
                codec.decode(encoded, TestPayload.class);

        assertEquals(original, decoded);
    }

    @Test
    void usesUtf8Encoding() {
        ProtocolEnvelope<TestPayload> envelope =
                ProtocolEnvelope.create(
                        "PING",
                        new TestPayload("áéíóú ñ 世界")
                );

        byte[] encoded = codec.encode(envelope);
        ProtocolEnvelope<TestPayload> decoded =
                codec.decode(encoded, TestPayload.class);

        assertEquals("áéíóú ñ 世界", decoded.payload().value());
        assertArrayEquals(
                new String(encoded, StandardCharsets.UTF_8)
                        .getBytes(StandardCharsets.UTF_8),
                encoded
        );
    }

    @Test
    void rejectsNullEnvelope() {
        assertThrows(
                ProtocolCodecException.class,
                () -> codec.encode(null)
        );
    }

    @Test
    void rejectsNullEncodedMessage() {
        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decode(null, TestPayload.class)
        );
    }

    @Test
    void rejectsEmptyEncodedMessage() {
        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decode(new byte[0], TestPayload.class)
        );
    }

    @Test
    void rejectsMalformedJson() {
        byte[] malformed = "{invalid-json"
                .getBytes(StandardCharsets.UTF_8);

        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decode(malformed, TestPayload.class)
        );
    }

    @Test
    void rejectsJsonNull() {
        byte[] jsonNull = "null".getBytes(StandardCharsets.UTF_8);

        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decode(jsonNull, TestPayload.class)
        );
    }

    @Test
    void rejectsMalformedUtf8() {
        byte[] malformedUtf8 = {
                (byte) 0xC3,
                (byte) 0x28
        };

        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decode(malformedUtf8, TestPayload.class)
        );
    }

    @Test
    void rejectsOversizedEncodedMessage() {
        byte[] oversized = new byte[
                ProtocolJsonCodec.MAX_MESSAGE_BYTES + 1
                ];
        Arrays.fill(oversized, (byte) 'a');

        assertThrows(
                ProtocolCodecException.class,
                () -> codec.decode(oversized, TestPayload.class)
        );
    }

    @Test
    void rejectsOversizedEnvelopeDuringEncoding() {
        String largeValue = "a".repeat(
                ProtocolJsonCodec.MAX_MESSAGE_BYTES
        );

        ProtocolEnvelope<TestPayload> envelope =
                ProtocolEnvelope.create(
                        "PING",
                        new TestPayload(largeValue)
                );

        assertThrows(
                ProtocolCodecException.class,
                () -> codec.encode(envelope)
        );
    }

    private record TestPayload(String value) {
    }
}