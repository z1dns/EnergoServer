package ru.tomsk.messages;

public interface Serialization {
    byte [] serialize();
    void deserialize(byte [] bytes) throws IllegalArgumentException;
}
