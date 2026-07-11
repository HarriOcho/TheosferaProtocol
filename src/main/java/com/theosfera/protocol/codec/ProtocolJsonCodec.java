package com.theosfera.protocol.codec;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.theosfera.protocol.message.ProtocolEnvelope;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class ProtocolJsonCodec {

    public static final int MAX_MESSAGE_BYTES = 32 * 1024;

    private final Gson gson;

    public ProtocolJsonCodec() {
        this(new Gson());
    }

    public ProtocolJsonCodec(Gson gson) {
        this.gson = Objects.requireNonNull(gson, "gson cannot be null");
    }

    public byte[] encode(ProtocolEnvelope<?> envelope) {
        if (envelope == null) {
            throw new ProtocolCodecException("Envelope cannot be null");
        }

        final byte[] encoded;

        try {
            encoded = gson.toJson(envelope).getBytes(StandardCharsets.UTF_8);
        } catch (RuntimeException exception) {
            throw new ProtocolCodecException(
                    "Could not encode protocol envelope",
                    exception
            );
        }

        validateSize(encoded.length);
        return encoded;
    }

    public <T> ProtocolEnvelope<T> decode(
            byte[] encoded,
            Class<T> payloadType
    ) {
        Objects.requireNonNull(
                payloadType,
                "payloadType cannot be null"
        );

        if (encoded == null) {
            throw new ProtocolCodecException("Encoded message cannot be null");
        }

        if (encoded.length == 0) {
            throw new ProtocolCodecException("Encoded message cannot be empty");
        }

        validateSize(encoded.length);

        String json = decodeUtf8(encoded);
        Type envelopeType = TypeToken.getParameterized(
                ProtocolEnvelope.class,
                payloadType
        ).getType();

        try {
            ProtocolEnvelope<T> envelope = gson.fromJson(json, envelopeType);

            if (envelope == null) {
                throw new ProtocolCodecException(
                        "Decoded envelope cannot be null"
                );
            }

            return envelope;
        } catch (ProtocolCodecException exception) {
            throw exception;
        } catch (JsonParseException | IllegalArgumentException exception) {
            throw new ProtocolCodecException(
                    "Could not decode protocol envelope",
                    exception
            );
        }
    }

    private String decodeUtf8(byte[] encoded) {
        try {
            return StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(encoded))
                    .toString();
        } catch (CharacterCodingException exception) {
            throw new ProtocolCodecException(
                    "Encoded message is not valid UTF-8",
                    exception
            );
        }
    }

    private void validateSize(int size) {
        if (size > MAX_MESSAGE_BYTES) {
            throw new ProtocolCodecException(
                    "Protocol message exceeds the maximum size of "
                            + MAX_MESSAGE_BYTES
                            + " bytes"
            );
        }
    }
}