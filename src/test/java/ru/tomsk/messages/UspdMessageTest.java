package ru.tomsk.messages;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class UspdMessageTest {
    @Test
    public void deserializeNotEnoughBytes() {
        var uspdMessage = new UspdMessage();
        var bytes = new byte[UspdMessage.length() - 1];
        Arrays.fill(bytes, (byte) 0);
        try {
            uspdMessage.deserialize(bytes);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void serializeAndDeserialize() {
        var expected = new UspdMessage();
        expected.idField = 100;
        for (int trmIdx = 0; trmIdx < UspdMessage.TRM_COUNT; ++trmIdx) {
            var trmMessage = new TrmMessage();
            if (trmIdx % 2 == 0) {
                trmMessage.idField = 0;
                trmMessage.timestampField = 0;
                trmMessage.surfaceTemperatureField = 0;
                trmMessage.airTemperatureField = 0;
            } else {
                trmMessage.idField = (short) trmIdx;
                trmMessage.timestampField = 16 * trmIdx;
                trmMessage.surfaceTemperatureField = (short) (trmIdx * 3);
                trmMessage.airTemperatureField = (short) (trmIdx * 2);
            }
            expected.trmMessageArray[trmIdx] = trmMessage;
        }
        byte [] bytes = expected.serialize();
        var actual = new UspdMessage();
        actual.deserialize(bytes);
        assertEquals(expected, actual);
        assertTrue(actual.isCRCCorrect());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 100, UspdMessage.TRM_COUNT - 1 })
    void testGetTrmData(int idx) {
        var uspdMessage = new UspdMessage();
        uspdMessage.idField = 10;
        for (int trmIdx = 0; trmIdx < UspdMessage.TRM_COUNT; ++trmIdx) {
            var trmMessage = new TrmMessage();
            trmMessage.idField = 0;
            trmMessage.timestampField = 0;
            trmMessage.surfaceTemperatureField = 0;
            trmMessage.airTemperatureField = 0;
            uspdMessage.trmMessageArray[trmIdx] = trmMessage;
        }
        var expected = new TrmMessage();
        expected.idField = 0x1234;
        expected.timestampField = 0x12345678;
        expected.surfaceTemperatureField = 0x3ABC;
        expected.airTemperatureField = 0x1EFD;
        uspdMessage.trmMessageArray[idx] = expected;
        var bytes = uspdMessage.serialize();
        var message = new UspdMessage();
        message.deserialize(bytes);
        var trmData = message.getTrmData(idx);
        var actual = new TrmMessage();
        actual.deserialize(trmData);
        assertEquals(expected, actual);
    }
}
