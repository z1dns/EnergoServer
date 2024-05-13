package ru.tomsk.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tomsk.temperature.TemperatureRecord;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TemperatureRecordService {
    private static final Logger LOGGER = LogManager.getLogger();
    private final DBConnectionProvider connectionProvider;

    public TemperatureRecordService(DBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public void addAll(List<TemperatureRecord> temperatureRecords) {
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO TemperatureRecords (trmId, time_stamp, surfaceTemperature, airTemperature) VALUES (?, ?, ?, ?)")) {
            for (var record : temperatureRecords) {
                preparedStatement.setInt(1, record.deviceID());
                preparedStatement.setTimestamp(2, Timestamp.from(record.timestamp()));
                preparedStatement.setFloat(3, record.surfaceTemperature());
                preparedStatement.setFloat(4, record.airTemperature());
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (SQLException e) {
            for (Throwable t : e) {
                LOGGER.warn("Insert records to DB error: {}", t.getMessage());
            }
        }
    }
}
