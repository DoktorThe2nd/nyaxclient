package com.doktorthe2nd.min;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.doktorthe2nd.min.luaj.LuajThread;
import com.doktorthe2nd.min.luaj.ScriptAPI;
import com.doktorthe2nd.min.luajobjs.UIBuilder;
import com.doktorthe2nd.min.modules.MReporter;

import org.luaj.vm2.LuaValue;

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

        try {
            setContentView(R.layout.connecting);

            var layout = UIBuilder.makeLayout(false);
            layout.addView(UIBuilder.makeText("test 1234"));
            layout.addView(UIBuilder.makeButton("test", "none", LuaValue.NIL));
            if (!UIBuilder.setContentView(layout)) Toast.makeText(this, "shit", Toast.LENGTH_SHORT).show();

            /*luajThread.start();
            luajThread.loadScripts();
            luajThread.callEvent(Events.STARTUP, LuaValue.NIL);*/
        } catch (RuntimeException e) {
            MReporter.makeErrorScreen(this, e.getMessage());
        }
    }
}