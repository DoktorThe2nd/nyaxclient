package com.doktorthe2nd.nyax.types.packets.session;

import com.doktorthe2nd.nyax.Consts;
import com.doktorthe2nd.nyax.net.OpcodeTable;
import com.doktorthe2nd.nyax.types.MapContainer;
import com.doktorthe2nd.nyax.types.packets.SendablePacket;

public class SessionInitPacket extends SendablePacket {
    private Long callsSeed;

    @Override
    public int getOpcode() {
        return OpcodeTable.sessionInit;
    }

    @Override
    public MapContainer serialize() {
        return super.serialize()
                .putc("mt_instanceid", Consts.instanceId)
                .putc("userAgent", Consts.getUserAgent())
                .putc("clientSessionId", Consts.clientSessionId)
                .putc("deviceId", Consts.deviceId);
    }

    @Override
    public boolean deserialize(MapContainer data) {
        if (!super.deserialize(data)) return false;
        callsSeed = data.getLong("callsSeed");
        return allNotNull(callsSeed);
    }

    public Long getCallsSeed() {
        return callsSeed;
    }
}
