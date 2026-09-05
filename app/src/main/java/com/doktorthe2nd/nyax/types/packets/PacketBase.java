package com.doktorthe2nd.nyax.types.packets;

import com.doktorthe2nd.nyax.types.MapContainer;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * Packet with no data and no opcode.
 * Contains basic functions.
 */
public class PacketBase {
    private boolean notSerializable = false;

    /** Mark this packet not serializable. */
    public void markNotSerializable() {
        notSerializable = true;
    }

    /** Returns empty {@link MapContainer} (because no data present).
     * <p> This function should be overridden to add more data to it. <p>
     * Example: <pre>{@code
     * @Override
     * public MapContainer serialize() {
     *     return super.serialize()
     *         .putc("key", value)
     *         .putc("someOtherData", someOtherValue);
     * }
     * }</pre> */
    public MapContainer serialize() {
        if (notSerializable) throw new RuntimeException("Serialize called on packet marked not serializable");
        return new MapContainer();
    }

    /** Returns true if data is not null. <p> This function should be overridden to read data.
     * Return false if needed value is not found. Use {@link #allNotNull(Object...)} for convenience.
     * <p> Used when trying to deserialize incoming packet.
     * If called twice, second time is not guaranteed to properly return false on error,
     * instead data that was not found will be carried from first call.<p>
     * Example: <pre>{@code
     * @Override
     * public boolean deserialize(MapContainer data) {
     *     if (!super.deserialize(data)) return false;
     *     String myData = data.getString("key");
     *     Float myOtherData = data.getFloat("otherKey");
     *     return allNotNull(myData, myOtherData);
     * }
     * }</pre> */
    public boolean deserialize(MapContainer data) {
        return data != null;
    }
    public boolean deserialize(Map<Object, Object> data) {
        return deserialize(MapContainer.of(data));
    }

    /** Returns true if all given objects are not null, false otherwise. */
    public static boolean allNotNull(Object... objects) {
        return Arrays.stream(objects).noneMatch(Objects::isNull);
    }
    /** Returns true if all given objects are not null, false otherwise. */
    public static boolean allNotNull(Object object) {
        return object != null;
    }
    /** Returns true if all given objects are not null, false otherwise. */
    public static boolean allNotNull(Object object1, Object object2) {
        return object1 != null && object2 != null;
    }
    /** Returns true if all given objects are not null, false otherwise. */
    public static boolean allNotNull(Object object1, Object object2, Object object3) {
        return object1 != null && object2 != null && object3 != null;
    }
}
