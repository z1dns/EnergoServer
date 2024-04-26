package ru.tomsk.connection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tomsk.messages.UspdMessage;
import ru.tomsk.temperature.TemperatureRecordConverter;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;

public class Server {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_MESSAGE_LENGTH = 4096;

    public void start(int port) throws IOException {
        try (var server = new ServerSocket(port)) {
            LOGGER.info("Server started at port:{}", port);
            while (true) {
                try (var client = server.accept();
                     var in = client.getInputStream()) {
                    LOGGER.debug("Client connected to server: {}", client);
                    var bytes = in.readNBytes(MAX_MESSAGE_LENGTH);
                    LOGGER.trace("Server received {} bytes from {}, data:{}", bytes.length, client, Arrays.toString(bytes));
                    processMessage(bytes);
                } catch (IOException exception) {
                    LOGGER.warn("Client error: {}", exception.getMessage());
                }

            }
        }
    }

    private static void processMessage(byte[] bytes) {
        if (bytes.length == UspdMessage.length()) {
            LOGGER.debug("UspdMessage received");
            processUspdMessage(bytes);
        } else {
            LOGGER.warn("Unknown message received:{}", Arrays.toString(bytes));
        }
    }

    private static void processUspdMessage(byte[] bytes) {
        try {
            var uspdMessage = new UspdMessage();
            uspdMessage.deserialize(bytes);
            var temperatureRecords = TemperatureRecordConverter.fromMessage(uspdMessage);
            LOGGER.trace("Received temperature records: {}", temperatureRecords);
        } catch (RuntimeException e) {
            LOGGER.warn("Error while processing UspdMessage: {}", e.getMessage());
        }
    }
}
