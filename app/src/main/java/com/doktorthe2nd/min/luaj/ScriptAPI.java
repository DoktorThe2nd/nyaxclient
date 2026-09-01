package com.doktorthe2nd.min.luaj;

import android.app.Activity;
import android.content.Context;

import org.luaj.vm2.lib.ZeroArgFunction;

public interface ScriptAPI {
    Context getAppContext();
    Activity getActivity();
    boolean isActivityAlive();
}
