package com.doktorthe2nd.nyax.luaj;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.HashMap;
import java.util.Map;

/** Not thread-safe, even in different instances (uses shared cache) */
class ExecutableScript {
    private static final Map<String, ExecutableScript> CACHED = new HashMap<>();

    public static boolean cache_has_module(String module) {
        return CACHED.containsKey(module);
    }
    public static LuaValue from_cache(String module) {
        ExecutableScript cached_scr = CACHED.get(module);
        if (cached_scr == null)
            throw new LuaException("Module '"+module+"' is not cached, but was queried. Report this to developer.");
        return cached_scr.run_with_cache();
    }
    public static LuaValue run_with_cache(ExecutableScript script) {
        ExecutableScript cached_scr = CACHED.get(script.module_id);
        if (cached_scr != null) return cached_scr.run_with_cache();
        return script.run_with_cache();
    }
    public static LuaValue run_no_cache(ExecutableScript script) {
        return script.run_no_cache();
    }

    private LuaValue cached = LuaValue.NIL;

    private final LuaValue script;

    protected final LuajEngine parent;
    protected final Metadata meta;
    protected final String module_id;

    private ExecutableScript(LuajEngine parent, String data, String module, Metadata meta) {
        this.parent = parent;
        this.meta = meta;
        this.module_id = module;

        LuaTable env = new LuaTable();
        LuaTable envMeta = new LuaTable();

        Require require;
        if (meta.require_trusted) {
            require = new Require(this, true);
            envMeta.set(LuaValue.INDEX, parent.trusted_globals);
        }
        else {
            require = new Require(this, meta.getIdList());
            envMeta.set(LuaValue.INDEX, parent.restricted_globals);
        }

        env.set("require", require);

        env.setmetatable(envMeta);

        if (meta.require_trusted) this.script = parent.trusted_globals.load(data, module_id, env);
        else this.script = parent.restricted_globals.load(data, module_id, env);

        CACHED.put(module, this);
    }

    protected ExecutableScript(LuajEngine engine, LuaFunction script) {
        this.parent = engine;
        this.meta = new Metadata();
        this.meta.name = "Event subscribe";
        this.module_id = "(event subscribe)";
        this.script = script;
    }

    static ExecutableScript of(LuajEngine engine, Script script) {
        if (CACHED.containsKey(script.module_id)) return CACHED.get(script.module_id);
        return new ExecutableScript(engine, script.data, script.module_id, script.meta);
    }

    public void invoke(Varargs args) {
        script.invoke(args);
    }

    private LuaValue run() {
        LuaValue result = script.call();
        return result != LuaValue.NIL ? result : LuaValue.TRUE;
    }

    public LuaValue run_no_cache() {
        LuaValue result = run();
        cached = result;
        return result;
    }

    public LuaValue run_with_cache() {
        if (cached != LuaValue.NIL) return cached;
        return run_no_cache();
    }

    public LuaException exception(String message) {
        return new LuaException("Exception in module '"+meta.name+"' ("+ module_id +"): "+message);
    }
}
