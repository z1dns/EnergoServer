package ru.tomsk.messages;

public abstract class Message implements Serialization {
    protected static final int CRC_LENGTH = 2;
    protected short crcField = 0; //unsigned
    protected boolean correctCRC = false;
    public boolean isCRCCorrect() {
        return correctCRC;
    }
}
