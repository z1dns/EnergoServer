package ru.tomsk.temperature;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import ru.tomsk.messages.UspdMessage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TemperatureRecordConverterTest {
    @ParameterizedTest
    @ValueSource(ints = {1, 100, UspdMessage.TRM_COUNT})
    public void testConvert(int recordCount) {
        List<TemperatureRecord> expected = new ArrayList<>();
        for (int recordIdx = 0; recordIdx < recordCount; ++recordIdx) {
            var deviceID = new DeviceID(recordIdx + 1000, 54321);
            expected.add(new TemperatureRecord(deviceID.getValue(),
                    Instant.now().plusSeconds(recordIdx),
                    TemperatureRecord.MAX_TEMPERATURE - 0.1f * recordIdx,
                    TemperatureRecord.MIN_TEMPERATURE + 0.1f * recordIdx));
        }
        var message = TemperatureRecordConverter.toMessage(expected);
        var bytes = message.serialize();
        var uspdMessage = new UspdMessage();
        uspdMessage.deserialize(bytes);
        var actual = TemperatureRecordConverter.fromMessage(uspdMessage);
        assertEquals(expected, actual);
    }
}