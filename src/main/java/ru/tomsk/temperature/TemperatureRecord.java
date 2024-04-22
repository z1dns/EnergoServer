package ru.tomsk.temperature;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record TemperatureRecord(int deviceID,
                                Instant timestamp,
                                float surfaceTemperature,
                                float airTemperature) {
    public static final float MIN_TEMPERATURE = -125.f;
    public static final float MAX_TEMPERATURE = 125.f;
    public static final Instant MIN_DATETIME = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant();
    public static final Instant MAX_DATETIME = ZonedDateTime.of(2100, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant();

    public TemperatureRecord {
        if (deviceID < DeviceID.MIN_VALUE) {
            throw new IllegalArgumentException(String.format("Invalid deviceID: %d", deviceID));
        }
        if (timestamp.isBefore(MIN_DATETIME) || timestamp.isAfter(MAX_DATETIME)) {
            throw new IllegalArgumentException(String.format("Invalid datetime: %s", timestamp.atZone(ZoneId.of("UTC"))));
        }
        if (surfaceTemperature < MIN_TEMPERATURE || surfaceTemperature > MAX_TEMPERATURE) {
            throw new IllegalArgumentException(String.format("Invalid surface temperature: %f", surfaceTemperature));
        }
        if (airTemperature < MIN_TEMPERATURE || airTemperature > MAX_TEMPERATURE) {
            throw new IllegalArgumentException(String.format("Invalid air temperature: %f", airTemperature));
        }
    }
}
