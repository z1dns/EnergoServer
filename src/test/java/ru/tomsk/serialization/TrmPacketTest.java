package ru.tomsk.serialization;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TrmPacketTest {
    @Test
    public void serializeEmpty() {
        var expected = new byte[TrmPacket.length()];
        Arrays.fill(expected, (byte) 0);
        var trmPacket = new TrmPacket();
        byte [] actual = trmPacket.serialize();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void serialize() {
        var expected = new byte[] {0x12, 0x34, 0x4D, 0x3C, 0x2B, 0x1A,
                (byte) 0xCD, (byte) 0xAB, (byte) 0x90, (byte) 0x78, -43 ,-45};
        var trmPacket = new TrmPacket();
        trmPacket.idField = 0x3412;
        trmPacket.timestampField = 0x1A2B3C4D;
        trmPacket.surfaceTemperatureField = (short) 0xABCD;
        trmPacket.airTemperatureField = 0x7890;
        var actual = trmPacket.serialize();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void serializeMaxValues() {
        var expected = new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x21 , (byte) 0x82};
        var trmPacket = new TrmPacket();
        trmPacket.idField = (short) 0xFFFF;
        trmPacket.timestampField = 0xFFFFFFFF;
        trmPacket.surfaceTemperatureField = (short) 0xFFFF;
        trmPacket.airTemperatureField = (short) 0xFFFF;
        var actual = trmPacket.serialize();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void deserializeEmpty() {
        var bytes = new byte[TrmPacket.length()];
        Arrays.fill(bytes, (byte) 0);
        var trmPacket = new TrmPacket();
        trmPacket.deserialize(bytes);
        assertTrue(trmPacket.isEmpty());
    }

    @Test
    public void deserializeNotEnoughBytes() {
        var trmPacket = new TrmPacket();
        var bytes = new byte[TrmPacket.length() - 1];
        Arrays.fill(bytes, (byte) 0);
        try {
            trmPacket.deserialize(bytes);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void deserialize() {
        var bytes = new byte[] {0x12, 0x34, 0x4D, 0x3C, 0x2B, 0x1A,
                (byte) 0xCD, (byte) 0xAB, (byte) 0x90, (byte) 0x78, -43 ,-45};
        var trmPacket = new TrmPacket();
        trmPacket.deserialize(bytes);
        assertAll("Verify TRMPacket properties",
                () -> assertEquals(0x3412, trmPacket.idField),
                () -> assertEquals(0x1A2B3C4D, trmPacket.timestampField),
                () -> assertEquals((short) 0xABCD, trmPacket.surfaceTemperatureField),
                () -> assertEquals((short) 0x7890, trmPacket.airTemperatureField),
                () -> assertTrue(trmPacket.isCRCCorrect()));
    }

    @Test
    public void serializeAndDeserialize() {
        var expected = new TrmPacket();
        expected.idField = 123;
        expected.timestampField = 9876543;
        expected.surfaceTemperatureField = 500;
        expected.airTemperatureField = 909;
        byte [] bytes = expected.serialize();
        var actual = new TrmPacket();
        actual.deserialize(bytes);
        assertEquals(expected, actual);
        assertTrue(actual.isCRCCorrect());
    }
}
