package com.doktorthe2nd.nyax.luajobjs;

import android.view.View;

import com.doktorthe2nd.nyax.types.MapContainer;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils {
    @FunctionalInterface
    public interface Coercer<T> {
        T coerce(LuaValue value);

        static <T,S> Coercer<T> cast(Coercer<S> source, Class<T> to) {
            return value -> {
                S result = source.coerce(value);
                if (result == null || !to.isInstance(result)) return null;
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
        Coercer<Runnable> FUNCTION = value -> {
            if (!value.isfunction()) return null;
            return value::call;
        };
        Coercer<Object> OBJECT = value -> {
            if (value.isboolean()) return BOOLEAN.coerce(value);
            if (value.isint()) return INTEGER.coerce(value);
            if (value.isnumber()) return DOUBLE.coerce(value);
            if (value.isstring()) return STRING.coerce(value);
            if (value.islong()) return LONG.coerce(value);
            if (value.isfunction()) return FUNCTION.coerce(value);
            return USERDATA.coerce(value);
        };
    }

    public static <T> ArrayList<T> coerceToArrayList(LuaTable table, Coercer<T> coercer) {
        ArrayList<T> list = new ArrayList<>();
        for (int i = 1; i <= table.length(); i++) {
            var res = coercer.coerce(table.get(i));
            if (res != null) list.add(res);
        }
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
            var jkey = key_coercer.coerce(new_key);
            var jvalue = value_coercer.coerce(new_value);
            if (jkey != null && jvalue != null) map.put(jkey, jvalue);
            key = new_key;
        }
        return map;
    }

    public static MapContainer coerceToMapContainer(LuaTable table) {
        return MapContainer.of(coerceToHashMap(table, Coercer.OBJECT, Coercer.OBJECT));
    }

    public static LuaTable coerceList(List<Object> list) {
        LuaTable table = new LuaTable();
        for (int i = 0; i < list.size(); i++)
            table.set(i+1, CoerceJavaToLua.coerce(list.get(i)));
        table.set("n", list.size());
        return table;
    }
}
