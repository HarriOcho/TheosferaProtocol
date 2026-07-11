package com.theosfera.protocol.codec;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.theosfera.protocol.message.ProtocolEnvelope;
import com.theosfera.protocol.message.ProtocolMessageRegistry;

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
        this.gson = Objects.requireNonNull(
                gson,
                "gson cannot be null"
        );
    }

    public byte[] encode(ProtocolEnvelope<?> envelope) {
        if (envelope == null) {
            throw new ProtocolCodecException(
                    "Envelope cannot be null"
            );
        }

        final byte[] encoded;

        try {
            encoded = gson.toJson(envelope)
                    .getBytes(StandardCharsets.UTF_8);
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

        String json = validateAndDecode(encoded);

        return decodeJson(json, payloadType);
    }

    public ProtocolEnvelope<?> decodeRegistered(
            byte[] encoded
    ) {
        String json = validateAndDecode(encoded);
        String messageType = readMessageType(json);

        Class<?> payloadType =
                ProtocolMessageRegistry.payloadType(
                        messageType
                ).orElseThrow(() ->
                        new ProtocolCodecException(
                                "Unknown protocol message type: "
                                        + messageType
                        )
                );

        return decodeJson(json, payloadType);
    }

    private String validateAndDecode(byte[] encoded) {
        if (encoded == null) {
            throw new ProtocolCodecException(
                    "Encoded message cannot be null"
            );
        }

        if (encoded.length == 0) {
            throw new ProtocolCodecException(
                    "Encoded message cannot be empty"
            );
        }

        validateSize(encoded.length);
        return decodeUtf8(encoded);
    }

    private String readMessageType(String json) {
        final JsonElement parsed;

        try {
            parsed = gson.fromJson(json, JsonElement.class);
        } catch (JsonParseException exception) {
            throw new ProtocolCodecException(
                    "Could not inspect protocol envelope",
                    exception
            );
        }

        if (parsed == null || !parsed.isJsonObject()) {
            throw new ProtocolCodecException(
                    "Protocol envelope must be a JSON object"
            );
        }

        JsonObject object = parsed.getAsJsonObject();
        JsonElement typeElement = object.get("type");

        if (typeElement == null
                || !typeElement.isJsonPrimitive()
                || !typeElement.getAsJsonPrimitive().isString()) {
            throw new ProtocolCodecException(
                    "Protocol envelope type must be a string"
            );
        }

        return typeElement.getAsString();
    }

    private <T> ProtocolEnvelope<T> decodeJson(
            String json,
            Class<T> payloadType
    ) {
        Type envelopeType = TypeToken.getParameterized(
                ProtocolEnvelope.class,
                payloadType
        ).getType();

        try {
            ProtocolEnvelope<T> envelope =
                    gson.fromJson(json, envelopeType);

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
                    .onUnmappableCharacter(
                            CodingErrorAction.REPORT
                    )
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