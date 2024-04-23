package ru.tomsk.temperature;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tomsk.messages.TrmMessage;
import ru.tomsk.messages.UspdMessage;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TemperatureRecordConverter {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final float TEMPERATURE_RATE = 100.f;
    public static List<TemperatureRecord> fromMessage(UspdMessage uspdMessage) {
        List<TemperatureRecord> records = new ArrayList<>();
        if (!uspdMessage.isCRCCorrect()) {
            LOGGER.debug("UspdMessage has invalid CRC {}", Arrays.toString(uspdMessage.getBytes()));
            return records;
        }

        Instant baseTimestamp = TemperatureRecord.MIN_DATETIME;
        for (int trmIdx = 0; trmIdx < UspdMessage.TRM_COUNT; ++trmIdx) {
            TrmMessage trmMessage = uspdMessage.trmMessageArray[trmIdx];
            if (trmMessage.isEmpty()) {
                continue;
            }
            if (!trmMessage.isCRCCorrect()) {
                LOGGER.debug("TrmMessage at index:{} has invalid CRC {}", trmIdx, Arrays.toString(uspdMessage.getTrmData(trmIdx)));
                continue;
            }

            try {
                var deviceID = new DeviceID(Short.toUnsignedInt(trmMessage.idField), Short.toUnsignedInt(uspdMessage.idField));
                var timeStamp = baseTimestamp.plusSeconds(Integer.toUnsignedLong(trmMessage.timestampField));
                var surfaceTemperature = ((float) trmMessage.surfaceTemperatureField) / TEMPERATURE_RATE;
                var airTemperature = ((float) trmMessage.airTemperatureField) / TEMPERATURE_RATE;
                var record = new TemperatureRecord(deviceID.getValue(), timeStamp, surfaceTemperature, airTemperature);
                records.add(record);
            } catch (IllegalArgumentException exception) {
                LOGGER.info("Parse invalid TemperatureRecord:{}, raw data:{}", exception.getMessage(), trmMessage);
            }
        }
        return records;
    }

    public static UspdMessage toMessage(List<TemperatureRecord> temperatureRecords) {
        if (temperatureRecords.size() > UspdMessage.TRM_COUNT) {
            throw new IllegalArgumentException(String.format("Too many (%d) records for UspdMessage", temperatureRecords.size()));
        }

        UspdMessage uspdMessage = new UspdMessage();
        Arrays.fill(uspdMessage.trmMessageArray, new TrmMessage());
        int trmIdx = 0;
        Instant baseTimestamp = TemperatureRecord.MIN_DATETIME;
        for (var record : temperatureRecords) {
            DeviceID deviceID = new DeviceID(record.deviceID());
            if (uspdMessage.idField == 0) {
                uspdMessage.idField = (short) deviceID.getUspdID();
            }
            var trmMessage = new TrmMessage();
            trmMessage.idField = (short) deviceID.getTrmID();
            trmMessage.timestampField = (int) baseTimestamp.until(record.timestamp(), ChronoUnit.SECONDS);
            trmMessage.surfaceTemperatureField = (short) Math.round(record.surfaceTemperature() * TEMPERATURE_RATE);
            trmMessage.airTemperatureField = (short) Math.round(record.airTemperature() * TEMPERATURE_RATE);
            uspdMessage.trmMessageArray[trmIdx] = trmMessage;
            ++trmIdx;
        }

        return uspdMessage;
    }
}
