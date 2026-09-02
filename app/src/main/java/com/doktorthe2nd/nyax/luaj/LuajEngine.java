package com.doktorthe2nd.nyax.luaj;

import org.jetbrains.annotations.NotNull;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.compiler.LuaC;
import org.luaj.vm2.lib.BaseLib;
import org.luaj.vm2.lib.Bit32Lib;
import org.luaj.vm2.lib.CoroutineLib;
import org.luaj.vm2.lib.MathLib;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.StringLib;
import org.luaj.vm2.lib.TableLib;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

class LuajEngine {
    private final BlockingQueue<Event> events = new LinkedBlockingQueue<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    public void setRunning(boolean running) {
        this.running.set(running);
    }

    private final ConcurrentMap<String, List<ExecutableScript>> subscribers = new ConcurrentHashMap<>();

    protected final Globals trusted_globals = JsePlatform.standardGlobals();
    protected final Globals restricted_globals = new Globals();

    /**
     * Thread-safe. Should be accessible from anywhere
     * @param name event name
     * @param args arguments
     */
    public void push_event(String name, Varargs args) {
        events.add(new Event(name.toLowerCase(), args));
    }

    /**
     * Thread-safe. Should be accessible from anywhere
     * @param event_name name of event to subscribe
     * @param function Lua function to call on event
     */
    public void add_subscriber(String event_name, LuaFunction function) {
        String norm_name = event_name.toLowerCase();
        List<ExecutableScript> subs = this.subscribers.get(norm_name);
        if (subs == null) {
            subs = new ArrayList<>();
            this.subscribers.put(norm_name, subs);
        }
        subs.add(new ExecutableScript(this, function));
    }

    /**
     * Main engine loop. Should be called in its own thread. Throws only LuaException
     * @throws LuaException if any exception (excluding InterruptedException) occurred
     */
    public void loop() throws LuaException {
        try {
            while (running.get()) {
                Event event = events.take();
                var subscribers = this.subscribers.get(event.name);
                if (subscribers == null) continue;
                for (var item : subscribers) item.invoke(event.args);
            }
        } catch (InterruptedException e) {
            running.set(false);
        } catch (LuaException e) {
            throw e;
        } catch (Exception e) {
            throw new LuaException(e.toString());
        }
    }

    private interface EventsAPI {
        void call(String name, Varargs args);
        void subscribe(String name, LuaFunction function);
    }

    public LuajEngine(@NotNull ScriptAPI api) {
        restricted_globals.load(new BaseLib());
        restricted_globals.load(new PackageLib());
        restricted_globals.load(new TableLib());
        restricted_globals.load(new Bit32Lib());
        restricted_globals.load(new StringLib());
        restricted_globals.load(new MathLib());
        restricted_globals.load(new CoroutineLib());

        restricted_globals.set("os", LuaValue.NIL);
        restricted_globals.set("io", LuaValue.NIL);
        restricted_globals.set("debug", LuaValue.NIL);
        restricted_globals.set("package", LuaValue.NIL);
        restricted_globals.set("loadlib", LuaValue.NIL);
        restricted_globals.set("dofile", LuaValue.NIL);
        restricted_globals.set("loadfile", LuaValue.NIL);

        trusted_globals.set("api", CoerceJavaToLua.coerce(api));
        trusted_globals.set("events_api", CoerceJavaToLua.coerce(new EventsAPI() {
            @Override
            public void call(String name, Varargs args) {
                push_event(name, args);
            }
            @Override
            public void subscribe(String name, LuaFunction function) {
                add_subscriber(name, function);
            }
        }));
        trusted_globals.set("events_ids", CoerceJavaToLua.coerce(Events.class));

        Events.addBasicSubscribers(this);

        restricted_globals.set("_G", restricted_globals);
        LuaC.install(restricted_globals);
    }
}
