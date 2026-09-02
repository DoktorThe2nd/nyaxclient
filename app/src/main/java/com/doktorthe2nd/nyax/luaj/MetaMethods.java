package com.doktorthe2nd.nyax.luaj;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.ArrayList;
import java.util.List;

class MetaMethods {
    public static final String REQUIRE = "require";
    public static final String GET_MODULE_ID = "getModuleId";

    private static final List<String> locked = new ArrayList<>() {{
        add(REQUIRE); add(GET_MODULE_ID);
    }};

    public static void applyLock(LuaTable env, LuaTable envMeta) {
        envMeta.set(LuaValue.NEWINDEX, new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue key, LuaValue value) {
                if (locked.contains(key.tojstring()))
                    return LuaValue.error("Attempt to redefine '"+key.tojstring()+"'");
                env.rawset(key, value);
                return LuaValue.NIL;
            }
        });
    }
}
