package com.doktorthe2nd.nyax.types.packets;

import com.doktorthe2nd.nyax.types.MapContainer;

public class ErrorPacket extends PacketBase {
    private String message = "unknown cause";

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return true;
    }

    public ErrorPacket() { markNotSerializable(); }
    public ErrorPacket(String message) {
        markNotSerializable();
        this.message = message;
    }
    public ErrorPacket(MapContainer data) {
        markNotSerializable();
        deserialize(data);
    }

    @Override
    public boolean deserialize(MapContainer data) {
        if (!super.deserialize(data)) return false;
        this.message = data.getStringOr("localizedMessage", data.getStringOr("message", "unknown cause"));
        return true;
    }
}
