package ru.tomsk.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Serialization {
    void serialize(OutputStream outputStream) throws IOException;
    void deserialize(InputStream inputStream) throws IOException;
}
