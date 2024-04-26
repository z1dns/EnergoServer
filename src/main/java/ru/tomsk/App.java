package ru.tomsk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.tomsk.connection.Server;

import java.io.IOException;

public class App 
{

    private static final Logger LOGGER = LogManager.getLogger(App.class);
    public static void main( String[] args ) {
        try {
            LOGGER.info("Program started");
            var server = new Server();
            server.start(4444);
        } catch (RuntimeException | IOException e) {
            LOGGER.error("Program running error: {}", e.getMessage());
        }
    }
}
