package ru.tomsk.connection;

import ru.tomsk.messages.UspdMessage;

import java.io.IOException;
import java.net.Socket;

public class Client implements Runnable {
    private final String host;
    private final int port;
    private final UspdMessage message;

    public Client(String host, int port, UspdMessage message) {
        this.host = host;
        this.port = port;
        this.message = message;
    }


    @Override
    public void run() {
        try (var socket = new Socket(host, port);
            var out = socket.getOutputStream()) {
            out.write(message.serialize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
