package ru.tomsk.serialization;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TrmPacketTest {
    @Test
    public void serializeEmpty() {
        var trmPacket = new TrmPacket();
        try (var bos = new ByteArrayOutputStream()){
            trmPacket.serialize(bos);
            var expected = new byte[12];
            Arrays.fill(expected, (byte) 0);
            assertArrayEquals(expected, bos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void serialize() {
        var trmPacket = new TrmPacket();
        trmPacket.idField = 0x3412;
        trmPacket.timestampField = 0x1A2B3C4D;
        trmPacket.surfaceTemperatureField = (short) 0xABCD;
        trmPacket.airTemperatureField = 0x7890;
        try (var bos = new ByteArrayOutputStream()){
            trmPacket.serialize(bos);
            var expected = new byte[] {0x12, 0x34, 0x4D, 0x3C, 0x2B, 0x1A,
                    (byte) 0xCD, (byte) 0xAB, (byte) 0x90, (byte) 0x78, -43 ,-45};
            var actual = bos.toByteArray();
            assertArrayEquals(expected, actual);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void serializeMaxValues() {
        var trmPacket = new TrmPacket();
        trmPacket.idField = (short) 0xFFFF;
        trmPacket.timestampField = 0xFFFFFFFF;
        trmPacket.surfaceTemperatureField = (short) 0xFFFF;
        trmPacket.airTemperatureField = (short) 0xFFFF;
        try (var bos = new ByteArrayOutputStream()){
            trmPacket.serialize(bos);
            var expected = new byte[] {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                    (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x21 , (byte) 0x82};
            var actual = bos.toByteArray();
            assertArrayEquals(expected, actual);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deserializeEmpty() {
        var trmPacket = new TrmPacket();
        var bytes = new byte[TrmPacket.length()];
        Arrays.fill(bytes, (byte) 0);
        try (var bis = new ByteArrayInputStream(bytes)){
            trmPacket.deserialize(bis);
            assertTrue(trmPacket.isEmpty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void deserializeNotEnoughBytes() {
        var trmPacket = new TrmPacket();
        var bytes = new byte[TrmPacket.length() - 1];
        Arrays.fill(bytes, (byte) 0);
        try (var bis = new ByteArrayInputStream(bytes)){
            trmPacket.deserialize(bis);
            assertTrue(false);
        } catch (IOException e) {
            assertTrue(true);
        }
    }

    @Test
    public void deserialize() {
        var trmPacket = new TrmPacket();
        var bytes = new byte[] {0x12, 0x34, 0x4D, 0x3C, 0x2B, 0x1A,
                (byte) 0xCD, (byte) 0xAB, (byte) 0x90, (byte) 0x78, -43 ,-45};
        try (var bis = new ByteArrayInputStream(bytes)){
            trmPacket.deserialize(bis);
            assertAll("Verify TRMPacket properties",
                    () -> assertEquals(0x3412, trmPacket.idField),
                    () -> assertEquals(0x1A2B3C4D, trmPacket.timestampField),
                    () -> assertEquals((short) 0xABCD, trmPacket.surfaceTemperatureField),
                    () -> assertEquals((short) 0x7890, trmPacket.airTemperatureField),
                    () -> assertTrue(trmPacket.isCRCCorrect()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void serializeAndDeserialize() {
        var expected = new TrmPacket();
        expected.idField = 123;
        expected.timestampField = 9876543;
        expected.surfaceTemperatureField = 500;
        expected.airTemperatureField = 909;
        byte [] bytes;
        try (var bos = new ByteArrayOutputStream()){
            expected.serialize(bos);
            bytes = bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var actual = new TrmPacket();
        try (var bis = new ByteArrayInputStream(bytes)){
            actual.deserialize(bis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(expected, actual);
    }
}
