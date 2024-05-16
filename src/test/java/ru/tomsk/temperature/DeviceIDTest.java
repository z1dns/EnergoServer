package ru.tomsk.temperature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class DeviceIDTest {
    @Test
    public void testMinValue() {
        DeviceID deviceID = new DeviceID(100_001);
        Assertions.assertEquals(1, deviceID.getTrmID());
        Assertions.assertEquals(1, deviceID.getUspdID());
    }
    @Test
    public void testMinTrmUspd() {
        DeviceID deviceID = new DeviceID(1, 1);
        Assertions.assertEquals(100_001, deviceID.getValue());
        Assertions.assertEquals(1, deviceID.getTrmID());
        Assertions.assertEquals(1, deviceID.getUspdID());
    }

    @Test
    public void testMaxValue() {
        DeviceID deviceID = new DeviceID(555_599_999);
        Assertions.assertEquals(5555, deviceID.getTrmID());
        Assertions.assertEquals(99_999, deviceID.getUspdID());
    }

    @Test
    public void testMaxTrmUspd() {
        DeviceID deviceID = new DeviceID(7777, 99_999);
        Assertions.assertEquals(777_799_999, deviceID.getValue());
        Assertions.assertEquals(7777, deviceID.getTrmID());
        Assertions.assertEquals(99_999, deviceID.getUspdID());
    }

    @Test
    public void testValue() {
        DeviceID deviceID = new DeviceID(12_304_567);
        Assertions.assertEquals(123, deviceID.getTrmID());
        Assertions.assertEquals(4567, deviceID.getUspdID());
    }

    @Test
    public void testTrmUspd() {
        DeviceID deviceID = new DeviceID(123, 4567);
        Assertions.assertEquals(12_304_567, deviceID.getValue());
        Assertions.assertEquals(123, deviceID.getTrmID());
        Assertions.assertEquals(4567, deviceID.getUspdID());
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