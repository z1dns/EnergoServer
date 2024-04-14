package ru.tomsk.serialization;

public interface Serialization {
    byte [] serialize();
    void deserialize(byte [] bytes) throws IllegalArgumentException;
}
