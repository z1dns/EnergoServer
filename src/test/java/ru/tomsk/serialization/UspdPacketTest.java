package ru.tomsk.serialization;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class UspdPacketTest {
    @Test
    public void deserializeNotEnoughBytes() {
        var uspdPacket = new UspdPacket();
        var bytes = new byte[UspdPacket.length() - 1];
        Arrays.fill(bytes, (byte) 0);
        try {
            uspdPacket.deserialize(bytes);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void serializeAndDeserialize() {
        var expected = new UspdPacket();
        expected.idField = 100;
        for (int trmIdx = 0; trmIdx < UspdPacket.TRM_COUNT; ++trmIdx) {
            var trmPacket = new TrmPacket();
            if (trmIdx % 2 == 0) {
                trmPacket.idField = 0;
                trmPacket.timestampField = 0;
                trmPacket.surfaceTemperatureField = 0;
                trmPacket.airTemperatureField = 0;
            } else {
                trmPacket.idField = (short) trmIdx;
                trmPacket.timestampField = 16 * trmIdx;
                trmPacket.surfaceTemperatureField = (short) (trmIdx * 3);
                trmPacket.airTemperatureField = (short) (trmIdx * 2);
            }
            expected.TrmPacketArray[trmIdx] = trmPacket;
        }
        byte [] bytes = expected.serialize();
        var actual = new UspdPacket();
        actual.deserialize(bytes);
        assertEquals(expected, actual);
        assertTrue(actual.isCRCCorrect());
    }
}
