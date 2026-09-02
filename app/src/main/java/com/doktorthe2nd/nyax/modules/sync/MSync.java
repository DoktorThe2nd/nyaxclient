package com.doktorthe2nd.nyax.modules.sync;

import com.doktorthe2nd.nyax.Consts;
import com.doktorthe2nd.nyax.types.MapContainer;
import com.doktorthe2nd.nyax.modules.MReporter;
import com.doktorthe2nd.nyax.modules.Profile;
import com.doktorthe2nd.nyax.modules.chat.MChats;
import com.doktorthe2nd.nyax.net.Connection;
import com.doktorthe2nd.nyax.net.OpcodeTable;

import java.util.HashMap;

public class MSync {
    public static void sendLogin(Runnable onReply) {
        // TODO: in pymax there is login2 function, so PROLLY it is needed. if login fails, think about that
        // https://github.com/MaxApiTeam/PyMax/blob/9885d79573ceb64bef7c37f750f5621c99ec4f5f/src/pymax/app.py#L211

        Connection.sendRequest(OpcodeTable.login, new HashMap<>(){{
            put("userAgent", Consts.getUserAgent());
            put("token", Consts.currentSession.token);
            put("chatCacheFingerprint", Consts.getFingerprint());
            put("chatsSync", Consts.currentSession.sync.chats_sync);
            put("contactsSync", Consts.currentSession.sync.contacts_sync);
            put("draftsSync", Consts.currentSession.sync.drafts_sync);
            put("interactive", true);
            put("presenceSync", Consts.currentSession.sync.presence_sync);
            put("exp", new HashMap<>(){{
                put("chatsCountGroups", new byte[]{10, 50}); // magic
            }});
            put("configHash", Consts.currentSession.sync.config_hash);
        }}, packet -> {
            if (MReporter.toastIfError(packet)) return;
            MapContainer map = new MapContainer(packet.payload);
            var prof = map.getMap("profile");
            if (prof == null) {
                MReporter.toastError("No profile in answer");
                return;
            }
            Profile.myProfile.setData(prof);
            var chats = map.getMapsArray("chats");
            if (chats == null) {
                MReporter.toastError("No chats in answer");
                return;
            }
            MChats.addFromLogin(chats);
            onReply.run();
        });
    }
}
