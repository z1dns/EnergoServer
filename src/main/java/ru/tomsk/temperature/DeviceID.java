package ru.tomsk.temperature;

import java.util.Objects;

public class DeviceID {
    private static final int TRM_ID_SHIFT = 100_000;
    public static final int MIN_VALUE = TRM_ID_SHIFT + 1;
    private final int value;

    public DeviceID(int value) {
        if (value < MIN_VALUE) {
            throw new IllegalArgumentException(String.format("Invalid DeviceID value: %d", value));
        }
        this.value = value;
    }

    public DeviceID(int trmID, int uspdID) {
        if (uspdID >= TRM_ID_SHIFT) {
            throw new IllegalArgumentException(String.format("Invalid uspdID: %d", uspdID));
        }
        this.value = uspdID + trmID * TRM_ID_SHIFT;
    }

    public int getValue() {
        return value;
    }

    public int getTrmID() {
        return value / TRM_ID_SHIFT;
    }

    public int getUspdID() {
        return value % TRM_ID_SHIFT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceID deviceID = (DeviceID) o;
        return value == deviceID.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "DeviceID{" +
                "value=" + value +
                '}';
    }
}
