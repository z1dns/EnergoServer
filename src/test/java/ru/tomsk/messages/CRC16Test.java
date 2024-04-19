package ru.tomsk.messages;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    public void crcInRange() {
        short expected = (short) 44775;
        short actual = CRC16.calculate("999123456789111".getBytes(StandardCharsets.UTF_8), 3, 9);
        assertEquals(expected, actual);
    }

    @Test
    public void crcInRangeFromBegin() {
        short expected = (short) 44775;
        short actual = CRC16.calculate("123456789111".getBytes(StandardCharsets.UTF_8), 0, 9);
        assertEquals(expected, actual);
    }

    @Test
    public void crcInRangeTillEnd() {
        short expected = (short) 44775;
        short actual = CRC16.calculate("999123456789".getBytes(StandardCharsets.UTF_8), 3, 9);
        assertEquals(expected, actual);
    }

    @Test
    public void crcInInvalidRange() {
        try {
            short result = CRC16.calculate("999123456789".getBytes(StandardCharsets.UTF_8), 3, 10);
            fail();
        } catch (IndexOutOfBoundsException e) {
            assertTrue(true);
        }
    }
}

