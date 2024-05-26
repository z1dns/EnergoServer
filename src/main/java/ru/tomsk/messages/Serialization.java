package ru.tomsk.messages;

/**
 * Интерфейс для сериализуемых/десериализуемых типов
 */

public interface Serialization {
    byte [] serialize();
    void deserialize(byte [] bytes) throws IllegalArgumentException;
}
