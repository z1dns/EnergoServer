package ru.tomsk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tomsk.connection.Server;
import ru.tomsk.database.DBConnectionProvider;
import ru.tomsk.database.TemperatureRecordService;

public class App 
{
    private static final Logger LOGGER = LogManager.getLogger(App.class);
    public static void main( String[] args ) {
        try {
            LOGGER.info("Program started");
            var settings = Settings.getInstance();
            LOGGER.info("Program settings: {}", settings);
            var connectionProvider = new DBConnectionProvider(settings.getDatabaseURL(), settings.getDatabaseUsername(), settings.getDatabasePassword());
            TemperatureRecordService temperatureRecordService = new TemperatureRecordService(connectionProvider);
            Thread.setDefaultUncaughtExceptionHandler((t, e) -> LOGGER.warn("Error in thread: {}, cause: {}", t, e.getMessage()));
            var server = new Server(settings.getServerPort(), temperatureRecordService);
            var serverThread = new Thread(server, "ServerThread");
            serverThread.start();
            serverThread.join();
            LOGGER.info("Program stopped");
        } catch (RuntimeException | InterruptedException e) {
            LOGGER.error("Program crashed: {}", e.getMessage());
        }
    }
}
