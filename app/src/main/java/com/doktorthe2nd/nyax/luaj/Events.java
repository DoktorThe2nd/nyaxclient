package com.doktorthe2nd.nyax.luaj;

import com.doktorthe2nd.nyax.Consts;
import com.doktorthe2nd.nyax.MainActivity;
import com.doktorthe2nd.nyax.luaj.loaders.LuaFromAssetsLoader;
import com.doktorthe2nd.nyax.luaj.loaders.LuaFromImportedLoader;
import com.doktorthe2nd.nyax.luaj.loaders.LuaFromTrustedLoader;
import com.doktorthe2nd.nyax.net.Packet;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.io.InputStream;
import java.util.UUID;

public class Events {
    public static final String STARTUP = "nyax:startup";
    public static final String SOCKET_OPENED = "nyax:socket_opened";
    public static final String SOCKET_CLOSED = "nyax:socket_closed";
    public static final String UNHANDLED_PACKET = "nyax:unhandled_packet";

    private static final String _RUN_MODULE = UUID.randomUUID().toString();
    private static final String _RUN_ON_REPLY = UUID.randomUUID().toString();

    /** Prefers built-in directories. Thread-safe. Runs module on next event cycle (not immediately) */
    protected static void runModule(String module) {
        MainActivity.luajThread.callEvent(_RUN_MODULE, LuaValue.valueOf(module));
    }

    protected static void runOnReply(LuaFunction function, Packet packet) {
        MainActivity.luajThread.callEvent(_RUN_ON_REPLY, LuaValue.varargsOf(function, CoerceJavaToLua.coerce(packet)));
    }

    private static String findScript(String name) {
        String relativePath = name.replace('.', '/') + ".lua";
        InputStream fis2 = new LuaFromTrustedLoader().findResource(relativePath);
        if (fis2 != null) return Consts.readInputStream(fis2);
        InputStream fis3 = new LuaFromAssetsLoader().findResource(relativePath);
        if (fis3 != null) return Consts.readInputStream(fis3);
        InputStream fis = new LuaFromImportedLoader().findResource(relativePath);
        if (fis != null) return Consts.readInputStream(fis);
        return null;
    }

    static void addBasicSubscribers(LuajEngine engine) {
        engine.add_subscriber(_RUN_MODULE, new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() < 1) throw new LuaException("RunModule: Got no module name");
                String name = args.arg1().checkjstring();
                System.out.println("RunModule got '"+name+"'");
                String scr = findScript(name);
                if (scr == null) throw new LuaException("RunModule: Module '"+name+"' not found");
                ExecutableScript executableScript = ExecutableScript.of(engine, new Script(name, scr));
                return ExecutableScript.run_with_cache(executableScript);
            }
        });
        engine.add_subscriber(_RUN_ON_REPLY, new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if (args.narg() < 2) throw new LuaException("RunOnReply: Got wrong number of args");
                LuaFunction func = args.arg(1).checkfunction();
                LuaValue packet = args.arg(2).checknotnil();
                return func.call(packet);
            }
        });
    }
}
