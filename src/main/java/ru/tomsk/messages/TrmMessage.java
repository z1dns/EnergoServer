package ru.tomsk.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Objects;

public class TrmMessage extends Message {
    private static final int DATA_LENGTH = 10;
    public short idField = 0; //unsigned
    public int timestampField = 0; //unsigned
    public short surfaceTemperatureField = 0;
    public short airTemperatureField = 0;

    public boolean isEmpty() {
        return idField == 0 && timestampField == 0 && surfaceTemperatureField == 0 && airTemperatureField == 0;
    }

    public static int length() {
        return DATA_LENGTH + CRC_LENGTH;
    }

    @Override
    public String toString() {
        return "TrmMessage{" +
                "idField=" + idField +
                ", timestampField=" + timestampField +
                ", surfaceTemperatureField=" + surfaceTemperatureField +
                ", airTemperatureField=" + airTemperatureField +
                ", crcField=" + crcField +
                ", correctCRC=" + correctCRC +
                ", empty=" + isEmpty() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrmMessage trmMessage = (TrmMessage) o;
        return idField == trmMessage.idField &&
                timestampField == trmMessage.timestampField &&
                surfaceTemperatureField == trmMessage.surfaceTemperatureField &&
                airTemperatureField == trmMessage.airTemperatureField;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idField, timestampField, surfaceTemperatureField, airTemperatureField);
    }

    @Override
    public byte [] serialize() {
        bytes = new byte[length()];
        var buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(idField);
        buffer.putInt(timestampField);
        buffer.putShort(surfaceTemperatureField);
        buffer.putShort(airTemperatureField);
        crcField =  isEmpty() ? 0 : CRC16.calculate(bytes, 0, DATA_LENGTH);
        buffer.putShort(crcField);
        return bytes;
    }

    @Override
    public void deserialize(byte [] bytes) throws IllegalArgumentException {
        if (bytes.length != length()) {
            throw new IllegalArgumentException(String.format("Incorrect count of bytes(%d) for deserialize %s", bytes.length, this.getClass()));
        }
        this.bytes = bytes;
        var buffer = ByteBuffer.wrap(this.bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        idField = buffer.getShort();
        timestampField = buffer.getInt();
        surfaceTemperatureField = buffer.getShort();
        airTemperatureField = buffer.getShort();
        crcField = buffer.getShort();
        correctCRC = crcField == CRC16.calculate(this.bytes, 0, DATA_LENGTH);
    }
}