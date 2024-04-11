package ru.tomsk.serialization;

public class CRC16 {
    private static final short CRC16_POLY = (short) 0x8005;
    static final short CRC_INIT = (short) 0xFFFF;
    private static short culCalcCRC(byte crcData, short crcReg) {
        for (byte i = 0; i < 8; ++i) {
            if ((((crcReg & 0x8000) >> 8) ^ (crcData & (short) 0x80)) != 0) {
                crcReg = (short) ((crcReg << 1) ^ CRC16_POLY);
            } else {
                crcReg = (short) (crcReg << 1);
            }
            crcData <<= 1;
        }
        return crcReg;
    }

    public static short calculate(byte[] data) {
        short crc = CRC_INIT;
        for (byte b : data) {
            crc = culCalcCRC(b, crc);
        }
        return crc;
    }

}