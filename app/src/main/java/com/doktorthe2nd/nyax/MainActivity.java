package com.doktorthe2nd.nyax;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.doktorthe2nd.nyax.luaj.Events;
import com.doktorthe2nd.nyax.luaj.LuajThread;
import com.doktorthe2nd.nyax.luaj.ScriptAPI;
import com.doktorthe2nd.nyax.net.OnReply;

import org.luaj.vm2.LuaFunction;

import java.lang.ref.WeakReference;

public class MainActivity extends Activity {
    public interface RunOnUi {
        void run(Runnable runnable);
    }

    public static Context appContext;
    public static RunOnUi runOnUi;
    private static WeakReference<MainActivity> weakActivity;
    public static boolean weakDead() {
        return weakActivity.get() == null;
    }
    public static MainActivity getWeak() {
        return weakActivity.get();
    }

    public static final LuajThread luajThread = new LuajThread(new ScriptAPI() {
        public Context getAppContext() {
            return appContext;
        }

        public Activity getActivity() {
            return getWeak();
        }

        public boolean isActivityAlive() {
            return !weakDead();
        }

        public Class<?> findGlobalClass(String name) throws ClassNotFoundException {
            return Class.forName(name, true, this.getClass().getClassLoader());
        }

        public Class<?> findClass(String name) throws ClassNotFoundException  {
            try {
                return findGlobalClass("com.doktorthe2nd.nyax.luajobjs."+name);
            } catch (ClassNotFoundException e) {
                try {
                    return findGlobalClass("com.doktorthe2nd.nyax.types."+name);
                } catch (ClassNotFoundException ex) {
                    return findGlobalClass(name);
                }
            }
        }

        public Class<?> findPacketClass(String name) throws ClassNotFoundException {
            return findGlobalClass("com.doktorthe2nd.nyax.types.packets."+name);
        }

        public OnReply makeOnReply(LuaFunction function) {
            return packet -> luajThread.runOnReply(function, packet);
        }
    });

    @Override
    protected void onResume() {
        super.onResume();
        weakActivity = new WeakReference<>(this);
    }

    /*@Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appContext = getApplicationContext();
        runOnUi = this::runOnUiThread;
        weakActivity = new WeakReference<>(this);

        setContentView(R.layout.luaj_startup);

        luajThread.start();
        luajThread.loadScripts();
        luajThread.callEvent(Events.STARTUP);
    }
}