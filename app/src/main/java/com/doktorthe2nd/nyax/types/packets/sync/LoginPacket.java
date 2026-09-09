package com.doktorthe2nd.nyax.types.packets.sync;

import com.doktorthe2nd.nyax.Consts;
import com.doktorthe2nd.nyax.net.OpcodeTable;
import com.doktorthe2nd.nyax.types.MapContainer;
import com.doktorthe2nd.nyax.types.Profile;
import com.doktorthe2nd.nyax.types.packets.SendablePacket;

import java.util.List;
import java.util.Map;

public class LoginPacket extends SendablePacket {
    @Override
    public int getOpcode() {
        return OpcodeTable.login;
    }

    @Override
    public MapContainer serialize() {
        return super.serialize()
                .putc("userAgent", Consts.getUserAgent())
                .putc("token", Consts.currentSession.token)
                .putc("chatCacheFingerprint", Consts.getFingerprint())
                .putc("chatsSync", Consts.currentSession.sync.chats_sync)
                .putc("contactsSync", Consts.currentSession.sync.contacts_sync)
                .putc("draftsSync", Consts.currentSession.sync.drafts_sync)
                .putc("presenceSync", Consts.currentSession.sync.presence_sync)
                .putc("exp", new MapContainer().putc("chatsCountGroups", new byte[]{10, 50}))
                .putc("configHash", Consts.currentSession.sync.config_hash);
    }

    private Map<Object, Object> myProfile;
    public Map<Object, Object> getMyProfileData() {
        return myProfile;
    }
    private List<Map<Object, Object>> chats;
    public List<Map<Object, Object>> getChatsData() {
        return chats;
    }

    @Override
    public boolean deserialize(MapContainer data) {
        if (!super.deserialize(data)) return false;
        myProfile = data.getMap("profile");
        chats = data.getMapsArray("chats");
        return allNotNull(myProfile, chats);
    }
}
