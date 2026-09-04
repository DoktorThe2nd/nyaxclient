package com.doktorthe2nd.nyax.types.packets.session;

import com.doktorthe2nd.nyax.Consts;
import com.doktorthe2nd.nyax.net.OpcodeTable;
import com.doktorthe2nd.nyax.types.MapContainer;
import com.doktorthe2nd.nyax.types.packets.SendablePacket;

public class AuthRequestPacket extends SendablePacket {
    private final String phone;
    private String authToken;

    public String getAuthToken() {
        return authToken;
    }
    public String getPhone() {
        return phone;
    }

    public AuthRequestPacket(String phone) {
        this.phone = phone;
    }

    @Override
    public int getOpcode() {
        return OpcodeTable.authRequest;
    }

    @Override
    public MapContainer serialize() {
        return super.serialize()
                .putc("phone", phone)
                .putc("type", "START_AUTH")
                .putc("mode", Consts.getFingerprint());
    }

    @Override
    public boolean deserialize(MapContainer data) {
        if (!super.deserialize(data)) return false;
        authToken = data.getString("token");
        return allNotNull(authToken);
    }
}
