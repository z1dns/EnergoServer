package ru.tomsk.messages;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TrmMessageTest {
    @Test
    public void serializeEmpty() {
        var expected = new byte[TrmMessage.length()];
        Arrays.fill(expected, (byte) 0);
        var trmMessage = new TrmMessage();
        byte [] actual = trmMessage.serialize();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void serialize() {
        var expected = new byte[] {0x12, 0x34, 0x4D, 0x3C, 0x2B, 0x1A,
                (byte) 0xCD, (byte) 0xAB, (byte) 0x90, (byte) 0x78, -43 ,-45};
        var trmMessage = new TrmMessage();
        trmMessage.idField = 0x3412;
        trmMessage.timestampField = 0x1A2B3C4D;
        trmMessage.surfaceTemperatureField = (short) 0xABCD;
        trmMessage.airTemperatureField = 0x7890;
        var actual = trmMessage.serialize();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void serializeMaxValues() {
        var expected = new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x21 , (byte) 0x82};
        var trmMessage = new TrmMessage();
        trmMessage.idField = (short) 0xFFFF;
        trmMessage.timestampField = 0xFFFFFFFF;
        trmMessage.surfaceTemperatureField = (short) 0xFFFF;
        trmMessage.airTemperatureField = (short) 0xFFFF;
        var actual = trmMessage.serialize();
        assertArrayEquals(expected, actual);
    }

    @Test
    public void deserializeEmpty() {
        var bytes = new byte[TrmMessage.length()];
        Arrays.fill(bytes, (byte) 0);
        var trmMessage = new TrmMessage();
        trmMessage.deserialize(bytes);
        assertTrue(trmMessage.isEmpty());
    }

    @Test
    public void deserializeNotEnoughBytes() {
        var trmMessage = new TrmMessage();
        var bytes = new byte[TrmMessage.length() - 1];
        Arrays.fill(bytes, (byte) 0);
        try {
            trmMessage.deserialize(bytes);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void deserialize() {
        var bytes = new byte[] {0x12, 0x34, 0x4D, 0x3C, 0x2B, 0x1A,
                (byte) 0xCD, (byte) 0xAB, (byte) 0x90, (byte) 0x78, -43 ,-45};
        var trmMessage = new TrmMessage();
        trmMessage.deserialize(bytes);
        assertAll("Verify TrmMessage properties",
                () -> assertEquals(0x3412, trmMessage.idField),
                () -> assertEquals(0x1A2B3C4D, trmMessage.timestampField),
                () -> assertEquals((short) 0xABCD, trmMessage.surfaceTemperatureField),
                () -> assertEquals((short) 0x7890, trmMessage.airTemperatureField),
                () -> assertTrue(trmMessage.isCRCCorrect()));
    }

    @Test
    public void serializeAndDeserialize() {
        var expected = new TrmMessage();
        expected.idField = 123;
        expected.timestampField = 9876543;
        expected.surfaceTemperatureField = 500;
        expected.airTemperatureField = 909;
        byte [] bytes = expected.serialize();
        var actual = new TrmMessage();
        actual.deserialize(bytes);
        assertEquals(expected, actual);
        assertTrue(actual.isCRCCorrect());
    }
}
