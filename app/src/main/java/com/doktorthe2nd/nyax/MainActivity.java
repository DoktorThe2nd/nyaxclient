package com.doktorthe2nd.nyax;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.doktorthe2nd.nyax.luaj.Events;
import com.doktorthe2nd.nyax.luaj.LuaProxyFactory;
import com.doktorthe2nd.nyax.luaj.LuajThread;
import com.doktorthe2nd.nyax.luaj.ScriptAPI;
import com.doktorthe2nd.nyax.net.OnReply;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.lang.ref.WeakReference;
import java.util.UUID;

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
        @Override
        public Context getAppContext() {
            return appContext;
        }

        @Override
        public Activity getActivity() {
            return getWeak();
        }

        @Override
        public boolean isActivityAlive() {
            return !weakDead();
        }

        @Override
        public Class<?> findClass(String name) throws ClassNotFoundException  {
            try {
                return Class.forName("com.doktorthe2nd.nyax.luajobjs."+name, true, this.getClass().getClassLoader());
            } catch (ClassNotFoundException e) {
                return Class.forName(name, true, this.getClass().getClassLoader());
            }
        }

        @Override
        public Class<?> findPacketClass(String name) throws ClassNotFoundException {
            return Class.forName("com.doktorthe2nd.nyax.types.packets."+name, true, this.getClass().getClassLoader());
        }

        @Override
        public OnReply makeOnReply(LuaFunction function) {
            String replyId = UUID.randomUUID().toString();
            luajThread.addEventSubscriber(replyId, function);
            return packet -> luajThread.callEvent(replyId, CoerceJavaToLua.coerce(packet));
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