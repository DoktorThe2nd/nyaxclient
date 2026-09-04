package com.doktorthe2nd.nyax.luaj;

import android.app.Activity;
import android.content.Context;

import com.doktorthe2nd.nyax.net.OnReply;

import org.luaj.vm2.LuaFunction;

public interface ScriptAPI {
    Context getAppContext();
    Activity getActivity();
    boolean isActivityAlive();
    Class<?> findClass(String name) throws ClassNotFoundException;
    Class<?> findPacketClass(String name) throws ClassNotFoundException;
    OnReply onReplyProxy(LuaFunction function);
}
