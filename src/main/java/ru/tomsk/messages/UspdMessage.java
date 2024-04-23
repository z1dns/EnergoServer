package ru.tomsk.messages;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class UspdMessage extends Message {
    public static final int TRM_COUNT = 250;
    private static final int DATA_LENGTH = 2 + TRM_COUNT * TrmMessage.length();
    public short idField = 0; //unsigned
    public TrmMessage[] trmMessageArray = new TrmMessage[TRM_COUNT];

    public static int length() {
        return DATA_LENGTH + CRC_LENGTH;
    }

    public byte[] getTrmData(int trmIdx) {
        if (bytes.length != length()) {
            throw new IllegalArgumentException(String.format("Incorrect count of bytes(%d) for getTrmData", bytes.length));
        }
        if (trmIdx < 0 || trmIdx >= TRM_COUNT) {
            throw new IllegalArgumentException(String.format("Incorrect trmIdx(%d) for getTrmData", bytes.length));
        }
        int from = 2 + trmIdx * TrmMessage.length();
        int to = from + TrmMessage.length();
        return Arrays.copyOfRange(bytes, from, to);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UspdMessage that = (UspdMessage) o;
        return idField == that.idField && crcField == that.crcField && Arrays.equals(trmMessageArray, that.trmMessageArray);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(idField, crcField);
        result = 31 * result + Arrays.hashCode(trmMessageArray);
        return result;
    }

    @Override
    public String toString() {
        return "UspdMessage{" +
                "correctCRC=" + correctCRC +
                ", idField=" + idField +
                ", TrmPacketArray=\n\t" + arrayToString() +
                ", \ncrcField=" + crcField +
                '}';
    }

    @Override
    public byte[] serialize() {
        bytes = new byte[length()];
        var buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(idField);
        for (var trmMessage : trmMessageArray) {
            buffer.put(trmMessage.serialize());
        }
        crcField =  CRC16.calculate(bytes, 0, DATA_LENGTH);
        buffer.putShort(crcField);
        return bytes;
    }

    @Override
    public void deserialize(byte[] bytes) throws IllegalArgumentException {
        if (bytes.length != length()) {
            throw new IllegalArgumentException(String.format("Incorrect count of bytes(%d) for deserialize %s", bytes.length, this.getClass()));
        }
        this.bytes = bytes;
        var buffer = ByteBuffer.wrap(this.bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        idField = buffer.getShort();
        for (int trmIdx = 0; trmIdx < TRM_COUNT; ++trmIdx) {
            var trmMessage = new TrmMessage();
            byte[] trmData = new byte[TrmMessage.length()];
            buffer.get(trmData);
            trmMessage.deserialize(trmData);
            trmMessageArray[trmIdx] = trmMessage;
        }
        crcField = buffer.getShort();
        correctCRC = crcField == CRC16.calculate(this.bytes, 0, DATA_LENGTH);
    }

    private String arrayToString() {
        return Arrays.stream(trmMessageArray).map(TrmMessage::toString).collect(Collectors.joining("\n\t"));
    }
}
