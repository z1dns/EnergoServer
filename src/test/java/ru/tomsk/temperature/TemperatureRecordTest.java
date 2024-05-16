package ru.tomsk.temperature;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TemperatureRecordTest {
    @ParameterizedTest
    @ValueSource(ints = { -1, 1045 })
    void testInvalidTrmID(int deviceID) {
        try {
            new TemperatureRecord(deviceID, Instant.now(), 10.f, 10.f);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("Invalid deviceID"));
        }
    }

    @ParameterizedTest
    @ValueSource(floats = {TemperatureRecord.MIN_TEMPERATURE - 0.5f, TemperatureRecord.MAX_TEMPERATURE + 0.5f})
    void testInvalidSurfaceTemperature(float surfaceTemperature) {
        try {
            new TemperatureRecord(DeviceID.MIN_VALUE, Instant.now(), surfaceTemperature, 1000.f);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("Invalid surface temperature"));
        }
    }

    @ParameterizedTest
    @ValueSource(floats = {TemperatureRecord.MIN_TEMPERATURE - 1, TemperatureRecord.MAX_TEMPERATURE + 1})
    void testInvalidAirTemperature(float airTemperature) {
        try {
            new TemperatureRecord(DeviceID.MIN_VALUE, Instant.now(), 10.f, airTemperature);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("Invalid air temperature"));
        }
    }

    @Test
    void testInvalidLowerTimestamp() {
        try {
            new TemperatureRecord(DeviceID.MIN_VALUE, TemperatureRecord.MIN_DATETIME.minusSeconds(10), 10.f, -10.f);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("Invalid datetime"));
        }
    }

    @Test
    void testInvalidUpperTimestamp() {
        try {
            new TemperatureRecord(DeviceID.MIN_VALUE, TemperatureRecord.MAX_DATETIME.plusSeconds(1), 10.f, -10.f);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("Invalid datetime"));
        }
    }

    @Test
    void testLowerBoundRecord() {
        try {
            new TemperatureRecord(DeviceID.MIN_VALUE,
                    TemperatureRecord.MIN_DATETIME,
                    TemperatureRecord.MIN_TEMPERATURE,
                    TemperatureRecord.MIN_TEMPERATURE);
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    void testUpperBoundRecord() {
        try {
            new TemperatureRecord(Integer.MAX_VALUE,
                    TemperatureRecord.MAX_DATETIME,
                    TemperatureRecord.MAX_TEMPERATURE,
                    TemperatureRecord.MAX_TEMPERATURE);
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    void testValidRecord() {
        try {
            new TemperatureRecord(DeviceID.MIN_VALUE,
                    Instant.now(),
                    75,
                    25);
        } catch (IllegalArgumentException e) {
            fail();
        }
    }
}