package com.doktorthe2nd.nyax.luaj;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

public class Event {
    public final String name;
    public final LuaTable args;

    public Event(String name, LuaTable args) {
        this.name = name;
        this.args = args;
    }
    public Event(String name, LuaValue... args) {
        this(name, LuaValue.listOf(args));
    }
}
