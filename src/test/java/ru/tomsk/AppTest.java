package ru.tomsk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;
import ru.tomsk.connection.Client;
import ru.tomsk.connection.Server;
import ru.tomsk.database.DBConnectionProvider;
import ru.tomsk.database.TemperatureRecordService;
import ru.tomsk.messages.UspdMessage;
import ru.tomsk.temperature.DeviceID;
import ru.tomsk.temperature.TemperatureRecord;
import ru.tomsk.temperature.TemperatureRecordConverter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AppTest {
    private static TemperatureRecordService temperatureRecordService;

    @BeforeAll
    static void setupDatabase() {
        DBConnectionProvider dbConnectionProvider = new DBConnectionProvider("jdbc:derby:energoserver;create=true;",
                "user",
                "password");
        temperatureRecordService = new TemperatureRecordService(dbConnectionProvider);
        dropTableIfExist(dbConnectionProvider);
        createTable(dbConnectionProvider);
    }

    static void createTable(DBConnectionProvider dbConnectionProvider) {
        try (Connection connection = dbConnectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE TemperatureRecords
                    (recordId BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
                    trmId INT NOT NULL,
                    time_stamp TIMESTAMP NOT NULL,
                    surfaceTemperature FLOAT NOT NULL\s
                    CHECK (surfaceTemperature <= 125.0 AND surfaceTemperature>= -125.0),
                    airTemperature FLOAT NOT NULL
                    CHECK (airTemperature <= 125.0 AND airTemperature >= -125.0),
                    UNIQUE(trmId, time_stamp))""");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static void dropTableIfExist(DBConnectionProvider dbConnectionProvider) {
        try (Connection connection = dbConnectionProvider.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE TemperatureRecords");
        } catch (SQLException e) {
             //ignore
        }
    }

    @Test
    public void test() {
        final int port = 8080;
        final int uspdCount = 5;
        final int trmCount = 10;
        var server = new Server(port, temperatureRecordService);
        var serverThread = new Thread(server, "ServerThread");
        serverThread.start();
        List<TemperatureRecord> expectedTemperatureRecords = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (int uspdIdx = 0; uspdIdx < uspdCount; ++uspdIdx) {
            for (int trmIdx = 0; trmIdx < trmCount; ++trmIdx) {
                var temperatureRecords = generateRecords(uspdIdx, trmIdx);
                expectedTemperatureRecords.addAll(temperatureRecords);
                var message = TemperatureRecordConverter.toMessage(temperatureRecords);
                var client = new Client("localhost", port, message);
                executor.execute(client);
            }
        }
        executor.shutdown();
        try {
            if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
                TimeUnit.SECONDS.sleep(3); //wait for server finish its job
                serverThread.interrupt();
                serverThread.join();
                var actualTemperatureRecords = temperatureRecordService.getAll();
                var comparator = Comparator.comparing(TemperatureRecord::deviceID).thenComparing(TemperatureRecord::timestamp);
                expectedTemperatureRecords.sort(comparator);
                actualTemperatureRecords.sort(comparator);
                Assertions.assertEquals(expectedTemperatureRecords, actualTemperatureRecords);
            } else {
                fail();
            }
        } catch (InterruptedException e) {
            fail();
        }
    }

    private static float round(float value, int scale) {
        return (float) (Math.round(value * Math.pow(10, scale)) / Math.pow(10, scale));
    }

    private static List<TemperatureRecord> generateRecords(final int uspdIdx, final int trmIdx) {
        List<TemperatureRecord> temperatureRecords = new ArrayList<>();
        final Random random  = new Random();
        for (int recordIdx = 0; recordIdx < UspdMessage.TRM_COUNT; ++recordIdx) {
            var deviceID = new DeviceID(trmIdx + uspdIdx + 100, (uspdIdx + 1) * 5);
            temperatureRecords.add(new TemperatureRecord(deviceID.getValue(),
                    Instant.now().plusSeconds(recordIdx).truncatedTo(ChronoUnit.SECONDS),
                    round(100 * random.nextFloat(), 2),
                    round(50 * random.nextFloat(), 2)));
        }
        return temperatureRecords;
    }
}