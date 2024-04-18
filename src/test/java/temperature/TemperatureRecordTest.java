package temperature;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.tomsk.temperature.TemperatureRecord;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class TemperatureRecordTest {
    @ParameterizedTest
    @ValueSource(ints = { -1, 0 })
    void testInvalidTrmID(int trmID) {
        try {
            var record = new TemperatureRecord(trmID, Instant.now(), 10.f, 10.f);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @ParameterizedTest
    @ValueSource(floats = {TemperatureRecord.MIN_TEMPERATURE - 0.5f, TemperatureRecord.MAX_TEMPERATURE + 0.5f})
    void testInvalidSurfaceTemperature(float surfaceTemperature) {
        try {
            var record = new TemperatureRecord(1, Instant.now(), surfaceTemperature, 1000.f);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @ParameterizedTest
    @ValueSource(floats = {TemperatureRecord.MIN_TEMPERATURE - 1, TemperatureRecord.MAX_TEMPERATURE + 1})
    void testInvalidAirTemperature(float airTemperature) {
        try {
            var record = new TemperatureRecord(1, Instant.now(), 10.f, airTemperature);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    void testInvalidLowerTimestamp() {
        try {
            var record = new TemperatureRecord(1, TemperatureRecord.MIN_DATETIME.minusSeconds(10), 10.f, -10.f);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    void testInvalidUpperTimestamp() {
        try {
            var record = new TemperatureRecord(1, TemperatureRecord.MAX_DATETIME.plusSeconds(1), 10.f, -10.f);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    void testLowerBoundRecord() {
        try {
            var record = new TemperatureRecord(1,
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
            var record = new TemperatureRecord(Integer.MAX_VALUE,
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
            var record = new TemperatureRecord(12345,
                    Instant.now(),
                    75,
                    25);
        } catch (IllegalArgumentException e) {
            fail();
        }
    }
}
