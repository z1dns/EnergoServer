package ru.tomsk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Settings INSTANCE = new Settings();
    private int serverPort = 8030;

    public static Settings getInstance() {
        return INSTANCE;
    }

    public int getServerPort() {
        return serverPort;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "serverPort=" + serverPort +
                '}';
    }

    private Settings() {
        var propertiesPath = getClass().getClassLoader().getResource("").getPath() + "app.properties";
        try (var fileReader = new FileReader(propertiesPath)){
            var properties = new Properties();
            properties.load(fileReader);
            serverPort = Integer.parseInt(properties.getProperty("server.port", Integer.toString(serverPort)));
        } catch (IOException e) {
            LOGGER.warn("Error while reading properties in path {}", propertiesPath);
        } catch (NumberFormatException e) {
            LOGGER.warn("Error while parsing properties file: {}", e.getMessage());
        }
    }
}
