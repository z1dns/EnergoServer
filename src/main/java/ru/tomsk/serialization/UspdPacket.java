package ru.tomsk.serialization;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class UspdPacket implements Serialization {
    static final int TRM_COUNT = 250;
    private static final int DATA_LENGTH = 2 + TRM_COUNT * TrmPacket.length();
    private static final int CRC_LENGTH = 2;
    private boolean correctCRC = false;
    short idField = 0; //unsigned
    TrmPacket [] TrmPacketArray = new TrmPacket[TRM_COUNT];
    short crcField = 0; //unsigned

    public static int length() {
        return DATA_LENGTH + CRC_LENGTH;
    }

    public boolean isCRCCorrect() {
        return correctCRC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UspdPacket that = (UspdPacket) o;
        return idField == that.idField && crcField == that.crcField && Arrays.equals(TrmPacketArray, that.TrmPacketArray);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(idField, crcField);
        result = 31 * result + Arrays.hashCode(TrmPacketArray);
        return result;
    }

    @Override
    public String toString() {
        return "UspdPacket{" +
                "correctCRC=" + correctCRC +
                ", idField=" + idField +
                ", TrmPacketArray=\n\t" + arrayToString() +
                ", \ncrcField=" + crcField +
                '}';
    }

    @Override
    public byte[] serialize() {
        byte [] data = new byte[length()];
        var buffer = ByteBuffer.wrap(data);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(idField);
        for (var trmPacket : TrmPacketArray) {
            buffer.put(trmPacket.serialize());
        }
        crcField =  CRC16.calculate(data, 0, DATA_LENGTH);
        buffer.putShort(crcField);
        return data;
    }

    @Override
    public void deserialize(byte[] bytes) throws IllegalArgumentException {
        if (bytes.length != length()) {
            throw new IllegalArgumentException(String.format("Incorrect count of bytes(%d) for deserialize %s", bytes.length, this.getClass()));
        }
        var buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        idField = buffer.getShort();
        for (int trmIdx = 0; trmIdx < TRM_COUNT; ++trmIdx) {
            var trmPacket = new TrmPacket();
            byte[] trmData = new byte[TrmPacket.length()];
            buffer.get(trmData);
            trmPacket.deserialize(trmData);
            TrmPacketArray[trmIdx] = trmPacket;
        }
        crcField = buffer.getShort();
        correctCRC = crcField == CRC16.calculate(bytes, 0, DATA_LENGTH);
    }

    private String arrayToString() {
        return Arrays.stream(TrmPacketArray).map(TrmPacket::toString).collect(Collectors.joining("\n\t"));
    }
}
