package ru.tomsk.serialization;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;

public class TrmPacket implements Serialization {
    private static final int DATA_LENGTH = 10;
    private static final int CRC_LENGTH = 2;
    private boolean correctCRC = false;
    short idField = 0; //unsigned
    int timestampField = 0; //unsigned
    short surfaceTemperatureField = 0; //signed
    short airTemperatureField = 0; //unsigned
    short crcField = 0; //unsigned

    public boolean isEmpty() {
        return idField == 0 && timestampField == 0 && surfaceTemperatureField == 0 && airTemperatureField == 0;
    }

    public boolean isCRCCorrect() {
        return correctCRC;
    }

    public static int length() {
        return DATA_LENGTH + CRC_LENGTH;
    }

    @Override
    public String toString() {
        return "TrmPacket{" +
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
        TrmPacket trmPacket = (TrmPacket) o;
        return idField == trmPacket.idField &&
                timestampField == trmPacket.timestampField &&
                surfaceTemperatureField == trmPacket.surfaceTemperatureField &&
                airTemperatureField == trmPacket.airTemperatureField;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idField, timestampField, surfaceTemperatureField, airTemperatureField);
    }

    @Override
    public void serialize(OutputStream outputStream) throws IOException {
        byte [] data = new byte[DATA_LENGTH];
        var buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(idField);
        buffer.putInt(timestampField);
        buffer.putShort(surfaceTemperatureField);
        buffer.putShort(airTemperatureField);
        crcField =  isEmpty() ? 0 : CRC16.calculate(data);
        byte [] crcData = new byte[CRC_LENGTH];
        var crcBuffer = ByteBuffer.wrap(crcData);
        crcBuffer.order(ByteOrder.LITTLE_ENDIAN);
        crcBuffer.putShort(crcField);

        outputStream.write(data);
        outputStream.write(crcData);
    }

    @Override
    public void deserialize(InputStream inputStream) throws IOException {
        byte [] bytes = inputStream.readAllBytes();
        if (bytes.length != DATA_LENGTH + CRC_LENGTH) {
            throw new IOException(String.format("Incorrect count of bytes(%d) for %s", bytes.length, this.getClass()));
        }
        byte [] data = Arrays.copyOfRange(bytes, 0, DATA_LENGTH);
        var buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        idField = buffer.getShort();
        timestampField = buffer.getInt();
        surfaceTemperatureField = buffer.getShort();
        airTemperatureField = buffer.getShort();

        byte [] crcData = Arrays.copyOfRange(bytes, DATA_LENGTH, DATA_LENGTH + CRC_LENGTH);
        var crcBuffer = ByteBuffer.wrap(crcData);
        crcBuffer.order(ByteOrder.LITTLE_ENDIAN);
        crcField = crcBuffer.getShort();
        correctCRC = crcField == CRC16.calculate(data);
    }
}