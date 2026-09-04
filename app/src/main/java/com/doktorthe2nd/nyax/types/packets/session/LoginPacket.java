package com.doktorthe2nd.nyax.types.packets.session;

import com.doktorthe2nd.nyax.net.OpcodeTable;
import com.doktorthe2nd.nyax.types.MapContainer;
import com.doktorthe2nd.nyax.types.packets.SendablePacket;

public class LoginPacket extends SendablePacket {
    private String token;

    public String getToken() {
        return token;
    }

    @Override
    public int getOpcode() {
        return OpcodeTable.login;
    }

    @Override
    public boolean deserialize(MapContainer data) {
        if (!super.deserialize(data)) return false;
        token = data.getc("tokenAttrs").getc("LOGIN").getString("token");
        return allNotNull(token);
    }

    @Override
    public MapContainer serialize() {
        return super.serialize();
    }
}
