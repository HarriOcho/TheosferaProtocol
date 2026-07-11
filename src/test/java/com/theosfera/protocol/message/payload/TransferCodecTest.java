package com.theosfera.protocol.message.payload;

import com.theosfera.protocol.codec.ProtocolJsonCodec;
import com.theosfera.protocol.message.ProtocolEnvelope;
import com.theosfera.protocol.message.ProtocolMessageType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransferCodecTest {

    private static final UUID PLAYER_ID =
            UUID.fromString(
                    "417e98b4-74a1-467e-b453-a15be3af8996"
            );

    private final ProtocolJsonCodec codec =
            new ProtocolJsonCodec();

    @Test
    void roundTripsTransferRequestPayload() {
        TransferRequestPayload payload =
                new TransferRequestPayload(
                        PLAYER_ID,
                        BackendType.SKYBLOCK
                );

        assertRoundTrip(
                ProtocolMessageType.TRANSFER_REQUEST,
                payload,
                TransferRequestPayload.class
        );
    }

    @Test
    void roundTripsSuccessfulTransferResultPayload() {
        TransferResultPayload payload =
                new TransferResultPayload(
                        PLAYER_ID,
                        TransferResultStatus.SUCCESS,
                        "Transfer completed"
                );

        assertRoundTrip(
                ProtocolMessageType.TRANSFER_RESULT,
                payload,
                TransferResultPayload.class
        );
    }

    @Test
    void roundTripsFailedTransferResultPayload() {
        TransferResultPayload payload =
                new TransferResultPayload(
                        PLAYER_ID,
                        TransferResultStatus.FAILED,
                        "Destination backend unavailable"
                );

        assertRoundTrip(
                ProtocolMessageType.TRANSFER_RESULT,
                payload,
                TransferResultPayload.class
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