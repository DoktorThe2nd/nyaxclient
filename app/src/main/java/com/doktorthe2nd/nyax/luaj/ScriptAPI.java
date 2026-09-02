package com.doktorthe2nd.nyax.luaj;

import android.app.Activity;
import android.content.Context;

public interface ScriptAPI {
    Context getAppContext();
    Activity getActivity();
    boolean isActivityAlive();
    Class<?> findClass(String name) throws ClassNotFoundException;
}
