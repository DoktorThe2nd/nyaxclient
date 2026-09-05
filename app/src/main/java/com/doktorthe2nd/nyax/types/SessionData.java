package com.doktorthe2nd.nyax.types;

import android.content.Context;
import android.content.SharedPreferences;

import com.doktorthe2nd.nyax.MainActivity;
import com.doktorthe2nd.nyax.modules.sync.SyncState;

import java.util.UUID;

public class SessionData {
    public String token = null;
    public String deviceId;
    public String phone;
    public String mt_instanceid;
    public SyncState sync = new SyncState();

    public boolean hasToken() {
        return token != null;
    }

    public static SessionData spoofedSession() {
        SessionData ret = new SessionData();
        ret.token = null;
        ret.deviceId = UUID.randomUUID().toString();
        ret.phone = null;
        ret.mt_instanceid = UUID.randomUUID().toString();
        return ret;
    }

    public static void saveSession(int slot, SessionData session) {
        SharedPreferences.Editor sharedPref = MainActivity.appContext
                .getSharedPreferences("session_data_"+slot, Context.MODE_PRIVATE).edit();
        sharedPref.putString("token", session.token);
        sharedPref.putString("deviceId", session.deviceId);
        sharedPref.putString("phone", session.phone);
        sharedPref.putString("mt_instanceid", session.mt_instanceid);
        sharedPref.putInt("sync-chats_sync", session.sync.chats_sync);
        sharedPref.putInt("sync-contacts_sync", session.sync.contacts_sync);
        sharedPref.putInt("sync-drafts_sync", session.sync.drafts_sync);
        sharedPref.putInt("sync-presence_sync", session.sync.presence_sync);
        sharedPref.putString("sync-config_hash", session.sync.config_hash);
        sharedPref.apply();
    }

    public static boolean isSessionSaved(int slot) {
        SharedPreferences sharedPref = MainActivity.appContext
                .getSharedPreferences("session_data_"+slot, Context.MODE_PRIVATE);
        return sharedPref.contains("token");
    }

    public static SessionData loadSession(int slot) {
        SharedPreferences sharedPref = MainActivity.appContext
                .getSharedPreferences("session_data_"+slot, Context.MODE_PRIVATE);
        if (!sharedPref.contains("token")) return null;
        SessionData session = new SessionData();
        session.token = sharedPref.getString("token", "");
        session.deviceId = sharedPref.getString("deviceId", "");
        session.phone = sharedPref.getString("phone", "");
        session.mt_instanceid = sharedPref.getString("mt_instanceid", "");
        session.sync.chats_sync = sharedPref.getInt("sync-chats_sync", -1);
        session.sync.contacts_sync = sharedPref.getInt("sync-contacts_sync", -1);
        session.sync.drafts_sync = sharedPref.getInt("sync-drafts_sync", -1);
        session.sync.presence_sync = sharedPref.getInt("sync-presence_sync", -1);
        session.sync.config_hash = sharedPref.getString("sync-config_hash", "NULL_HASH");
        return session;
    }
}
