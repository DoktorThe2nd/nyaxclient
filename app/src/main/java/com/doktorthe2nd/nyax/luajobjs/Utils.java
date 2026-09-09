package com.doktorthe2nd.nyax.luajobjs;

import android.view.View;

import com.doktorthe2nd.nyax.types.MapContainer;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.util.ArrayList;
import java.util.HashMap;

public class Utils {
    @FunctionalInterface
    public interface Coercer<T> {
        T coerce(LuaValue value);

        static <T,S> Coercer<T> cast(Coercer<S> source, Class<T> to) {
            return value -> {
                S result = source.coerce(value);
                if (!to.isInstance(result)) return null;
                return to.cast(result);
            };
        }

        Coercer<Object> USERDATA = LuaValue::touserdata;
        Coercer<Integer> INTEGER = LuaValue::toint;
        Coercer<Double> DOUBLE = LuaValue::todouble;
        Coercer<Boolean> BOOLEAN = LuaValue::toboolean;
        Coercer<String> STRING = LuaValue::tojstring;
        Coercer<Byte> BYTE = LuaValue::tobyte;
        Coercer<Float> FLOAT = LuaValue::tofloat;
        Coercer<Long> LONG = LuaValue::tolong;
        Coercer<Character> CHAR = LuaValue::tochar;
        Coercer<Short> SHORT = LuaValue::toshort;
        Coercer<View> VIEW = cast(USERDATA, View.class);
        Coercer<Runnable> RUNNABLE = value -> {
            if (!value.isfunction()) return null;
            return value::call;
        };
        Coercer<Object> OBJECT = value -> {
            if (value.isuserdata()) return USERDATA.coerce(value);
            if (value.isboolean()) return BOOLEAN.coerce(value);
            if (value.isint()) return INTEGER.coerce(value);
            if (value.isnumber()) return DOUBLE.coerce(value);
            if (value.isstring()) return STRING.coerce(value);
            if (value.islong()) return LONG.coerce(value);
            if (value.isfunction()) return RUNNABLE.coerce(value);
            return null;
        };
    }

    public static <T> ArrayList<T> coerceToArrayList(LuaTable table, Coercer<T> coercer) {
        ArrayList<T> list = new ArrayList<>();
        for (int i = 1; i <= table.length(); i++)
            list.add(coercer.coerce(table.get(i)));
        return list;
    }

    public static <K,V> HashMap<K,V> coerceToHashMap(LuaTable table, Coercer<K> key_coercer, Coercer<V> value_coercer) {
        HashMap<K, V> map = new HashMap<>();
        LuaValue key = LuaValue.NIL;
        while (true) {
            Varargs pair = table.next(key);
            LuaValue new_key = pair.arg1();
            if (new_key.isnil()) break;
            LuaValue new_value = pair.arg(2);
            map.put(key_coercer.coerce(new_key), value_coercer.coerce(new_value));
            key = new_key;
        }
        return map;
    }

    public static MapContainer coerceToMapContainer(LuaTable table) {
        return MapContainer.of(coerceToHashMap(table, Coercer.USERDATA, Coercer.USERDATA));
    }
}
