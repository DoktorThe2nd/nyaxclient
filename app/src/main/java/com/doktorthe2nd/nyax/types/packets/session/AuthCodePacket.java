package com.doktorthe2nd.nyax.types.packets.session;

import com.doktorthe2nd.nyax.net.OpcodeTable;
import com.doktorthe2nd.nyax.types.MapContainer;
import com.doktorthe2nd.nyax.types.packets.SendablePacket;

public class AuthCodePacket extends SendablePacket {
    private final String code;
    private final String authToken;

    private String trackId;
    public String getTrackId() {
        return trackId;
    }

    public AuthCodePacket(String authToken, String code) {
        this.authToken = authToken;
        this.code = code;
    }

    @Override
    public int getOpcode() {
        return OpcodeTable.auth;
    }

    @Override
    public MapContainer serialize() {
        return super.serialize()
                .putc("token", authToken)
                .putc("verifyCode", code)
                .putc("auth_token_type", "CHECK_CODE");
    }

    /** Returns false if no passwordChallenge present, means packet you try to deserialize is probably LoginPacket. */
    @Override
    public boolean deserialize(MapContainer data) {
        if (!super.deserialize(data)) return false;
        trackId = data.getc("passwordChallenge").getString("trackId");
        return allNotNull(trackId);
    }
}
