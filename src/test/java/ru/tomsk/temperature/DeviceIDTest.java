package ru.tomsk.temperature;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeviceIDTest {
    @Test
    public void testMinValue() {
        DeviceID deviceID = new DeviceID(100_001);
        assertEquals(1, deviceID.getTrmID());
        assertEquals(1, deviceID.getUspdID());
    }
    @Test
    public void testMinTrmUspd() {
        DeviceID deviceID = new DeviceID(1, 1);
        assertEquals(100_001, deviceID.getValue());
        assertEquals(1, deviceID.getTrmID());
        assertEquals(1, deviceID.getUspdID());
    }

    @Test
    public void testMaxValue() {
        DeviceID deviceID = new DeviceID(555_599_999);
        assertEquals(5555, deviceID.getTrmID());
        assertEquals(99_999, deviceID.getUspdID());
    }

    @Test
    public void testMaxTrmUspd() {
        DeviceID deviceID = new DeviceID(7777, 99_999);
        assertEquals(777_799_999, deviceID.getValue());
        assertEquals(7777, deviceID.getTrmID());
        assertEquals(99_999, deviceID.getUspdID());
    }

    @Test
    public void testValue() {
        DeviceID deviceID = new DeviceID(12_304_567);
        assertEquals(123, deviceID.getTrmID());
        assertEquals(4567, deviceID.getUspdID());
    }

    @Test
    public void testTrmUspd() {
        DeviceID deviceID = new DeviceID(123, 4567);
        assertEquals(12_304_567, deviceID.getValue());
        assertEquals(123, deviceID.getTrmID());
        assertEquals(4567, deviceID.getUspdID());
    }

    @Test
    public void testInvalidValue() {
        try {
            new DeviceID(DeviceID.MIN_VALUE - 1);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}