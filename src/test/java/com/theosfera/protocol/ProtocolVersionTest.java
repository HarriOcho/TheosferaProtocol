package com.theosfera.protocol;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProtocolVersionTest {

    @Test
    void currentProtocolVersionIsOne() {
        assertEquals(1, ProtocolVersion.CURRENT);
    }
}