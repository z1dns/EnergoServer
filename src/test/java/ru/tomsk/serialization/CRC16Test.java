package ru.tomsk.serialization;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.nio.charset.StandardCharsets;

public class CRC16Test {
    @Test
    public void emptyCRC() {
        short expected = CRC16.CRC_INIT;
        short actual = CRC16.calculate("".getBytes(StandardCharsets.UTF_8));
        assertEquals(expected, actual);
    }

    @Test
    public void validCRC()  {
        short expected = (short) 44775;
        short actual = CRC16.calculate("123456789".getBytes(StandardCharsets.UTF_8));
        assertEquals(expected, actual);
    }

    @Test
    public void invalidCRC()  {
        short expected = 123;
        short actual = CRC16.calculate("123456789".getBytes(StandardCharsets.UTF_8));
        assertNotEquals(expected, actual);
    }
}

