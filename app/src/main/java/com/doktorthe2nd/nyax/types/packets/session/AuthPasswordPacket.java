package com.doktorthe2nd.nyax.types.packets.session;

import com.doktorthe2nd.nyax.net.OpcodeTable;
import com.doktorthe2nd.nyax.types.MapContainer;
import com.doktorthe2nd.nyax.types.packets.SendablePacket;

import org.jetbrains.annotations.Contract;

public class AuthPasswordPacket extends SendablePacket {
    private final String trackId;
    private final String password;

    public AuthPasswordPacket(String trackId, String password) {
        this.trackId = trackId;
        this.password = password;
    }

    @Override
    public int getOpcode() {
        return OpcodeTable.authLoginCheckPassword;
    }

    @Override
    public MapContainer serialize() {
        return super.serialize()
                .putc("trackId", trackId)
                .putc("password", password);
    }

    /** You should deserialize packet as LoginPacket. This always returns false. */
    @Override
    public boolean deserialize(MapContainer data) {
        return false;
    }
}
